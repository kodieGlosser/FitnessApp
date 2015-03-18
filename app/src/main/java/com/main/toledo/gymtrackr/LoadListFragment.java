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
    /*
    private DropListener mDropListener =
            new DropListener() {
                public void onDrop(int fromX, int fromY, int toX, int toY) {
                    ExpandableListAdapter adapter = loadListView.getListAdapter();
                    if (adapter instanceof WorkspaceExpandableListAdapterMKII) {
                        //Log.d("TOUCH TESTS", "ITEM DROPPED");
                        ((WorkspaceExpandableListAdapterMKII)adapter).onDrop(fromX, fromY, toX, toY);
                        ((WorkspaceActivity)getActivity()).getAdapter().notifyDataSetChanged();
                    }
                }
            };
    */

    //TO GREG - pretty sure the drag color bug originates from here
    private ListDragListener mDragListener =
            new ListDragListener() {
                //android:background="#00B800"
                int backgroundColor = 0xff00B800;
                int defaultBackgroundColor;

                public void onDrag(int x, int y, ListView listView) {
                    //Log.d("TOUCH TESTS", "ON DRAG CALLED");
                    // TODO Auto-generated method stub
                }

                public void onStartDrag(View itemView) {
                    //Log.d("TOUCH TESTS", "ON START DRAG CALLED");
                    itemView.setVisibility(View.INVISIBLE);
                    defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
                    itemView.setBackgroundColor(backgroundColor);
                    //test
                    //ImageView iv = (ImageView)itemView.findViewById(R.id.ImageView01);
                    //if (iv != null) iv.setVisibility(View.INVISIBLE);
                }

                public void onStopDrag(View itemView) {
                    //Log.d("TOUCH TESTS", "ON STOP DRAG CALLED");
                    itemView.setVisibility(View.VISIBLE);
                    //GREG - DRAG BUG IS PROBABLY THIS EXACT THING
                    itemView.setBackgroundColor(backgroundColor);
                    //test
                    //ImageView iv = (ImageView)itemView.findViewById(R.id.ImageView01);
                    //if (iv != null) iv.setVisibility(View.VISIBLE);
                }

            };
}