package com.main.toledo.gymtrackr;

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
    private String selectionOption;
    private Button testButton;
    private TextView loadMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        Log.d("test", "On createview called.");
        //sets the view for the fragment
        View v = inflater.inflate(R.layout.l_frag_header, null);

        //sets the listener for the test button
        loadMessage = (TextView)v.findViewById(R.id.headerMessage);
        loadMessage.setText("WERKOUTMANG");

        testButton = (Button)v.findViewById(R.id.LoadTestButton);

        testButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //calls additem in the browse activity
                //2/19 dewired((BrowseActivity)getActivity()).addItem();
            }
        });
        //code to handle search
        return v;
    }
}
