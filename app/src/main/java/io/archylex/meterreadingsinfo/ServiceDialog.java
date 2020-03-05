package io.archylex.meterreadingsinfo;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


public class ServiceDialog {
    private final Activity context;
    private MeterService new_ms;
    private String meterReading;

    public ServiceDialog(Activity context, DialogView dlgView, final MeterService ms, final int position) {
        this.context = context;
        this.new_ms = new MeterService();

        if (dlgView == DialogView.READING)
            showSendDialog(ms);
        else
            showAddServiceDialog(dlgView, ms, position);
    }

    private void showAddServiceDialog(DialogView dlgView, final MeterService ms, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View view = layoutInflaterAndroid.inflate(R.layout.add_service_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(view);

        final Spinner spinnerOrganization = view.findViewById(R.id.input_organization);
        final EditText editTextLogin = view.findViewById(R.id.input_login);
        final EditText editTextPassword = view.findViewById(R.id.input_password);
        final CheckBox checkBoxEnabled = view.findViewById(R.id.input_enabled);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message mesg)
            {
                throw new RuntimeException();
            }
        };

        dialogTitle.setText(dlgView == DialogView.CREATE ? context.getString(R.string.label_new_service_title) : context.getString(R.string.label_edit_service_title));

        if (dlgView == DialogView.UPDATE && ms != null) {
            editTextLogin.setText(ms.getLogin());
            editTextPassword.setText(ms.getPassword());
            checkBoxEnabled.setChecked(ms.getEnabled() == 1 ? true : false);
            spinnerOrganization.setSelection(ms.getImageId());
            this.new_ms = ms;
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(dlgView == DialogView.UPDATE ? context.getString(R.string.list_service_dlg_update) : context.getString(R.string.list_service_dlg_add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton(context.getString(R.string.list_service_dlg_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                new_ms = null;
                                handler.sendMessage(handler.obtainMessage());
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextLogin.getText().toString())) {
                    Toast.makeText(context, R.string.err_new_dlg_service_login, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();

                    new_ms.setOrganization(spinnerOrganization.getSelectedItem().toString());
                    new_ms.setLogin(editTextLogin.getText().toString());
                    new_ms.setPassword(editTextPassword.getText().toString());
                    new_ms.setEnabled(checkBoxEnabled.isChecked() ? 1 : 0);
                    new_ms.setImageId((int) spinnerOrganization.getSelectedItemId());

                    handler.sendMessage(handler.obtainMessage());
                }
            }
        });

        try {
            Looper.loop();
        } catch(RuntimeException e) {

        }
    }

    public MeterService getValues() {
        return new_ms;
    }


    private void showSendDialog(final MeterService ms) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View view = layoutInflaterAndroid.inflate(R.layout.send_reading_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(view);

        final EditText editTextReading = view.findViewById(R.id.input_reading);

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message mesg)
            {
                throw new RuntimeException();
            }
        };

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.label_send_reading_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton(context.getString(R.string.list_service_dlg_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                meterReading = null;
                                handler.sendMessage(handler.obtainMessage());
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextReading.getText().toString())) {
                    Toast.makeText(context, R.string.error_meter_reading, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    meterReading = editTextReading.getText().toString();
                    handler.sendMessage(handler.obtainMessage());
                    alertDialog.dismiss();
                }
            }
        });

        try {
            Looper.loop();
        } catch(RuntimeException e) {

        }
    }

    public String getMeterReading() {
        return meterReading;
    }

}
