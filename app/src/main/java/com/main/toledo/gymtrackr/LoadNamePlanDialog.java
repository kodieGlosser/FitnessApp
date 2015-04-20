package com.main.toledo.gymtrackr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

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
    final int OTHER = 10, INVALID_NAME_VALUE = 11, TAKEN_NAME_VALUE = 12;
    private boolean copyflag = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.w_plan_dialog, null);
        copyflag = ((LoadActivity) getActivity()).getCopyflag();


        if(!copyflag)
            builder.setMessage("ENTER PLAN NAME");

        if(copyflag)
            builder.setMessage("CREATING PLAN FROM WORKSPACE CONTENTS. ENTER PLAN NAME");


        final EditText t = (EditText) v.findViewById(R.id.planName);
        builder.setView(v)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //EditText t = (EditText) v.findViewById(R.id.planName);
                        String planName = t.getText().toString();
                        //ERROR HAPPENS
                        if (((LoadActivity) getActivity()).getPlanList().contains(planName) || (planName.equals(""))) {
                            //pass the plan name string value to the activity for storage
                            ((LoadActivity) getActivity()).setNewPlanName(planName);
                            //call error dialog asking how to proceed
                            int errorFlag = OTHER;
                            //TODO:  WE NEED MORE CHECKS, MULTIPLE SPACES ETC
                            if(planName.equals("")){
                                errorFlag = INVALID_NAME_VALUE;
                            }
                            if(((LoadActivity) getActivity()).getPlanList().contains(planName)){
                                errorFlag = TAKEN_NAME_VALUE;
                            }

                            ((LoadActivity) getActivity()).showErrorDialog(errorFlag);
                        } else {
                            //NO ERROR
                            ((LoadActivity) getActivity()).setNewPlanName(planName);

                            if(!copyflag)
                                ((LoadActivity) getActivity()).createNewPlan();

                            if(copyflag)
                                ((LoadActivity) getActivity()).createPlanFromWorkspace();
                            //m_Listener.onDialogPositiveClick(WorkspaceNameDialog.this);
                        }

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //m_Listener.onDialogNegativeClick(WorkspaceNameDialog.this);
                        ((LoadActivity) getActivity()).setCopyFlag(false);
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
