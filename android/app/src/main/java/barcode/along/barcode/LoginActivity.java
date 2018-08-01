package barcode.along.barcode;

import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import barcode.along.barcode.Api.ApiService;
import barcode.along.barcode.Api.HttpObserver;
import barcode.along.barcode.Utils.App;
import barcode.along.barcode.Utils.Base64Utils;
import barcode.along.barcode.Utils.SharedPreferencesUtils;
import barcode.along.barcode.Utils.ToastUtil;
import barcode.along.barcode.bean.UserBean;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    SharedPreferencesUtils helper = new SharedPreferencesUtils(App.INSTANCE, "setting");
    private LoadingDialog mLoadingDialog; //显示正在加载的对话框
    //布局内的控件
    private EditText et_name;
    private EditText et_password;
    private Button mLoginBtn;
    private CheckBox checkBox_password;
    private CheckBox checkBox_login;
    private ImageView iv_see_password;

    @Override
    public int initContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initUIAndListener() {
        initViews();
        setupEvents();
    }

    @Override
    protected void initData() {
        //判断用户第一次登陆
        if (firstLogin()) {
            checkBox_password.setChecked(false);//取消记住密码的复选框
            checkBox_login.setChecked(false);//取消自动登录的复选框
        }else{
            //判断是否记住密码
            if (remenberPassword()) {
                checkBox_password.setChecked(true);//勾选记住密码
                setTextNameAndPassword();//把密码和账号输入到输入框中
            } else {
                et_name.setText(helper.getString("username"));
            }

            //判断是否自动登录
            if (helper.getBoolean("autoLogin", false)) {
                checkBox_login.setChecked(true);
                login();//去登录就可以

            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                loadUserName();    //无论如何保存一下用户名
                login(); //登陆
                break;
            case R.id.iv_see_password:
                setPasswordVisibility();    //改变图片并设置输入框的文本可见或不可见
                break;

        }
    }

    /**
     * CheckBox点击时的回调方法 ,不管是勾选还是取消勾选都会得到回调
     *
     * @param buttonView 按钮对象
     * @param isChecked  按钮的状态
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBox_password) {  //记住密码选框发生改变时
            if (!isChecked) {   //如果取消“记住密码”，那么同样取消自动登陆
                checkBox_login.setChecked(false);
            }
        } else if (buttonView == checkBox_login) {   //自动登陆选框发生改变时
            if (isChecked) {   //如果选择“自动登录”，那么同样选中“记住密码”
                checkBox_password.setChecked(true);
            }
        }
    }

    private void initViews() {
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        et_name = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        checkBox_password = (CheckBox) findViewById(R.id.checkBox_password);
        checkBox_login = (CheckBox) findViewById(R.id.checkBox_login);
        iv_see_password = (ImageView) findViewById(R.id.iv_see_password);
    }

    private void setupEvents() {
        mLoginBtn.setOnClickListener(this);
        checkBox_password.setOnCheckedChangeListener(this);
        checkBox_login.setOnCheckedChangeListener(this);
        iv_see_password.setOnClickListener(this);

    }

    /**
     * 判断是否是第一次登陆
     */
    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        boolean first = helper.getBoolean("first", true);
        if (first) {
            //创建一个ContentVa对象（自定义的）设置不是第一次登录，,并创建记住密码和自动登录是默认不选，创建账号和密码为空
            helper.putValues(new SharedPreferencesUtils.ContentValue("first", false),
                    new SharedPreferencesUtils.ContentValue("remenberPassword", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("username", ""),
                    new SharedPreferencesUtils.ContentValue("password", ""));
            return true;
        }
        return false;
    }

    private boolean remenberPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        boolean remenberPassword = helper.getBoolean("remenberPassword", false);
        return remenberPassword;
    }

    public void setTextNameAndPassword() {
        et_name.setText(helper.getString("username"));
        et_password.setText(Base64Utils.getFromBase64(helper.getString("password")));
    }

    /**
     * 保存用户账号
     */
    public void loadUserName() {
        if (!et_name.getText().equals("") || !et_name.getText().equals("请输入登录账号")) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("username", et_name.getText()));
        }

    }

    /**
     * 设置密码可见和不可见的相互转换
     */
    private void setPasswordVisibility() {
        if (iv_see_password.isSelected()) {
            iv_see_password.setSelected(false);
            //密码不可见
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            iv_see_password.setSelected(true);
            //密码可见
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

    }

    private void login() {

        //先做一些基本的判断，比如输入的用户命为空，密码为空，网络不可用多大情况，都不需要去链接服务器了，而是直接返回提示错误
        if (et_name.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"你输入的账号为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_password.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"你输入的密码为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        showLoading();//显示加载框
        UserBean user = new UserBean();
        user.setUsername(et_name.getText().toString());
        user.setPassword(et_password.getText().toString());
        Gson gson = new Gson();

        ApiService.getApiService().auth(new HttpObserver<UserBean>() {
            @Override
            public void onNext(UserBean userBean) {
                Log.d("MainActivity", userBean.getUsername());
                loadCheckBoxState(checkBox_password, checkBox_login);
                mLoadingDialog.hide();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();//关闭页面
            }

            @Override
            public void onFinished() {
                //不做任何处理
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mLoadingDialog.hide();
            }

            @Override
            public void getDisposable(Disposable disposable) {
                addDisposable(disposable);
            }
        },gson.toJson(user));

    }

    /**
     * 保存按钮的状态值
     */
    public void loadCheckBoxState(CheckBox checkBox_password, CheckBox checkBox_login) {
        //如果设置自动登录
        if (checkBox_login.isChecked()) {
            //创建记住密码和自动登录是都选择,保存密码数据
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("remenberPassword", true),
                    new SharedPreferencesUtils.ContentValue("autoLogin", true),
                    new SharedPreferencesUtils.ContentValue("password", Base64Utils.getBase64(et_password.getText().toString())));

        } else if (!checkBox_password.isChecked()) { //如果没有保存密码，那么自动登录也是不选的
            //创建记住密码和自动登录是默认不选,密码为空
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("remenberPassword", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("password", ""));
        } else if (checkBox_password.isChecked()) {   //如果保存密码，没有自动登录
            //创建记住密码为选中和自动登录是默认不选,保存密码数据
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("remenberPassword", true),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("password", Base64Utils.getBase64(et_password.getText().toString())));
        }
    }

    /**
     * 显示加载的进度款
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, getString(R.string.loading), false);
        }
        mLoadingDialog.show();
    }


    /**
     * 隐藏加载的进度框
     */
    public void hideLoading() {
        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });

        }
    }

}
