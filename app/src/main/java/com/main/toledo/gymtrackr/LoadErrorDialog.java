package com.main.toledo.gymtrackr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Adam on 3/11/2015.
 */
public class LoadErrorDialog extends DialogFragment {
    final int OTHER = 10, INVALID_NAME_VALUE = 11, TAKEN_NAME_VALUE = 12;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String errorMessage;
        String errorAffirmative;
        String errorNegative;
        int error = ((LoadActivity) getActivity()).getError();
        switch(error){
            case OTHER:
                errorMessage = "YOU DID SOMETHING SUPER TERRIBLE";
                errorAffirmative = "TELL ADAM";
                errorNegative = "TELL ADAM";
                break;
            case INVALID_NAME_VALUE:
                errorMessage = "ENTERED NAME IS INVALID";
                errorAffirmative = "";
                errorNegative = "CRAP, I'LL FIX IT";
                break;
            case TAKEN_NAME_VALUE:
                errorMessage = "ERROR: DUPLICATE PLAN NAME EXISTS.  OVERWRITE?";
                errorAffirmative = "YUP";
                errorNegative = "NAW";
                break;
            default:
                errorMessage = "YOU DID SOMETHING SUPER TERRIBLE";
                errorAffirmative = "TELL ADAM";
                errorNegative = "TELL ADAM";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(errorMessage);//"ERROR: DUPLICATE PLAN NAME EXSISTS.  OVERWRITE?")

        if (error == TAKEN_NAME_VALUE){
            builder.setPositiveButton(errorAffirmative, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ((LoadActivity) getActivity()).createNewPlan();
                }
            });
        }

        builder.setNegativeButton(errorNegative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        // Create the AlertDialog object and return it
        return builder.create();

    }
}