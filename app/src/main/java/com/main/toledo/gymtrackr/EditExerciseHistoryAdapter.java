package com.main.toledo.gymtrackr;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private LinearLayout metricLayout;

    private final int dateId = View.generateViewId();
    private final int firstMetricId = View.generateViewId();
    private final int secondMetricId = View.generateViewId();
    private final int thirdMetricId = View.generateViewId();

    private final Context mContext;

    private int mNumMetrics;


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
            metricLayout = null;
            initializeView();
            convertView = metricLayout;
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
                holder.firstMetric.setText(s);
                break;
            case 3:
                s = "" + metrics.get(0).getType() + ": " + metrics.get(0).getMetricIntValue();
                holder.firstMetric.setText(s);
                s = "" + metrics.get(1).getType() + ": " + metrics.get(1).getMetricIntValue();
                holder.firstMetric.setText(s);
                s = "" + metrics.get(2).getType() + ": " + metrics.get(2).getMetricIntValue();
                holder.firstMetric.setText(s);
                break;
            default:
                break;
        }

        return convertView;
    }

    private void initializeView(){
        metricLayout = new LinearLayout(mContext);
        metricLayout.setOrientation(LinearLayout.VERTICAL);

        TextView dateView = new TextView(mContext);
        dateView.setId(dateId);

        metricLayout.addView(dateView);

        TextView firstMetricView = new TextView(mContext);
        firstMetricView.setId(firstMetricId);

        TextView secondMetricView = new TextView(mContext);
        secondMetricView.setId(secondMetricId);

        TextView thirdMetricView = new TextView(mContext);
        thirdMetricView.setId(thirdMetricId);

        switch(mNumMetrics) {
            case 0:
                break;
            case 1:
                metricLayout.addView(firstMetricView);
                break;
            case 2:
                metricLayout.addView(firstMetricView);
                metricLayout.addView(secondMetricView);
                break;
            case 3:
                metricLayout.addView(firstMetricView);
                metricLayout.addView(secondMetricView);
                metricLayout.addView(thirdMetricView);
                break;
            default:
                break;
        }
    }

    public static class ViewHolder{
        public TextView date;
        public TextView firstMetric;
        public TextView secondMetric;
        public TextView thirdMetric;
    }
}


