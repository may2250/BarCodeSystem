package barcode.along.barcode.bean;

/**
 * Created by ziabo on 2017/5/9.
 * T就是传递过来的data的类型
 */

public class HttpResult<T> {

    public int status;
    public String errinfo;
    public int msgcode;
    public T data;
}
