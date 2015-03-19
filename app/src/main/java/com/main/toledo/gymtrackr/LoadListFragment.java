package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Adam on 2/23/2015.
 */
public class LoadListFragment extends Fragment {

    ListView loadListView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //sets the list adapter to the one we made in the browse activity

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //sets the view for the fragment
        Log.d("PAD BUGS", "ON CREATE VIEW CALLED IN WLFRAG");
        View v = inflater.inflate(R.layout.l_frag_list, null);
        loadListView = (ListView) v.findViewById(R.id.loadListView);
        loadListView.setAdapter(((LoadActivity)getActivity()).getAdapter());
        //loadListView.setDropListener(mDropListener);
        //loadListView.setDragListener(mDragListener);
        return v;
    }
}