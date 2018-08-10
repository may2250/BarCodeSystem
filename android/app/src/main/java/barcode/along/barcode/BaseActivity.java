package barcode.along.barcode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Gravity;

import barcode.along.barcode.Api.ApiService;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {
    private ApiService mApiService;
    private CompositeDisposable mCompositeDisposable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mApiService == null){
            mApiService = ApiService.getApiService();
        }
        setContentView(initContentView());
        initUIAndListener();
        initData();
        getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide));
        Slide slide=new Slide();
        slide.setDuration(500);
        slide.setSlideEdge(Gravity.LEFT);
        getWindow().setEnterTransition(slide);
        getWindow().setReenterTransition(new Explode().setDuration(600));
    }

    public abstract int initContentView();

    /**
     * 初始化UI和Listener
     */
    protected abstract void initUIAndListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 管理所有建立的链接,在onDestroy中清空 mCompositeDisposable
     */
    protected void addDisposable(Disposable disposable){
        if (mCompositeDisposable==null){
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        if (mCompositeDisposable != null){
            mCompositeDisposable.clear();
        }
        super.onDestroy();
    }
}
