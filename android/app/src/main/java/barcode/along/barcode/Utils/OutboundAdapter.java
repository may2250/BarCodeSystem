package barcode.along.barcode.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;

import barcode.along.barcode.R;
import barcode.along.barcode.bean.FailedMessage;

public class OutboundAdapter extends ArrayAdapter {
    private final int resourceId;
    public OutboundAdapter(Context context, int textViewResourceId, List<FailedMessage> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FailedMessage message = (FailedMessage) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView mac = (TextView) view.findViewById(R.id.message_mac);
        TextView info = (TextView) view.findViewById(R.id.message_info);
        mac.setText(message.getMac());
        info.setText(message.getDesc());
        return view;

    }
}
