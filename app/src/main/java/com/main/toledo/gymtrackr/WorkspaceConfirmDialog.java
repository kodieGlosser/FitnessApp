package com.main.toledo.gymtrackr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Adam on 3/29/2015.
 */
public class WorkspaceConfirmDialog extends DialogFragment {
    final int PLAN = 1, WORKOUT = 2, WORKOUT_WITH_PLAN = 4, LOAD_PLAN = 5;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Use the Builder class for convenient dialog construction
        String errorMessage;
        String errorAffirmative;
        String errorNegative;

        int mode = ((WorkspaceActivity) getActivity()).getAppMode();
        Log.d("4/17", "APPMODE = " + mode);
        switch(mode){
            case PLAN:
                errorMessage = "SAVE PLAN CHANGES?";
                errorAffirmative = "YUP";
                errorNegative = "NAHP";
                break;
            case WORKOUT:
                errorMessage = "EXPORT CHECKED ITEMS TO HISTORY?  CANNOT BE UNDONE.";
                errorAffirmative = "WERD";
                errorNegative = "NAH";
                break;
            case WORKOUT_WITH_PLAN:
                errorMessage = "EXPORT CHECKED ITEMS TO HISTORY?  CANNOT BE UNDONE.";
                errorAffirmative = "WERD";
                errorNegative = "NAH";
                break;
            default:
                errorMessage = "YOU DID SOMETHING SUPER TERRIBLE";
                errorAffirmative = "TELL ADAM";
                errorNegative = "TELL ADAM";
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(errorMessage);//"ERROR: DUPLICATE PLAN NAME EXSISTS.  OVERWRITE?")


        builder.setPositiveButton(errorAffirmative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((WorkspaceActivity) getActivity()).save();
            }
        });

        builder.setNegativeButton(errorNegative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        // Create the AlertDialog object and return it
        return builder.create();

    }
}
