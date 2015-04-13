package com.main.toledo.gymtrackr;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Adam on 4/13/2015.
 */
public class HistoryAdapter extends ArrayAdapter<Date> {
    private Context mContext;
    private ArrayList<Date> mHistoryDates;

    public HistoryAdapter(Context context, int resource, ArrayList<Date> dates) {
        super(context, resource, dates);
        mContext = context;
        mHistoryDates = dates;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;

        if ((convertView == null)) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.h_history_item, null);
            holder.date = (TextView)convertView.findViewById(R.id.historyData);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.date.setText(mHistoryDates.get(position).toString());

        return convertView;
    }

    public static class ViewHolder{
        public TextView date;
    }
}