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
import android.widget.TextView;

/**
 * Created by Adam on 4/6/2015.
 */
public class historyHeaderFragment extends Fragment {
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

        View v = inflater.inflate(R.layout.h_fragment_header, null);

        return v;

    }
}
