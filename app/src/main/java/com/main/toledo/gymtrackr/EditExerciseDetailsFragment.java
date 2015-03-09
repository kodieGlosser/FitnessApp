package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditExerciseDetailsFragment extends Fragment {
    private EditText reps, weight, time;
    private Exercise exercise;
    private ArrayList<EditText> editList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        exercise = ((EditActivity) getActivity()).getExercise();
        //sets the view for the fragment
        View v = inflater.inflate(R.layout.e_frag_details, null);

        TextView title = (TextView) v.findViewById(R.id.exerciseNameView);
        title.setText(exercise.getName());

        for(int i = 0; i < exercise.getMetrics().size(); i++){
            switch(exercise.getMetrics().get(i).getType()){
                case TIME:

                    LinearLayout timeLayout = (LinearLayout) v.findViewById(R.id.detailTimeViewLayout);
                    timeLayout.setVisibility(View.VISIBLE);
                    TextView timeText = (TextView) v.findViewById(R.id.detailTimeView);
                    timeText.setText("Time: ");

                    time = (EditText)v.findViewById(R.id.time);

                    time.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                set();
                                handled = true;
                            }
                            return handled;
                        }
                    });

                    editList.add(time);
                    break;
                case REPETITIONS:
                    LinearLayout repLayout = (LinearLayout) v.findViewById(R.id.detailRepViewLayout);
                    repLayout.setVisibility(View.VISIBLE);
                    TextView repText = (TextView) v.findViewById(R.id.detailRepView);
                    repText.setText("Reps: ");

                    reps = (EditText)v.findViewById(R.id.reps);

                    reps.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                set();
                                handled = true;
                            }
                            return handled;
                        }
                    });
                    editList.add(reps);
                    break;
                case OTHER:
                    LinearLayout otherLayout = (LinearLayout) v.findViewById(R.id.detailOtherViewLayout);
                    otherLayout.setVisibility(View.VISIBLE);
                    TextView otherText = (TextView) v.findViewById(R.id.detailOtherView);
                    otherText.setText(exercise.getMetrics().get(i).getMetricName());
                    break;
                case WEIGHT:
                    LinearLayout weightLayout = (LinearLayout) v.findViewById(R.id.detailWeightViewLayout);
                    weightLayout.setVisibility(View.VISIBLE);
                    TextView weightText = (TextView) v.findViewById(R.id.detailWeightView);
                    weightText.setText("Weight: ");

                    weight = (EditText)v.findViewById(R.id.weight);

                    weight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                set();
                                handled = true;
                            }
                            return handled;
                        }
                    });
                    editList.add(weight);
                    break;
                default:
                    Log.d("ERROR DETAIL FRAGMENT", "DEFAULT REACHED IN DETAIL FRAGMENT");
                    break;
            }
        }



        return v;
    }

    private void set(){
        for(int i = 0; i < exercise.getMetrics().size(); i++){
            if (editList.get(i) != null) {
                int metricValue = Integer.parseInt(editList.get(i).getText().toString());
                exercise.getMetrics().get(i).setMetricIntValue(metricValue);
                Log.d("EDIT TEST", "METRIC: " + exercise.getMetrics().get(i).getType()
                        + " --- VALUE: " + metricValue);
            }
        }
    }

}
