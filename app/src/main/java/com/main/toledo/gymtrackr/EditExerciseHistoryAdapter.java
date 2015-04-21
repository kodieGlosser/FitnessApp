package com.main.toledo.gymtrackr;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditExerciseHistoryAdapter extends ArrayAdapter {
    //private final DynamicView d;
    private ArrayList<ExerciseHistory> m_exerciseHistory;

    private LinearLayout metricSubLayout;

    private final int dateId = View.generateViewId();
    private final int firstMetricId = View.generateViewId();
    private final int secondMetricId = View.generateViewId();
    private final int thirdMetricId = View.generateViewId();

    private final Context mContext;

    private int mNumMetrics;

    private int mDateTextSize = 20;
    private final int mMetricEditLeftMargininDP = 30;
    private int mMarginsDP = 10;
    private int mMetricEditLeftMarginPixels;
    private int mMarginsInPixels;
    public EditExerciseHistoryAdapter(Context context, int resource, ArrayList<ExerciseHistory> history){
        super(context, resource, history);
        m_exerciseHistory = history;
        mContext = context;

        if(history.size() != 0){
            mNumMetrics = history.get(0).getMetrics().size();
            initializeView();
        } else {
            //empty data set
        }

        float scale = context.getResources().getDisplayMetrics().density;
        mMetricEditLeftMarginPixels = (int) (mMetricEditLeftMargininDP * scale + 0.5f);

        mMarginsInPixels = (int) (mMarginsDP * scale + 0.5f);
        //Log.d("4.11", "CONSTRUCTIING ADAPTER, NUM METRICS: " + mNumMetrics);
    }

    public void reloadAdapter(){

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //Log.d("4.11", "getView called on position: " + position);
        ViewHolder holder = null;
        ArrayList<Metric> metrics = m_exerciseHistory.get(position).getMetrics();
        if( convertView == null ){
            holder = new ViewHolder();

            convertView = initializeView();
            holder.date = (TextView)convertView.findViewById(dateId);
            switch(mNumMetrics){
                case 0:
                    break;
                case 1:
                    holder.firstMetric = (TextView)convertView.findViewById(firstMetricId);
                    break;
                case 2:
                    holder.firstMetric = (TextView)convertView.findViewById(firstMetricId);
                    holder.secondMetric = (TextView)convertView.findViewById(secondMetricId);
                    break;
                case 3:
                    holder.firstMetric = (TextView)convertView.findViewById(firstMetricId);
                    holder.secondMetric = (TextView)convertView.findViewById(secondMetricId);
                    holder.thirdMetric = (TextView)convertView.findViewById(thirdMetricId);
                    break;
                default:
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.date.setText(m_exerciseHistory.get(position).getDate().toString());
        String s;
        switch(mNumMetrics){
            case 0:
                break;
            case 1:
                s = "" + metrics.get(0).getType() + ": " + metrics.get(0).getMetricIntValue();
                holder.firstMetric.setText(s);
                break;
            case 2:
                s = "" + metrics.get(0).getType() + ": " + metrics.get(0).getMetricIntValue();
                holder.firstMetric.setText(s);
                s = "" + metrics.get(1).getType() + ": " + metrics.get(1).getMetricIntValue();
                holder.secondMetric.setText(s);
                break;
            case 3:
                s = "" + metrics.get(0).getType() + ": " + metrics.get(0).getMetricIntValue();
                holder.firstMetric.setText(s);
                s = "" + metrics.get(1).getType() + ": " + metrics.get(1).getMetricIntValue();
                holder.secondMetric.setText(s);
                s = "" + metrics.get(2).getType() + ": " + metrics.get(2).getMetricIntValue();
                holder.thirdMetric.setText(s);
                break;
            default:
                break;
        }

        return convertView;
    }

    private LinearLayout initializeView(){
        int paddingInDp = 10;
        float scale = mContext.getResources().getDisplayMetrics().density;
        int padding = (int) (paddingInDp*scale + 0.5f);


        LinearLayout metricLayout = new LinearLayout(mContext);
        metricLayout.setPadding(0, padding, 0, 0);

        metricSubLayout = new LinearLayout(mContext);
        metricSubLayout.setOrientation(LinearLayout.VERTICAL);
        metricSubLayout.setBackground(mContext.getResources().getDrawable(R.drawable.circuit_notselected));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT
        );

        metricSubLayout.setLayoutParams(params);

        metricLayout.setLayoutParams(params);



        TextView dateView = new TextView(mContext);
        dateView.setId(dateId);
        metricSubLayout.addView(dateView);
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mDateTextSize);

        TextView firstMetricView = new TextView(mContext);
        firstMetricView.setId(firstMetricId);
        firstMetricView.setPadding(mMetricEditLeftMarginPixels,0,0,0);

        TextView secondMetricView = new TextView(mContext);
        secondMetricView.setId(secondMetricId);
        secondMetricView.setPadding(mMetricEditLeftMarginPixels,0,0,0);

        TextView thirdMetricView = new TextView(mContext);
        thirdMetricView.setId(thirdMetricId);
        thirdMetricView.setPadding(mMetricEditLeftMarginPixels,0,0,0);

        switch(mNumMetrics) {
            case 0:
                break;
            case 1:
                metricSubLayout.addView(firstMetricView);
                break;
            case 2:
                metricSubLayout.addView(firstMetricView);
                metricSubLayout.addView(secondMetricView);
                break;
            case 3:
                metricSubLayout.addView(firstMetricView);
                metricSubLayout.addView(secondMetricView);
                metricSubLayout.addView(thirdMetricView);
                break;
            default:
                break;
        }
        metricLayout.addView(metricSubLayout);

        return metricLayout;
    }

    public static class ViewHolder{
        public TextView date;
        public TextView firstMetric;
        public TextView secondMetric;
        public TextView thirdMetric;
    }
}


