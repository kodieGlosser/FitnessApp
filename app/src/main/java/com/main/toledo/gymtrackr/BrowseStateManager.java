package com.main.toledo.gymtrackr;

/**
 * Created by Adam on 4/17/2015.
 */
public class BrowseStateManager {
    private int mHistoryState;

    public BrowseStateManager(){

    }

    public void setHistoryState(int state){
        mHistoryState = state;
    }

    public int getHistoryState(){
        return mHistoryState;
    }


}
