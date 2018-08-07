package barcode.along.barcode.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import barcode.along.barcode.R;
import barcode.along.barcode.bean.ComMessageBean;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ComMessageBean> mMessageList;
    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    //全部加载完成
    public static final int  LOADING_END=2;
    private static final int TYPE_ITEM =0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //底部FootView
    private int load_more_status=0;

    public MessageAdapter(List<ComMessageBean> messageList){
        this.mMessageList = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            final ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            itemViewHolder.MessageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = itemViewHolder.getAdapterPosition();
                    ComMessageBean messageBean = mMessageList.get(position);
                    Toast.makeText(view.getContext(), "You clicked view "+ messageBean.getMac(), Toast.LENGTH_SHORT).show();
                }
            });
            return itemViewHolder;
        }else if(viewType==TYPE_FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item, parent, false);
            final FootViewHolder footViewHolder = new FootViewHolder(view);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return footViewHolder;
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if(holder instanceof ItemViewHolder) {
            ((ItemViewHolder)holder).productMac.setText(mMessageList.get(i).getMac());
            ((ItemViewHolder)holder).productSn.setText(mMessageList.get(i).getSn());
            ((ItemViewHolder)holder).productOrderid.setText(mMessageList.get(i).getOrderid());
            ((ItemViewHolder)holder).productDate.setText(mMessageList.get(i).getOptdate());
            ((ItemViewHolder)holder).itemView.setTag(i);
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    footViewHolder.foot_view_item_tv.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footViewHolder.foot_view_item_tv.setText("正在加载更多数据...");
                    break;
                case LOADING_END:
                    footViewHolder.foot_view_item_tv.setText("没有更多了...");
                    break;
            }
        }


    }

    @Override
    public int getItemCount() {
        if(mMessageList != null){
            return (mMessageList.size() + 1);
        }else{
            return 0;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView productMac;
        TextView productSn;
        TextView productOrderid;
        TextView productDate;
        View MessageView;
        public ItemViewHolder(View view){
            super(view);
            MessageView = view;
            productMac = (TextView) view.findViewById(R.id.product_mac);
            productSn = (TextView) view.findViewById(R.id.product_sn);
            productOrderid = (TextView) view.findViewById(R.id.product_orderid);
            productDate = (TextView) view.findViewById(R.id.product_optdate);
        }
    }
    /**
     * 底部FootView布局
     */
    public static class FootViewHolder extends  RecyclerView.ViewHolder{
        private TextView foot_view_item_tv;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv=(TextView)view.findViewById(R.id.foot_view_item_tv);
        }
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void changeMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }


}
