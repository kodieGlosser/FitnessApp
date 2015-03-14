package com.main.toledo.gymtrackr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Adam on 2/26/2015.
 */
public class WorkspaceHeaderFragment extends Fragment {

    private Button saveButton;
    private Button workspaceEditToggleButton;
    private TextView loadMessage;

    final int PLAN = 1, WORKOUT = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        //sets the view for the fragment
        View v = inflater.inflate(R.layout.w_frag_header, null);

        //sets first message text
        loadMessage = (TextView)v.findViewById(R.id.headerMessage);
        loadMessage.setText(((WorkspaceActivity) getActivity()).getPlanName());

        //sets second message text
        loadMessage = (TextView)v.findViewById(R.id.headerMessageTwo);

        //sets the listener for the test (collapse) button

        saveButton = (Button)v.findViewById(R.id.workspaceSaveButton);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //save code will go here

                if (((WorkspaceActivity) getActivity()).getAppMode() == PLAN) {
                    //CODE FOR PLAN SAVE
                    Plan p = WorkoutData.get(getActivity()).crapNewPlan();
                    DatabaseWrapper db = new DatabaseWrapper();
                    db.saveEntirePlan(p);
                }
                if (((WorkspaceActivity) getActivity()).getAppMode() == WORKOUT) {
                    //CODE FOR WORKOUT SAVE, EG EXPORT TO HISTORY
                }
            }
        });

        String textVal;
        switch(((WorkspaceActivity) getActivity()).getAppMode()){
            case PLAN:
                textVal = "  PLAN";
                saveButton.setText("SAVE PLAN");
                break;
            case WORKOUT:
                textVal = "  WORKOUT";
                saveButton.setText("EXPORT TO HISTORY");
                break;
            default:
                textVal = "  SHIT HAPPENED!";
                break;
        }

        loadMessage.setText(textVal);
        //sets the listener for the toggle button
        workspaceEditToggleButton = (Button)v.findViewById(R.id.toggleEdit);

        workspaceEditToggleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               ((WorkspaceActivity) getActivity()).toggleEdit();
            }
        });

        workspaceEditToggleButton.setBackgroundColor(Color.BLUE);
        workspaceEditToggleButton.setTextColor(Color.WHITE);
        //code to handle search
        return v;
    }
}
