package barcode.along.barcode.Api;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import barcode.along.barcode.Utils.RequestInterceptor;
import barcode.along.barcode.Utils.ResponseInterceptor;
import barcode.along.barcode.bean.QueryResultBean;
import barcode.along.barcode.bean.UserBean;
import io.reactivex.Observer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiService {

    private ApiInterface mApiInterface;

    private ApiService() {
        //HTTP log
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //RequestInterceptor
        RequestInterceptor requestInterceptor = new RequestInterceptor();

        //ResponseInterceptor
        ResponseInterceptor responseInterceptor = new ResponseInterceptor();

        //OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(requestInterceptor)
                .addInterceptor(responseInterceptor);
//      通过你当前的控制debug的全局常量控制是否打log
//        if (Constants.DEBUG_MODE) {
        builder.addInterceptor(httpLoggingInterceptor);
//        }
        OkHttpClient mOkHttpClient = builder.build();

        //Retrofit
        Retrofit mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://192.168.103.2:8000/BarcodeService/")//替换为你自己的BaseUrl
                .build();

        mApiInterface = mRetrofit.create(ApiInterface.class);
    }

    //单例
    private static class SingletonHolder {
        private static final ApiService INSTANCE = new ApiService();
    }

    //单例
    public static ApiService getApiService() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 验证用户
     */
    public void auth(Observer<UserBean> observer, String str) {
        mApiInterface.auth(str)
                .compose(SchedulersTransformer.io_main())
                .map(new HttpResultFunc<>())
                .subscribe(observer);
    }

    /**
     * 验证用户
     */
    public void getProduct(Observer<QueryResultBean> observer, String str) {
        mApiInterface.getProduct(str)
                .compose(SchedulersTransformer.io_main())
                .map(new HttpResultFunc<>())
                .subscribe(observer);
    }

}
