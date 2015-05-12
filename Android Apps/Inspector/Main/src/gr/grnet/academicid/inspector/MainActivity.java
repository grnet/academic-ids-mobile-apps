package gr.grnet.academicid.inspector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import gr.grnet.academicid.inspector.domain.Inspector;
import gr.grnet.academicid.inspector.parser.LoadUserResponse;
import gr.grnet.academicid.inspector.services.ServiceHandler;
import gr.grnet.academicid.inspector.parser.JSONParser;
import gr.grnet.academicid.inspector.utilities.Constants;
import gr.grnet.academicid.inspector.utilities.Tools;

public class MainActivity extends Activity {

    private EditText etxtUsername;
    private EditText etxtPassword;

    private ProgressDialog progressDialog;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        etxtUsername = (EditText) findViewById(R.id.etxt_username);
        etxtPassword = (EditText) findViewById(R.id.etxt_password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Tools.isNetworkAvailable()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.title_no_net_available));
            alertDialog.setMessage(getString(R.string.msg_no_net_available));

            alertDialog.setPositiveButton(getString(R.string.action_next), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            alertDialog.setNegativeButton(getString(R.string.action_close), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
        }
    }

    // Method called after 'Sign In' button is pressed
    public void signIn(View view) {
        // After 'Sign In' button is pressed, hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String username = etxtUsername.getText().toString();
        String password = etxtPassword.getText().toString();

        // Password is hashed and passed to async task for authorization
        new Authenticate().execute(username, Tools.getHash(password));
    }

    // Inner class to perform the network connection asynchronously
    private class Authenticate extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.msg_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... credentials) {
            return ServiceHandler.makeSingInCall(credentials[0], credentials[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            LoadUserResponse loadUserResponse = JSONParser.getResultOfLoadUser(result);

            if (!loadUserResponse.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_incorrect_credentials), Toast.LENGTH_SHORT).show();
                Log.e(Constants.LOGTAG, "MainActivity.Authenticate.OnPostExecute(): Web service returned unsuccessfully with errorCode: " + loadUserResponse.getError());
            } else {
                Inspector inspector = loadUserResponse.getInspector();
                String password = etxtPassword.getText().toString();
                inspector.setPasswordHash(Tools.getHash(password));
                InspectorApplication.setInspector(inspector);

                Log.i(Constants.LOGTAG, "User: " + inspector.getUsername() + "successfully logged in");

                // Check if user should change his password and redirect him to that view
                if (inspector.shouldChangePswAtLogin()) {
                    startActivity(new Intent(MainActivity.this, ChangePassword.class));
                } else {
                    startActivity(new Intent(MainActivity.this, InspectionResults.class));
                }
            }
        }
    }
}
