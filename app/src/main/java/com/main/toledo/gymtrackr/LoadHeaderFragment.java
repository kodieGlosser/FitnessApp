package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Adam on 2/23/2015.
 */
public class LoadHeaderFragment extends Fragment {
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
        //sets the view for the fragment
        View v = inflater.inflate(R.layout.l_frag_header, null);

        //sets the listener for the test button
        loadMessage = (TextView)v.findViewById(R.id.headerMessage);
        loadMessage.setText("Select a plan douchebag!");

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
    //method executed when radio button is selected


}
