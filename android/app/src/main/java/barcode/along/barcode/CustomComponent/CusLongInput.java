package barcode.along.barcode.CustomComponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import barcode.along.barcode.R;

public class CusLongInput extends RelativeLayout{
    private EditText et;
    private ImageView img;

    public CusLongInput(Context context) {
        super(context);
    }

    public CusLongInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        int resourceId = -1;
        View view = LayoutInflater.from(context).inflate(R.layout.cuslong_input_layout, this, true);

        et = (EditText) view.findViewById(R.id.et);
        //给et添加输入内容变化的监听，控制清除按钮的显示与否
        et.addTextChangedListener(textWatcher);

        img = (ImageView) view.findViewById(R.id.imgid);

        //获取自定义参数，对自定义控件进行初始化设置
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CusLongInput);
        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                //若读到hint属性设置，否则走不到该case
                case R.styleable.CusLongInput_hint:
                    resourceId = ta.getResourceId(R.styleable.CusLongInput_hint, 0);
                    //若resourceId>0，说明读到hint设置为reference类型，则找到资源字段进行设置
                    //否则，说明读到为hint设置了String字段，获取从读到的字段中取出对应String设置给et
                    et.setHint(resourceId > 0 ? getResources().getText(resourceId) : ta.getString(R.styleable.CusLongInput_hint));
                    break;

                //若读到text属性设置，否则走不到该case
                case R.styleable.CusLongInput_text:
                    resourceId = ta.getResourceId(R.styleable.CusLongInput_text, 0);
                    et.setText(resourceId > 0 ? getResources().getText(resourceId) : ta.getString(R.styleable.CusLongInput_text));
                    break;

                //若没有设置cleanIconDrawable属性，则走不到该case，cleanIconDrawable显示ImageView中默认的图标
                case R.styleable.CusLongInput_IconDrawable:
                    resourceId = ta.getResourceId(R.styleable.CusLongInput_IconDrawable, 0);
                    img.setImageResource(resourceId > 0 ? resourceId : R.drawable.ic_launcher_background);
                    break;

                case R.styleable.CusLongInput_textColor:
                    resourceId = ta.getResourceId(R.styleable.CusLongInput_textColor, 0);
                    et.setTextColor(resourceId > 0 ? getResources().getColor(resourceId) : ta.getColor(R.styleable.CusLongInput_textColor, Color.BLACK));
                    break;

                case R.styleable.CusLongInput_textSize:
                    resourceId = ta.getResourceId(R.styleable.CusLongInput_textSize, 0);
                    et.setTextSize(resourceId > 0 ? getResources().getDimension(resourceId) : ta.getDimension(R.styleable.CusLongInput_textSize, 50));
                    break;

                case R.styleable.CusLongInput_maxLength:
//                    et.setMaxEms(ta.getInt(R.styleable.EditTextCanClean_maxLength, 0));
                    et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ta.getInt(R.styleable.CusLongInput_maxLength, 0))});
                    break;

                default:
                    break;
            }
        }
        ta.recycle();   //一定要对TypedArray资源进行释放
    }

    //设置Hint提示字符串的方法，以便通过Java代码进行设置
    protected void setHint(String string){
        et.setHint(string);
    }

    //设置编辑框显示文字的方法，以便通过Java代码进行设置
    protected void setText(String string) {
        et.setText(string);
    }

    //获取输入值
    protected String getText(){
        return et.getText().toString();
    }

    //设置图标，以便通过Java代码进行设置
    protected void setIcon(int drawableId) {
        Drawable drawable = getResources().getDrawable(drawableId);
        img.setImageDrawable(drawable);
    }

    //***重要
    //暴露出EditText，以便设置EditText的更多属性，如inputType属性等等，是为了增加灵活性和兼容性
    protected EditText getEditText(){
        return et;
    }


    //输入框文本变化监听器
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /*if (TextUtils.isEmpty(s)){
                img.setVisibility(View.INVISIBLE);
            }else {
                img.setVisibility(View.VISIBLE);
            }*/
        }
    };

}
