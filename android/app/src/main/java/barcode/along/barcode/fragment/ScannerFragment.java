package barcode.along.barcode.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeImageTransform;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import barcode.along.barcode.Api.ApiService;
import barcode.along.barcode.Api.HttpObserver;
import barcode.along.barcode.CaptureActivity;
import barcode.along.barcode.R;
import barcode.along.barcode.Utils.App;
import barcode.along.barcode.Utils.CommonFunc;
import barcode.along.barcode.Utils.MessageAdapter;
import barcode.along.barcode.bean.ComMessageBean;
import barcode.along.barcode.bean.HttpResult;
import barcode.along.barcode.bean.QueryCmdBean;
import barcode.along.barcode.bean.QueryResultBean;
import barcode.along.barcode.bean.TBodyBean;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;


public class ScannerFragment extends Fragment {
    private View view;
    private RadioButton mscRb;
    private ImageView btnsearch;
    private EditText txt_key;
    private EditText txt_softversion;
    private EditText txt_dst;
    private FloatingActionButton fabtn;
    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scanner, container, false);
        // Inflate the layout for this fragment
        final ViewGroup transitionsContainer = (ViewGroup) view.findViewById(R.id.scanner_contain);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mscRb = (RadioButton) view.findViewById(R.id.sc_mac);
        btnsearch = (ImageView) view.findViewById(R.id.btn_search);
        txt_key = (EditText) view.findViewById(R.id.et_key);
        txt_softversion = (EditText) view.findViewById(R.id.et_softversion);
        txt_dst = (EditText) view.findViewById(R.id.et_dst);
        fabtn = (FloatingActionButton) view.findViewById(R.id.fab);

        btnsearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String str = "";
                if(mscRb.isChecked()){
                    str = "mac|"+ txt_key.getText() + "|"+ txt_softversion.getText() + "|" + txt_dst.getText();
                }else{
                    str = "sn|"+ txt_key.getText() + "|"+ txt_softversion.getText() + "|" + txt_dst.getText();
                }
                outBoundDevice(str);
            }
        });

        fabtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.INSTANCE, CaptureActivity.class);
                startActivityForResult(intent,0);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent)
    {
        super.onActivityResult(requestCode,resultCode,intent);
        if(requestCode == 0 && resultCode == RESULT_OK){
            String result = intent.getExtras().getString("ResultQRCode");
            txt_key.setText(result);
            if(!result.isEmpty()){
                String[] devstrs = txt_key.getText().toString().split("\r\n");
                for (String devstr: devstrs) {
                    if(mscRb.isChecked()){
                        outBoundDevice("mac|"+ devstr + "|"+ txt_softversion.getText() + "|" + txt_dst.getText());
                    }else{
                        outBoundDevice("sn|"+ devstr + "|"+ txt_softversion.getText() + "|" + txt_dst.getText());
                    }
                }
            }
        }
    }

    private void outBoundDevice(String str){
        Gson gson = new Gson();
        TBodyBean tbody = new TBodyBean();

        tbody.setData(str);
        ApiService.getApiService().outBound(new HttpObserver<HttpResult>() {
            @Override
            public void onNext(HttpResult resultBean) {
                if(resultBean.status != 0){
                    
                }
            }

            @Override
            public void onFinished() {
                //不做任何处理
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void getDisposable(Disposable disposable) {
                //addDisposable(disposable);
            }
        },gson.toJson(tbody));
    }
}
