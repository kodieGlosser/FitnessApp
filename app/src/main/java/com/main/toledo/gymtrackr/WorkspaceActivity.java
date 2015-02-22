package com.main.toledo.gymtrackr;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/15/2015.
 */
public class WorkspaceActivity extends Activity {


    ArrayList<Circuit> workout = new ArrayList<Circuit>();
    ArrayList<Circuit> singletonWorkout = new ArrayList<Circuit>();
    DragNDropExpandableListView workspaceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d("TEST ", "CALLED ONCREATE IN WORKSPACE ACTIVITY");

        // get the listview

        //testDataset();
        workspaceListView = (DragNDropExpandableListView) findViewById(R.id.lvExp);

        final WorkspaceExpandableListAdapterMKII listAdapter = new WorkspaceExpandableListAdapterMKII(
                this,  WorkoutData.get(this).getWorkout());
        //Log.d("TEST", "Adapter Created");
        workspaceListView.setAdapter(listAdapter);
        expandLists(listAdapter);


        /*TEST
        if (listView instanceof  DragNDropExpandableListView) {
            Log.d("DRAGTESTS", "SETTING LISTENERS");
            ((DragNDropExpandableListView) listView).setDropListener(mDropListener);
            //((DragNDropExpandableListView) listView).setRemoveListener(mRemoveListener);
            ((DragNDropExpandableListView) listView).setDragListener(mDragListener);
        }
        //attempt two
                Log.d("DRAGTESTS", "SETTING LISTENERS");
        ((DragNDropExpandableListView) listView).setDropListener(mDropListener);
            //((DragNDropExpandableListView) listView).setRemoveListener(mRemoveListener);
        ((DragNDropExpandableListView) listView).setDragListener(mDragListener);
        */
        workspaceListView.setDropListener(mDropListener);
        //((DragNDropExpandableListView) listView).setRemoveListener(mRemoveListener);
        workspaceListView.setDragListener(mDragListener);


    }

    @Override
    public void onResume(){
        super.onResume();
        //if (listAdapter != null) {
        //    listAdapter.notifyDataSetChanged();
        //}
    }

    public void expandLists(WorkspaceExpandableListAdapterMKII listAdapter){
        int count = listAdapter.getGroupCount();
        for (int position = 1; position <= count; position++){
            workspaceListView.expandGroup(position - 1);
        }
    }

    private DropListener mDropListener =
            new DropListener() {
                public void onDrop(int fromX, int fromY, int toX, int toY) {
                    ExpandableListAdapter adapter = workspaceListView.getExpandableListAdapter();
                    if (adapter instanceof WorkspaceExpandableListAdapterMKII) {
                        Log.d("TOUCH TESTS", "ITEM DROPPED");
                        ((WorkspaceExpandableListAdapterMKII)adapter).onDrop(fromX, fromY, toX, toY);
                        workspaceListView.invalidateViews();
                    }
                }
            };

    private DragListener mDragListener =
            new DragListener() {

                int backgroundColor = 0xe0103010;
                int defaultBackgroundColor;

                public void onDrag(int x, int y, ExpandableListView listView) {
                    Log.d("TOUCH TESTS", "ON DRAG CALLED");
                    // TODO Auto-generated method stub
                }

                public void onStartDrag(View itemView) {
                    itemView.setVisibility(View.INVISIBLE);
                    defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
                    itemView.setBackgroundColor(backgroundColor);
                    //test
                    //ImageView iv = (ImageView)itemView.findViewById(R.id.ImageView01);
                    //if (iv != null) iv.setVisibility(View.INVISIBLE);
                }

                public void onStopDrag(View itemView) {
                    itemView.setVisibility(View.VISIBLE);
                    itemView.setBackgroundColor(defaultBackgroundColor);
                    //test
                    //ImageView iv = (ImageView)itemView.findViewById(R.id.ImageView01);
                    //if (iv != null) iv.setVisibility(View.VISIBLE);
                }

            };


}




/* may need in future
    public void testDataset(){
        Circuit CircuitA = new Circuit();
        Circuit CircuitB = new Circuit();
        Circuit CircuitC = new Circuit();

        CircuitA.add( new Exercise("curl", "arms", 1, "dumbbell"));
        CircuitA.add( new Exercise("squat", "legs", 2, "barbell"));
        CircuitA.add( new Exercise("bench press", "chest", 3, "barbell"));
        CircuitB.add( new Exercise("dumb bell row", "back", 4, "dumbell"));
        CircuitB.add( new Exercise("triceps extension", "arms", 5, "dumbell"));
        CircuitC.add( new Exercise("shoulder press", "shoulders", 6, "barbell"));
        CircuitC.add( new Exercise("shrug", "shoulders", 7, "barbell"));
        CircuitC.add( new Exercise("shrug", "shoulders", 7, "barbell"));

        CircuitA.setName("testcircuitA");
        CircuitB.setName("testcircuitB");
        CircuitC.setName("testcircuitC");

        CircuitA.setOrder(0);
        CircuitB.setOrder(1);
        CircuitC.setOrder(2);

        workout.add( CircuitA );
        workout.add( CircuitB );
        workout.add( CircuitC );
    }

    public void incrementPlaceholderCircuit(){
        //WorkoutData.get(this).getWorkout()
    }


    @Override
    public void onGroupCollapse(int groupPosition){
        //Log.d("TEST ", "onGroupCollapse called from work act");
    }



 */
