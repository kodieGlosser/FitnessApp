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


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        boolean isError = ((CreateExerciseActivity) getActivity()).getError();
        String errorMessage;
        String errorAffirmative;
        String errorNegative;

        if(isError){
            errorMessage = "Error: Duplicate name exists.  Remove existing exercise via Browse menu, or input a new name.";
            errorAffirmative = "";
            errorNegative = "Okay";
        }else{
            errorMessage = "Would you like to permanently add this exercise to the database?";
            errorAffirmative = "Yes";
            errorNegative = "No";
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(errorMessage);

        if(!isError)
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
