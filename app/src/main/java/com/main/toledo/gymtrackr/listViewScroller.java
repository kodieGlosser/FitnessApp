package com.main.toledo.gymtrackr;

import android.widget.ListView;

/**
 * Created by Adam on 4/22/2015.
 */
public class listViewScroller implements Runnable {
    private volatile boolean running = false;
    private ListView list;
    private int trajectory;

    public listViewScroller(ListView listview) {
        this.list = listview;
    }

    public void setTrajectory(int t){
        this.trajectory = t;
    }

    public void setRunning(boolean isRunning){
        this.running = isRunning;
    }

    public boolean isRunning(){
        return running ;
    }

    public void run(){
        while(running){
            list.smoothScrollByOffset(-1);
        }

    }
}