package com.tstudioz.fax.fme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;


public class NetworkDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.no_network_text))
                .setMessage(context.getString(R.string.no_network_message))
                .setPositiveButton(context.getString(R.string.no_network_confirm), null);
        return builder.create();
    }
}
