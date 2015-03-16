package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Adam on 2/23/2015.
 */
public class LoadHeaderFragment extends Fragment {
    //private String selectionOption;
    //private Button testButton;
    //private TextView loadMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        //sets the view for the fragment
        View v = inflater.inflate(R.layout.l_frag_header, null);

        //sets the listener for the test button
        TextView loadMessage = (TextView)v.findViewById(R.id.headerMessage);
        loadMessage.setText("Select a plan!");

        Button addPlanButton = (Button)v.findViewById(R.id.addPlanButton);

        addPlanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((LoadActivity) getActivity()).showNameDialog();
                ((LoadActivity) getActivity()).getAdapter().notifyDataSetChanged();
            }
        });
        //code to handle search
        return v;
    }
    //method executed when radio button is selected


}
