package gr.grnet.academicid.inspector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import gr.grnet.academicid.inspector.domain.Inspector;
import gr.grnet.academicid.inspector.parser.ChangePasswordResponse;
import gr.grnet.academicid.inspector.parser.JSONParser;
import gr.grnet.academicid.inspector.services.ServiceHandler;
import gr.grnet.academicid.inspector.utilities.Constants;
import gr.grnet.academicid.inspector.utilities.Tools;

public class ChangePassword extends Activity {

    private EditText etxtCurrentPassword;
    private EditText etxtNewPassword;
    private EditText etxtNewPasswordConfirmation;

    private ProgressDialog progressDialog;
    private Inspector inspector;
    private String newPasswordHash;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        etxtCurrentPassword = (EditText) findViewById(R.id.etxt_current_password);
        etxtNewPassword = (EditText) findViewById(R.id.etxt_new_password);
        etxtNewPasswordConfirmation = (EditText) findViewById(R.id.etxt_new_password_confirmation);

        inspector = InspectorApplication.getInspector();
        if (inspector.shouldChangePswAtLogin())
            Toast.makeText(getApplicationContext(), getString(R.string.msg_password_change_required), Toast.LENGTH_LONG).show();
    }

    public void cancel(View view) {
        finish();
    }

    public void confirm(View view) {
        // After 'Sign In' button is pressed, hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String currentPasswordHash = Tools.getHash(etxtCurrentPassword.getText().toString());
        String newPassword = etxtNewPassword.getText().toString();
        newPasswordHash = Tools.getHash(newPassword);
        String newPasswordConfirmationHash = Tools.getHash(etxtNewPasswordConfirmation.getText().toString());

        inspector = InspectorApplication.getInspector();

        // Check if the Current password is the actual password of the user by comparing the hash values.
        if (!inspector.getPasswordHash().equals(currentPasswordHash)) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_incorrect_current_password), Toast.LENGTH_SHORT).show();
        }
        // Check if the new password and the new password confirmation match by comparing the hash values.
        else if (!newPasswordHash.equals(newPasswordConfirmationHash)) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_new_passwords_dont_match), Toast.LENGTH_SHORT).show();
        }
        // Check if new password is at least 4 chars long.
        else if (newPassword.length() < 4) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_password_too_short), Toast.LENGTH_SHORT).show();
        }
        // Check if new password is the same with current password by comparing the hash values.
        else if (currentPasswordHash.equals(newPasswordHash)) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_new_current_password_same), Toast.LENGTH_SHORT).show();
        } else {
            new UpdatePassword().execute(inspector.getUsername(), inspector.getPasswordHash(), newPasswordHash);
        }
    }

    private class UpdatePassword extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(ChangePassword.this);
            progressDialog.setMessage(getString(R.string.msg_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... variables) {
            return ServiceHandler.makeChangePswCall(variables[0], variables[1], variables[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            ChangePasswordResponse changePasswordResponse = JSONParser.getResultOfUpdatePassword(result);

            if (!changePasswordResponse.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_error_changing_password), Toast.LENGTH_SHORT).show();
                Log.e(Constants.LOGTAG, "ChangePassword.UpdatePassword.OnPostExecute(): Web service returned unsuccessfully with errorCode: " + changePasswordResponse.getError());
            } else {
                Log.i(Constants.LOGTAG, "ChangePassword.UpdatePassword.OnPostExecute(): User " + inspector.getUsername() + " successfully changed his password.");

                inspector = InspectorApplication.getInspector();
                boolean userChangedPsw = inspector.shouldChangePswAtLogin();
                inspector.setPasswordHash(newPasswordHash);
                inspector.setChangePswAtLogin(false);
                InspectorApplication.setInspector(inspector);

                if (userChangedPsw)
                    startActivity(new Intent(ChangePassword.this, InspectionResults.class));
                else
                    finish();
            }
        }
    }
}