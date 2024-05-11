package com.example.gasapplication;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecordItemAdapter extends RecyclerView.Adapter<RecordItemAdapter.ViewHolder> {
    private ArrayList<RecordItem> mRecordItemsData;
    private ArrayList<RecordItem> mRecordItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;
    RecordItemAdapter(Context context, ArrayList<RecordItem> itemsData) {
        this.mRecordItemsData = itemsData;
        this.mRecordItemsDataAll = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_readings, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordItemAdapter.ViewHolder holder, int position) {
        RecordItem currentItem = mRecordItemsData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.recycler_animation);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mRecordItemsData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private TextView mValueText;
        private TextView mDateAndTimeText;
        private TextView mWrongText;
        public ViewHolder(View itemView) {
            super(itemView);

            mValueText = itemView.findViewById(R.id.valueTextView);
            mDateAndTimeText = itemView.findViewById(R.id.dateAndTimeTextView);
            mWrongText = itemView.findViewById(R.id.wrongTextView);

            itemView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Activity", "Update clicked");
                }
            });

        }

        public void bindTo(RecordItem currentItem) {
            mValueText.setText(currentItem.getValue());
            mDateAndTimeText.setText(currentItem.getDateAndTime());
            mWrongText.setText((currentItem.isWrong()? "Hiba bejelentve!" : "Nem jelentett be hibát."));

            itemView.findViewById(R.id.update).setOnClickListener(view ->
                    ((ListedActivity)mContext).updateAppointment(currentItem));

            itemView.findViewById(R.id.delete).setOnClickListener(view ->
                    ((ListedActivity)mContext).deleteAppointment(currentItem));
        }
    }

}


