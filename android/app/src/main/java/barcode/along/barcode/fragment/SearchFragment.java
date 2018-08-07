package barcode.along.barcode.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import barcode.along.barcode.Api.ApiService;
import barcode.along.barcode.Api.HttpObserver;
import barcode.along.barcode.CaptureActivity;
import barcode.along.barcode.R;
import barcode.along.barcode.Utils.App;
import barcode.along.barcode.Utils.CommonFunc;
import barcode.along.barcode.Utils.MessageAdapter;
import barcode.along.barcode.Utils.ToastUtil;
import barcode.along.barcode.bean.ComMessageBean;
import barcode.along.barcode.bean.QueryCmdBean;
import barcode.along.barcode.bean.QueryResultBean;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;


public class SearchFragment extends Fragment {
    private View view;
    private RadioButton mbtableRb;
    private RadioButton mpackingRb;
    private ImageView btnsearch;
    private EditText txt_key;
    private TextView result_title;
    private FloatingActionButton fabtn;
    private CardView headercard;
    private CardView contencard;
    private RelativeLayout lay_radio;
    private RelativeLayout lay_search;
    private SwipeRefreshLayout swipeRefreshLayout;
    MessageAdapter adapter;
    private int _page = 1;
    private int resultcnt = 0;
    private static final int _num = 10;
    private boolean hasmore = true;
    private int lastVisibleItem;


    private List<ComMessageBean> comMessageBeans;

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
        //moutboundRb = (RadioButton) view.findViewById(R.id.db_outbound);
        btnsearch = (ImageView) view.findViewById(R.id.btn_search);
        txt_key = (EditText) view.findViewById(R.id.et_searchkey);
        result_title = (TextView) view.findViewById(R.id.detail_title);
        fabtn = (FloatingActionButton) view.findViewById(R.id.fab);
        headercard = (CardView) view.findViewById(R.id.card_header);
        contencard = (CardView) view.findViewById(R.id.card_content);
        lay_radio = (RelativeLayout) view.findViewById(R.id.lay_radio);
        lay_search = (RelativeLayout) view.findViewById(R.id.lay_search);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setEnabled(false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(App.INSTANCE,LinearLayoutManager.VERTICAL,false);

        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        comMessageBeans = new ArrayList<>();
        adapter = new MessageAdapter(comMessageBeans);
        recyclerView.setAdapter(adapter);

        btnsearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                _page = 1;
                searchrecords(false);
            }
        });

        fabtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.INSTANCE, CaptureActivity.class);
                startActivityForResult(intent,0);
                _page = 1;

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    if(hasmore){
                        _page += 1;
                        searchrecords(true);

                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
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
                _page = 1;
                searchrecords(false);
            }
        }
    }

    private void searchrecords(boolean flag){
        Gson gson = new Gson();
        QueryCmdBean querycmd = new QueryCmdBean();
        querycmd.setData(txt_key.getText().toString());
        querycmd.setNumber(_num);
        querycmd.setPage(_page);
        if(mbtableRb.isChecked()){
            querycmd.setQuerycmd(0);
        }else if(mpackingRb.isChecked()){
            querycmd.setQuerycmd(1);
        }else{
            querycmd.setQuerycmd(2);
        }
                /*result_title.setText("查询到2条数据.");
                comMessageBeans = new ArrayList<>();
                ComMessageBean messageBean = new ComMessageBean();
                messageBean.setMac("3071B2000001");
                messageBean.setSn("1252222220");
                messageBean.setOptdate("2018-08-04");
                messageBean.setOrderid("SCDD201804240001");
                comMessageBeans.add(messageBean);

                messageBean = new ComMessageBean();
                messageBean.setMac("3071B2000002");
                messageBean.setSn("1252222221");
                messageBean.setOptdate("2018-08-04");
                messageBean.setOrderid("SCDD201804240002");
                comMessageBeans.add(messageBean);
                MessageAdapter adapter = new MessageAdapter(comMessageBeans);
                recyclerView.setAdapter(adapter);
                */
        ApiService.getApiService().getProduct(new HttpObserver<QueryResultBean>() {
            @Override
            public void onNext(QueryResultBean resultBean) {
                if(!flag){
                    result_title.setText("查询到" + resultBean.getResultcnt() + "条数据.");
                    if(resultBean.getResultcnt() > 0){
                        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) headercard.getLayoutParams();
                        linearParams.weight = 1;
                        headercard.setLayoutParams(linearParams);
                        linearParams =(LinearLayout.LayoutParams) contencard.getLayoutParams();
                        linearParams.weight = 3;
                        contencard.setLayoutParams(linearParams);
                        linearParams =(LinearLayout.LayoutParams) lay_radio.getLayoutParams();
                        linearParams.height = ((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, getResources().getDisplayMetrics()));
                        lay_radio.setLayoutParams(linearParams);
                        linearParams =(LinearLayout.LayoutParams) lay_search.getLayoutParams();
                        linearParams.height = ((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, getResources().getDisplayMetrics()));
                        lay_search.setLayoutParams(linearParams);
                    }
                    comMessageBeans.clear();
                }

                resultcnt = resultBean.getResultcnt();
                if(resultBean.getResult() != null){
                    hasmore = true;
                    List<ComMessageBean> messageBeans = CommonFunc.fromJsonList(resultBean.getResult().toString(), ComMessageBean.class);

                    for (ComMessageBean message:messageBeans) {
                        comMessageBeans.add(message);
                    }
                    adapter.changeMoreStatus(MessageAdapter.LOADING_MORE);
                    //adapter.notifyDataSetChanged();
                    //判断是否已加载完
                    if(adapter.getItemCount()-1 == resultcnt){
                        hasmore = false;
                        adapter.changeMoreStatus(MessageAdapter.LOADING_END);
                    }
                }else{
                    hasmore = false;
                    adapter.changeMoreStatus(MessageAdapter.LOADING_END);
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
        },gson.toJson(querycmd));
    }


}
