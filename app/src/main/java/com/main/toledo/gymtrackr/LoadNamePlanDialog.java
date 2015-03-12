package com.main.toledo.gymtrackr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Adam on 3/10/2015.
 */
public class LoadNamePlanDialog extends DialogFragment {
    /*
    public interface NameDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }
    */
    //NameDialogListener m_Listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.w_plan_dialog, null);
        TextView textView = (TextView) v.findViewById(R.id.dialogMessage);
        textView.setText("ENTER PLAN NAME BELOW");
        final EditText t = (EditText) v.findViewById(R.id.planName);
        builder.setView(v)
                .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //EditText t = (EditText) v.findViewById(R.id.planName);
                        String planName = t.getText().toString();

                        //if the name is taken
                        if (((LoadActivity) getActivity()).getPlanList().contains(planName)) {
                            //pass the plan to the activity for storage
                            ((LoadActivity) getActivity()).setPlanName(planName);
                            //call error dialog asking how to proceed
                            ((LoadActivity) getActivity()).showErrorDialog();
                        } else {
                            ((LoadActivity) getActivity()).setPlanName(planName);
                            ((LoadActivity) getActivity()).createNewPlan();
                            //m_Listener.onDialogPositiveClick(WorkspaceNameDialog.this);
                        }

                    }
                })
                .setNegativeButton("PISS OFF", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //m_Listener.onDialogNegativeClick(WorkspaceNameDialog.this);

                        LoadNamePlanDialog.this.getDialog().cancel();
                    }
                });
        //TextView t = (TextView) getView().findViewById(R.id.dialogMessage);
        //t.setText("ENTER PLAN NAME");
        return builder.create();
    }
    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            m_Listener = (NameDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }


    }
    */
}
