package com.main.toledo.gymtrackr;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Adam on 2/26/2015.
 */
public class WorkspacePalletFragment extends Fragment {

    public TextView makeExercise;
    private ImageView exerciseDraggable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.w_frag_pallet, null);
        makeExercise = (TextView) v.findViewById(R.id.make_exercise);
        makeExercise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int action = event.getAction();
                final float y = event.getY();
                final float x = event.getX();

                switch (action) {
                    case MotionEvent.ACTION_DOWN: {

                        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
                        mWindowParams.x = (int)x - 700;
                        mWindowParams.y = (int)y + 650;
                        //TODO: MAKE SCREEN SIZE SCALABLE
                        mWindowParams.height = 200;
                        mWindowParams.width = 200;
                        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                        mWindowParams.format = PixelFormat.TRANSLUCENT;
                        mWindowParams.windowAnimations = 0;

                        Context context = getActivity();
                        exerciseDraggable = new ImageView(context);

                        exerciseDraggable.setImageDrawable(
                                context.getResources().getDrawable(R.drawable.drag1));


                        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        mWindowManager.addView(exerciseDraggable, mWindowParams);

                        //Todo readability: what is 2?
                        ((WorkspaceActivity) getActivity()).ListFragment.workspaceListView.mDraggedItemType = 2;
                        ((WorkspaceActivity) getActivity()).ListFragment.workspaceListView.setDragSpacing(200);
                        ((WorkspaceActivity) getActivity()).ListFragment.workspaceListView.currentXPos = 150;
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        if (exerciseDraggable != null) {
                            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) exerciseDraggable.getLayoutParams();
                            layoutParams.x = (int)event.getX() - 700;
                            layoutParams.y = (int)event.getY() + 650;
                            WindowManager mWindowManager = (WindowManager) getActivity()
                                    .getSystemService(Context.WINDOW_SERVICE);
                            mWindowManager.updateViewLayout(exerciseDraggable, layoutParams);

                            //TODO make scalable

                            ((WorkspaceActivity) getActivity()).ListFragment.workspaceListView.currentYPos = (int)y + 1300;
                            ((WorkspaceActivity) getActivity()).ListFragment.workspaceListView.dragHandling(false);
                            Log.d("WorkspacePalletFragment", "Y: " + ((int)y + 1300));
                        }
                        break;
                    }

                    case MotionEvent.ACTION_UP: {

                        if (exerciseDraggable != null) {
                            exerciseDraggable.setVisibility(View.GONE);
                            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                            wm.removeView(exerciseDraggable);
                            exerciseDraggable.setImageDrawable(null);
                            exerciseDraggable = null;
                            ((WorkspaceActivity) getActivity()).ListFragment.workspaceListView.closeUI();
                            ((WorkspaceActivity) getActivity()).ListFragment.workspaceListView.placeGenericExercise();
                        }

                    }
                }
                return true;
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onResume(){
        super.onResume();

    }
    private void createExerciseDraggable(){

    }
}
