package barcode.along.barcode.fragment;

import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import barcode.along.barcode.R;


public class ScannerFragment extends Fragment {
    private View view;
    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scanner, container, false);
        // Inflate the layout for this fragment
        final ViewGroup transitionsContainer = (ViewGroup) view.findViewById(R.id.scanner_contain);
        final TextView text = (TextView) transitionsContainer.findViewById(R.id.text);

        transitionsContainer.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                if(text.getVisibility() == View.VISIBLE){
                    text.setVisibility(View.GONE);
                }else{
                    text.setVisibility(View.VISIBLE);
                }
            }

        });
        return view;
    }

}
