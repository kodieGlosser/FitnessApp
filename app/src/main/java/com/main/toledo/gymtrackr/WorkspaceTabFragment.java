package com.main.toledo.gymtrackr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created by Adam on 2/26/2015.
 */
public class WorkspaceTabFragment extends Fragment {

    private Button saveButton;
    private Button workspaceEditToggleButton;
    private TextView loadMessage;

    final int PLAN = 1, WORKOUT = 2;
    final int WORKSPACE = 4, DETAILS = 5;
    private int parentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.w_frag_tabs, null);

        Button detailsButton = (Button) v.findViewById(R.id.details);
        Button workspaceButton = (Button) v.findViewById(R.id.workspace);

        workspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentActivity == DETAILS) {
                    ((DetailActivity)getActivity()).saveDetailState();
                    Intent i = new Intent(getActivity(), WorkspaceActivity.class);
                    startActivity(i);


                }
            }
        });

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentActivity == WORKSPACE) {
                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    int circuitValue = ((WorkspaceActivity) getActivity()).getToggledCircuit();
                    int exerciseValue = ((WorkspaceActivity) getActivity()).getToggledExercise();
                    i.putExtra("CIRCUIT_VALUE", circuitValue);
                    i.putExtra("EXERCISE_VALUE", exerciseValue);
                    startActivity(i);
                }
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity){

        String callingActivity = activity.getLocalClassName();
        if (callingActivity.equals("WorkspaceActivity")){
            parentActivity = WORKSPACE;
        }
        if (callingActivity.equals("DetailActivity")){
            parentActivity = DETAILS;
        }

        super.onAttach(activity);
    }
}
