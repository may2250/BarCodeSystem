package barcode.along.barcode.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import barcode.along.barcode.R;
import barcode.along.barcode.bean.ComMessageBean;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<ComMessageBean> mMessageList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View MessageView;
        TextView productMac;
        TextView productSn;
        TextView productOrderid;
        TextView productDate;
        public ViewHolder(View view){
            super(view);
            MessageView = view;
            productMac = (TextView) view.findViewById(R.id.product_mac);
            productSn = (TextView) view.findViewById(R.id.product_sn);
            productOrderid = (TextView) view.findViewById(R.id.product_orderid);
            productDate = (TextView) view.findViewById(R.id.product_optdate);
        }
    }

    public MessageAdapter(List<ComMessageBean> messageList){
        this.mMessageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.MessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                ComMessageBean messageBean = mMessageList.get(position);
                Toast.makeText(view.getContext(), "You clicked view "+ messageBean.getMac(), Toast.LENGTH_SHORT).show();
            }
        });

        /*holder.fruitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Fruit fruit = mFruitList.get(position);
                Toast.makeText(view.getContext(), "You clicked image "+ fruit.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        */
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        ComMessageBean messageBean = mMessageList.get(i);
        holder.productMac.setText(messageBean.getMac());
        holder.productSn.setText(messageBean.getSn());
        holder.productOrderid.setText(messageBean.getOrderid());
        holder.productDate.setText(messageBean.getOptdate());
    }

    @Override
    public int getItemCount() {
        if(mMessageList != null){
            return mMessageList.size();
        }else{
            return 0;
        }

    }
}
