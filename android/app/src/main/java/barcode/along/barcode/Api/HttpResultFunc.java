package barcode.along.barcode.Api;

import barcode.along.barcode.Utils.ToastUtil;
import barcode.along.barcode.bean.HttpResult;
import io.reactivex.functions.Function;


/**
 * 类描述：用来统一处理Http的status,并将HttpResult的data部分剥离出来返回给subscriber
 * @param <T> data部分的数据模型
 */

public class HttpResultFunc<T> implements Function<HttpResult<T>,T> {

    @Override
    public T apply(HttpResult<T> tHttpResult) throws Exception {
        if (tHttpResult.status != 0){//假设当结果为true的时候是请求成功
            if (tHttpResult.errinfo != ""){//请求失败的情况下吐司错误信息
                ToastUtil.showShort( tHttpResult.errinfo);
            }
        }
        return tHttpResult.data;
    }
}
