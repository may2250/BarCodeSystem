package barcode.along.barcode.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import barcode.along.barcode.Api.ApiService;
import barcode.along.barcode.Api.HttpObserver;
import barcode.along.barcode.LoginActivity;
import barcode.along.barcode.MainActivity;
import barcode.along.barcode.R;
import barcode.along.barcode.bean.QueryCmdBean;
import barcode.along.barcode.bean.QueryResultBean;
import barcode.along.barcode.bean.UserBean;
import io.reactivex.disposables.Disposable;

public class SearchFragment extends Fragment {
    private View view;
    private RadioButton mbtableRb;
    private RadioButton mpackingRb;
    private RadioButton moutboundRb;
    private ImageView btnsearch;
    private TextView txt_key;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mbtableRb = (RadioButton) view.findViewById(R.id.db_btable);
        mpackingRb = (RadioButton) view.findViewById(R.id.db_packing);
        moutboundRb = (RadioButton) view.findViewById(R.id.db_outbound);
        btnsearch = (ImageView) view.findViewById(R.id.btn_search);
        txt_key = (TextView) view.findViewById(R.id.et_searchkey);
        btnsearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                QueryCmdBean querycmd = new QueryCmdBean();
                querycmd.setData(txt_key.getText().toString());
                if(mbtableRb.isChecked()){
                    querycmd.setQuerycmd(0);
                }else if(mpackingRb.isChecked()){
                    querycmd.setQuerycmd(1);
                }else{
                    querycmd.setQuerycmd(2);
                }
                ApiService.getApiService().getProduct(new HttpObserver<QueryResultBean>() {
                    @Override
                    public void onNext(QueryResultBean resultBean) {
                        Log.d("SearchFragment", "onNext: ");

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
                },gson.toJson(querycmd));
            }
        });
    }
}
