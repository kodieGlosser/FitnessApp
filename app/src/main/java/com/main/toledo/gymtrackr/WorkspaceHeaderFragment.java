package com.main.toledo.gymtrackr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Adam on 2/26/2015.
 */
public class WorkspaceHeaderFragment extends Fragment {

    private Button testButton;
    private Button workspaceEditToggleButton;
    private TextView loadMessage;

    final int EDIT = 1, WORKOUT = 2;

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
        String textVal;
        switch(((WorkspaceActivity) getActivity()).getCourseOfAction()){
            case EDIT:
                textVal = "  EDIT";
                break;
            case WORKOUT:
                textVal = "  WORKOUT";
                break;
            default:
                textVal = "  SHIT HAPPENED!";
                break;
        }

        loadMessage.setText(textVal);

        //sets the listener for the test (collapse) button

        testButton = (Button)v.findViewById(R.id.WorkspaceTestButton);

        testButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Plan p = WorkoutData.get(getActivity()).crapNewPlan();
                DatabaseWrapper db = new DatabaseWrapper();
                db.saveEntirePlan(p);
            }
        });

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
