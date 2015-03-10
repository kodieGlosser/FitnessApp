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
    private final DynamicView d;
    private ExerciseHistory[] m_exerciseHistory;
    private int m_numMetrics;
    public EditExerciseHistoryAdapter(Context context, int resource, ExerciseHistory[] history){
        super(context, resource, history);
        m_exerciseHistory = history;
        m_numMetrics = history[0].getMetrics().size();
        d = new DynamicView(this.getContext() , m_numMetrics);
        d.initialize();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Log.d("EDIT HISTORY", "getView called on position: " + position);

        //if( convertView == null ){
            final DynamicView d = new DynamicView(this.getContext(), m_numMetrics);
            d.initialize();
            convertView = d.populateDynamicView(m_exerciseHistory[position]);
        //}
        //    convertView = new DynamicView(d).populateDynamicView(m_exerciseHistory[position]);

        //final View v = m_view;
        //convertView = v;
        return convertView;
    }

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
        //copy constructor
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
            m_view = (LinearLayout) inflater.inflate(R.layout.e_frag_list_history, null);

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
            m_metricTextViews.get(0).setText(e.getDate().toString());
            for(int i = 0; i < m_numMetrics; i++){
                m_metricTextViews.get(i+1).setText(e.getMetrics().get(i).getType().toString() + ": " +
                                                 e.getMetrics().get(i).getMetricIntValue());
                Log.d("EDIT HISTORY", "PopulateDynamicView called: " + e.getMetrics().get(i).getType().toString() + ": " +
                        e.getMetrics().get(i).getMetricIntValue());
            }
            return m_view;

        }
    }

}

