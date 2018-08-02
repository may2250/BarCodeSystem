package barcode.along.barcode.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import barcode.along.barcode.R;


public class ScannerFragment extends Fragment {
    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.title_scanner);
        return textView;
    }

}
