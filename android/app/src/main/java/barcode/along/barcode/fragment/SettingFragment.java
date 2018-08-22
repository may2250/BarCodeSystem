package barcode.along.barcode.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import barcode.along.barcode.R;
import barcode.along.barcode.Utils.App;
import barcode.along.barcode.Utils.SharedPreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private View view;
    SharedPreferencesUtils helper = new SharedPreferencesUtils(App.INSTANCE, "setting");

    private CheckBox cb_autologin;
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cb_autologin = (CheckBox) view.findViewById(R.id.checkBox_login);

        //判断是否自动登录
        if (helper.getBoolean("autoLogin", false)) {
            cb_autologin.setChecked(true);
        }

        cb_autologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.putValues(new SharedPreferencesUtils.ContentValue("autoLogin", cb_autologin.isChecked()));
            }
        });
    }
}
