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
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("ERROR: DUPLICATE PLAN NAME EXSISTS.  OVERWRITE?")
                .setPositiveButton("YUP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Method to delete plan goes here.
                        //((LoadActivity) getActivity()).createNewPlan();
                    }
                })
                .setNegativeButton("NAW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }
}