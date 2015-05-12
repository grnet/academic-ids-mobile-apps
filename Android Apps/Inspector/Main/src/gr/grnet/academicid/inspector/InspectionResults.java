package gr.grnet.academicid.inspector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import gr.grnet.academicid.inspector.domain.AcademicId;
import gr.grnet.academicid.inspector.domain.Inspector;
import gr.grnet.academicid.inspector.parser.InspectAcademicIdResponse;
import gr.grnet.academicid.inspector.parser.JSONParser;
import gr.grnet.academicid.inspector.services.ServiceHandler;
import gr.grnet.academicid.inspector.utilities.Constants;
import gr.grnet.academicid.inspector.utilities.Tools;
import org.jetbrains.annotations.NotNull;

public class InspectionResults extends Activity {

    private static final int SCAN_QR = 1;
    private static final int REFERRAL_QR = 1;
    private static final int REFERRAL_SERIAL = 2;

    private TextView lblInitMessage;
    private TextView txtvInspectionResult;
    private TextView lblSerialNumber;
    private TextView txtvSerialNumber;
    private TextView lblConcatName;
    private TextView txtvConcatName;
    private TextView lblUniversity;
    private TextView txtvUniversity;
    private TextView lblResidence;
    private TextView txtvResidence;
    private TextView lblInspectionDescription;
    private TextView txtvInspectionDescription;
    private EditText etxtSerialNumber;

    private ProgressDialog progressDialog;
    private int referral;
    private boolean doubleBackToExitPressedOnce = false;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspection_results);

        // Assign views to variables
        lblInitMessage = (TextView) findViewById(R.id.lbl_init_message);
        txtvInspectionResult = (TextView) findViewById(R.id.txtv_inspection_result);

        lblSerialNumber = (TextView) findViewById(R.id.lbl_serial_number);
        txtvSerialNumber = (TextView) findViewById(R.id.txtv_serial_number);

        lblConcatName = (TextView) findViewById(R.id.lbl_concat_name);
        txtvConcatName = (TextView) findViewById(R.id.txtv_concat_name);

        lblUniversity = (TextView) findViewById(R.id.lbl_university);
        txtvUniversity = (TextView) findViewById(R.id.txtv_university);

        lblResidence = (TextView) findViewById(R.id.lbl_residence);
        txtvResidence = (TextView) findViewById(R.id.txtv_residence);

        lblInspectionDescription = (TextView) findViewById(R.id.lbl_inspection_description);
        txtvInspectionDescription = (TextView) findViewById(R.id.txtv_inspection_description);

        etxtSerialNumber = (EditText) findViewById(R.id.etxt_serial_number);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;
    }

    // Method called after 'Scan QR' button is pressed
    public void scanQR(View view) {
        Intent intent = new Intent(this, ScanQR.class);
        startActivityForResult(intent, SCAN_QR);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_QR) {
            if (resultCode == RESULT_OK) {
                String scanResult = data.getStringExtra(ScanQR.SCAN_RESULT);
                referral = REFERRAL_QR;
                validate(scanResult);
            }
        }
    }

    // Method called after 'Magnifier' button is pressed
    public void checkSerialNo(View view) {
        // After 'Check Id' button is pressed, hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String input = etxtSerialNumber.getText().toString();
        referral = REFERRAL_SERIAL;
        validate(input);
    }

    // Method called after 'X' button is pressed
    public void clearContent(View view) {
        etxtSerialNumber.setText("");
    }

    private void validate(String academicId) {
        if ((academicId.length() == 12) && Tools.isNumericValue(academicId)) {
            Inspector inspector = InspectorApplication.getInspector();
            new ValidateAcademicId().execute(inspector.getUsername(), inspector.getPasswordHash(), academicId);
        } else {
            // Hide most of the views...
            lblInitMessage.setVisibility(View.GONE);
            lblSerialNumber.setVisibility(View.GONE);
            txtvSerialNumber.setVisibility(View.GONE);
            lblConcatName.setVisibility(View.GONE);
            txtvConcatName.setVisibility(View.GONE);
            lblUniversity.setVisibility(View.GONE);
            txtvUniversity.setVisibility(View.GONE);
            lblResidence.setVisibility(View.GONE);
            txtvResidence.setVisibility(View.GONE);
            lblInspectionDescription.setVisibility(View.GONE);

            if (referral == REFERRAL_QR)
                etxtSerialNumber.setText("");

            // ... and present only 'Invalid' message and reason
            txtvInspectionResult.setVisibility(View.VISIBLE);
            txtvInspectionResult.setTextAppearance(getApplicationContext(), R.style.intro_blurb_orange);
            txtvInspectionResult.setText(R.string.lbl_invalid);

            txtvInspectionDescription.setVisibility(View.VISIBLE);
            txtvInspectionDescription.setText(R.string.msg_invalid_input);
        }
    }

    private class ValidateAcademicId extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(InspectionResults.this);
            progressDialog.setMessage(getString(R.string.msg_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... variables) {
            return ServiceHandler.makeInspectAcademicIdCall(variables[0], variables[1], variables[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            InspectAcademicIdResponse inspectAcademicIdResponse = JSONParser.getResultOfInspectAcademicId(result);

            if (!inspectAcademicIdResponse.isSuccessful()) {
                Toast.makeText(getApplicationContext(), R.string.msg_error_inspecting_academicId, Toast.LENGTH_SHORT).show();
                Log.e(Constants.LOGTAG, "InspectionResults.ValidateAcademicId.onPostExecute(): Web service returned unsuccessfully with errorCode: " + inspectAcademicIdResponse.getError());
            } else {
                AcademicId academicId = inspectAcademicIdResponse.getAcademicId();

                lblInitMessage.setVisibility(View.GONE);
                txtvInspectionResult.setVisibility(View.VISIBLE);

                lblSerialNumber.setVisibility(View.VISIBLE);
                txtvSerialNumber.setVisibility(View.VISIBLE);
                txtvSerialNumber.setText(String.valueOf(academicId.getSerialNumber()));

                lblConcatName.setVisibility(View.VISIBLE);
                txtvConcatName.setVisibility(View.VISIBLE);
                txtvConcatName.setText(Tools.concatenateName(academicId));

                lblUniversity.setVisibility(View.VISIBLE);
                txtvUniversity.setVisibility(View.VISIBLE);
                txtvUniversity.setText(academicId.getUniversityLocation());

                lblResidence.setVisibility(View.VISIBLE);
                txtvResidence.setVisibility(View.VISIBLE);
                txtvResidence.setText(academicId.getResidenceLocation());

                if (referral == REFERRAL_QR)
                    etxtSerialNumber.setText("");


                if (academicId.isPasoValid() != null && academicId.isPasoValid()) { // If Academic Id is eligible as a a discount voucher
                    // show 'Valid' message...
                    txtvInspectionResult.setTextAppearance(getApplicationContext(), R.style.intro_blurb_green);
                    txtvInspectionResult.setText(R.string.lbl_valid);

                    // ... and hide inspection description view (which is empty)
                    lblInspectionDescription.setVisibility(View.GONE);
                    txtvInspectionDescription.setVisibility(View.GONE);

                } else { // If academic Id is not eligible as a discount voucher
                    // show 'Invalid' message...
                    txtvInspectionResult.setTextAppearance(getApplicationContext(), R.style.intro_blurb_orange);
                    txtvInspectionResult.setText(R.string.lbl_invalid);

                    if (academicId.thereIsValidationError()) { // ... and if there is a reason why it not valid
                        // then display it
                        lblInspectionDescription.setVisibility(View.VISIBLE);
                        lblInspectionDescription.setTextAppearance(getApplicationContext(), R.style.label_orange);

                        txtvInspectionDescription.setVisibility(View.VISIBLE);
                        txtvInspectionDescription.setText(academicId.getValidationError());
                    } else { // or make view invisible
                        lblInspectionDescription.setVisibility(View.GONE);
                        txtvInspectionDescription.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_application, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, Preferences.class));
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.lbl_exit, Toast.LENGTH_SHORT).show();
        }
    }
}