package com.main.toledo.gymtrackr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Adam on 4/3/2015.
 */
public class CreateExerciseDialog extends DialogFragment {

    private static final int NO_ERROR = 0, NAME_ERROR = 1, METRIC_ERROR = 2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        int errorType = ((CreateExerciseActivity) getActivity()).getError();
        String errorMessage;
        String errorAffirmative;
        String errorNegative;

        switch(errorType){
            case NO_ERROR:
                errorMessage = "Would you like to permanently add this exercise to the database?";
                errorAffirmative = "Yes";
                errorNegative = "No";
                break;
            case NAME_ERROR:
                errorMessage = "Error: Duplicate name exists.  Remove existing exercise via Browse menu, or input a new name.";
                errorAffirmative = "";
                errorNegative = "Okay";
                break;
            case METRIC_ERROR:
                errorMessage = "Error: Must implement at least one metric.";
                errorAffirmative = "";
                errorNegative = "Okay";
                break;
            default:
                errorMessage = "Something terrible has happened.";
                errorAffirmative = "";
                errorNegative = "Okay";
                break;
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(errorMessage);

        if(errorType == NO_ERROR)
            builder.setPositiveButton(errorAffirmative, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ((CreateExerciseActivity) getActivity()).save();
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
