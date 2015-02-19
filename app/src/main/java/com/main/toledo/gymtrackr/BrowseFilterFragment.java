package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Adam on 2/10/2015.
 * This is where the code for our browse filtering options lives.
 */
public class BrowseFilterFragment extends Fragment {
    //this is the test button that shows up at the top of the list
    //code for our checkboxes or whatever is going to go here.
    private EditText editText;
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
               //2/19 dewired((BrowseActivity)getActivity()).addItem();
           }
        });
        //code to handle search
        editText = (EditText)v.findViewById(R.id.exercise_name_search);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                String search_value;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
 //                   EditText e = (EditText)getActivity().getFragmentManager().findFragmentById
 //                           (R.id.exerciseFiltersContainer)
  //                         .getView().findViewById(R.id.exercise_name_search);
  //                  search_value = e.getText().toString();
   //                 testMethod(search_value);
                    testMethod("LISTENER CHECK");
                    handled = true;
                }
                return handled;
            }
        });
        return v;
    }
    public void testMethod(String s){
        ((BrowseActivity)getActivity()).searchForItem(editText.getText().toString());

    }
}
