package barcode.along.barcode.Api;

import java.util.List;
import java.util.Map;

import barcode.along.barcode.bean.ComMessageBean;
import barcode.along.barcode.bean.HttpResult;
import barcode.along.barcode.bean.QueryResultBean;
import barcode.along.barcode.bean.UserBean;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    /**
     * 获取信息
     */
    @POST("Auth")
    Observable<HttpResult<UserBean>> auth(@Body String str);

    @GET("Product/{querystr}")
    Observable<HttpResult<QueryResultBean>> getProduct(@Path("querystr") String querystr);

    @POST("Outbound")
    Observable<HttpResult> outbound(@Body String str);

    @POST("GetStatistic")
    Observable<HttpResult<List>> getStatistic(@Body String str);

}
