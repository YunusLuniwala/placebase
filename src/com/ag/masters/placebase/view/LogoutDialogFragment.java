package com.ag.masters.placebase.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.DialogFragment;

import com.ag.masters.placebase.R;
import com.ag.masters.placebase.StartActivity;
import com.ag.masters.placebase.model.Global;

public class LogoutDialogFragment extends DialogFragment {
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_logout)
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                      
                	   	// set Shared Preferences for user back to -1
				    	SharedPreferences settings = getActivity().getSharedPreferences(Global.PREFS, 0);
				    	SharedPreferences.Editor editor = settings.edit();
				    	editor.putInt("user", -1);
				    	editor.commit();
				    	
				    	Intent logoutIntent = new Intent(getActivity(), StartActivity.class);
				    	logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				    	startActivity(logoutIntent);
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	   LogoutDialogFragment.this.getDialog().cancel();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
}
