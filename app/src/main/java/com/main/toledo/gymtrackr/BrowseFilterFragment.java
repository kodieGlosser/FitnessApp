package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Adam on 2/10/2015.
 * This is where the code for our browse filtering options lives.
 */
public class BrowseFilterFragment extends Fragment {
    //this is the test button that shows up at the top of the list
    //code for our checkboxes or whatever is going to go here.

    private Button testButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        //sets the view for the fragment
        View v = inflater.inflate(R.layout.b_frag_filters, null);

        //sets the listener for the test button
        testButton = (Button)v.findViewById(R.id.filterTestButton);

        testButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               //calls additem in the browse activity
               ((BrowseActivity)getActivity()).addItem();
           }
        });


        return v;

    }
}
