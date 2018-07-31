package barcode.along.barcode.Utils;

import android.app.Application;


public class App extends Application{

    public static Application INSTANCE;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        ToastUtil.register(this);
        NetUtils.register(this);
    }
}
