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

    /*
    public class DynamicView{
        private LinearLayout m_view;
        private ArrayList<TextView> m_metricTextViews = new ArrayList<>();
        private ArrayList<LinearLayout> m_layoutRows = new ArrayList<>();
        private Context m_context;
        private int m_numMetrics;
        //base constructor
        public DynamicView(Context c , int m){
            m_numMetrics = m;
            m_context = c;
        }
        //copy constructor:  may not be needed

        public DynamicView(DynamicView d){
            this.m_view = d.m_view;
            this.m_metricTextViews = d.m_metricTextViews;
            this.m_layoutRows = d.m_layoutRows;
            this.m_context = d.m_context;
            this.m_numMetrics = d.m_numMetrics;
        }

        public void initialize(){
            LayoutInflater inflater = (LayoutInflater) m_context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            m_view = (LinearLayout) inflater.inflate(R.layout.e_frag_list_item_history, null);

            for(int i = 0; i < m_numMetrics; i++){
                if( i % 2 == 0) {
                    m_layoutRows.add(new LinearLayout(m_context));
                    m_layoutRows.get(i/2).setOrientation(LinearLayout.HORIZONTAL);
                    if ( i == 0){
                        m_metricTextViews.add(new TextView(m_context));
                        m_layoutRows.get(i/2).addView(m_metricTextViews.get(i));
                    }
                    m_metricTextViews.add(new TextView(m_context));
                    m_layoutRows.get(i/2).addView(m_metricTextViews.get(i+1));
                    m_view.addView(m_layoutRows.get(i/2));
                } else {
                    m_metricTextViews.add(new TextView(m_context));
                    m_layoutRows.get(((i-(i%2))/2)).addView(m_metricTextViews.get(i+1));
                }
            }
        }

        public LinearLayout populateDynamicView(ExerciseHistory e){
            if(e.getDate() != null) {
                m_metricTextViews.get(0).setText(e.getDate().toString());
            } else {
                m_metricTextViews.get(0).setText("DATE ERROR OCCURRED!");
            }
            for(int i = 0; i < m_numMetrics; i++){
                m_metricTextViews.get(i+1).setText(e.getMetrics().get(i).getType().toString() + ": " +
                                                 e.getMetrics().get(i).getMetricIntValue());
                //Log.d("EDIT HISTORY", "PopulateDynamicView called: " + e.getMetrics().get(i).getType().toString() + ": " +
                //        e.getMetrics().get(i).getMetricIntValue());
            }
            return m_view;

        }


    }
    */


