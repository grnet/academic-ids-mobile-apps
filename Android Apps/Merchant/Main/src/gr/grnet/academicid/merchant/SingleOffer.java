package gr.grnet.academicid.merchant;

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
import android.widget.*;
import gr.grnet.academicid.merchant.domain.Inspector;
import gr.grnet.academicid.merchant.domain.Offer;
import gr.grnet.academicid.merchant.parser.InspectProviderOfferResponse;
import gr.grnet.academicid.merchant.parser.JSONParser;
import gr.grnet.academicid.merchant.services.ServiceHandler;
import gr.grnet.academicid.merchant.utilities.Constants;
import gr.grnet.academicid.merchant.utilities.Tools;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleOffer extends Activity {

    private static final int SCAN_QR = 1;
    private static final String TAG = "SERIAL_TAG";

    private TextView txtvInspectionResult;
    private TextView txtvInspectionResultError;

    private ProgressDialog progressDialog;
    private long offerId;
    private int offerStatus;
    private int offerBeneficiaries;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_offer);

        // Assign views to variables
        TextView txtvOfferTitle = (TextView) findViewById(R.id.txtv_offer_title);
        TextView txtvOfferDescription = (TextView) findViewById(R.id.txtv_offer_description);
        TextView txtvOfferStartDate = (TextView) findViewById(R.id.txtv_offer_startDate);
        TextView txtvOfferEndDate = (TextView) findViewById(R.id.txtv_offer_endDate);
        TextView txtOfferCriteria = (TextView) findViewById(R.id.txtv_offer_criteria);

        txtvInspectionResult = (TextView) findViewById(R.id.txtv_offer_inspectionResult);
        txtvInspectionResultError = (TextView) findViewById(R.id.txtv_offer_inspectionResultError);

        Button btnScanQR = (Button) findViewById(R.id.btn_scan_qr);
        RelativeLayout rellaySerialInput = (RelativeLayout) findViewById(R.id.rellay_serial_input);


        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        // getting intent data
        Intent intent = getIntent();

        // Get values from intent extras
        offerId = intent.getLongExtra(Offer.ID, -1L);
        String offerTitle = intent.getStringExtra(Offer.TITLE);
        String offerDescription = intent.getStringExtra(Offer.DESCRIPTION);
        offerStatus = intent.getIntExtra(Offer.STATUS, 0);
        offerBeneficiaries = intent.getIntExtra(Offer.BENEFICIARIES, 0);
        Long offerStartDate = intent.getLongExtra(Offer.START_DATE, 0L);
        Long offerEndDate = intent.getLongExtra(Offer.END_DATE, 0L);
        String offerCriteria = intent.getStringExtra(Offer.CRITERIA);

        // Format values to display them on screen
        String offerStartDateStr = format.format(new Date(offerStartDate));
        String offerEndDateStr = format.format(new Date(offerEndDate));
        String newOfferCriteria = offerCriteria.replaceAll("<br>", "\n");

        // Displaying offer values on screen
        txtvOfferTitle.setText(offerTitle);
        txtvOfferDescription.setText(offerDescription);
        txtvOfferStartDate.setText(offerStartDateStr);
        txtvOfferEndDate.setText(offerEndDateStr);
        txtOfferCriteria.setText(newOfferCriteria);

        // If offer is active then show ScanQR and InputSerial views for validating an offer
        if (offerStatus == Offer.ACTIVE) {
            txtvOfferTitle.setTextAppearance(this, R.style.title_green);
        } else {
            txtvOfferTitle.setTextAppearance(this, R.style.title_orange);

            btnScanQR.setVisibility(View.GONE);
            rellaySerialInput.setVisibility(View.GONE);
        }
    }

    public void scanQR(View view) {
        Intent intent = new Intent(this, ScanQR.class);
        startActivityForResult(intent, SCAN_QR);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_QR) {
            if (resultCode == RESULT_OK) {

                String scanResult = data.getStringExtra(ScanQR.SCAN_RESULT);
                validate(scanResult);
            }
        }
    }

    // Method called after 'Check Serial Number' button is pressed
    public void checkSerialNo(View view) {
        // After 'Check Id' button is pressed, hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String input = ((EditText) findViewById(R.id.etxt_serial_number)).getText().toString();
        validate(input);
    }

    private void validate(String academicId) {
        // The passed parameter should be a 12 digits numeric value. Otherwise show invalid Academic ID without making an network call
        if ((academicId.length() == 12) && Tools.isNumericValue(academicId)) {
            Inspector inspector = MerchantApplication.getInspector();
            Offer offer = new Offer();

            offer.setId(offerId);
            offer.setStatus(offerStatus);
            offer.setBeneficiaries(offerBeneficiaries);
            offer.setAvailableAllAcademics(true);

            new ValidateOffer().execute(inspector.getUsername(), inspector.getPasswordHash(), offer, academicId);
        } else {
            txtvInspectionResult.setVisibility(View.VISIBLE);
            txtvInspectionResult.setTextAppearance(this, R.style.color_orange);
            txtvInspectionResult.setText(R.string.msg_invalid_input);
        }
    }

    private class ValidateOffer extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(SingleOffer.this);
            progressDialog.setMessage(getString(R.string.msg_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Object... variables) {
            return ServiceHandler.makeInspectProviderOfferCall((String) variables[0], (String) variables[1], (Offer) variables[2], (String) variables[3]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            InspectProviderOfferResponse inspectProviderOfferResponse = JSONParser.getResultsOfInspectProviderOffer(result);

            if (!inspectProviderOfferResponse.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_error_validating_offer), Toast.LENGTH_SHORT).show();
                Log.e(Constants.LOGTAG, "SingleOffer.ValidateOffer.OnPostExecute(): Web service returned unsuccessfully with errorCode: " + inspectProviderOfferResponse.getError());
            } else {
                String inspectionResultBuilder;

                if (inspectProviderOfferResponse.getOfferValidity()) {
                    inspectionResultBuilder = getResources().getString(R.string.msg_eligible_for_offer);

                    txtvInspectionResult.setTextAppearance(SingleOffer.this, R.style.color_green);
                    txtvInspectionResultError.setVisibility(View.GONE);
                } else {
                    inspectionResultBuilder = getResources().getString(R.string.msg_not_eligible_for_offer);

                    txtvInspectionResult.setTextAppearance(SingleOffer.this, R.style.color_orange);
                    txtvInspectionResultError.setVisibility(View.VISIBLE);
                    txtvInspectionResultError.setText(inspectProviderOfferResponse.getErrorDescription());
                }

                txtvInspectionResult.setVisibility(View.VISIBLE);
                inspectionResultBuilder = inspectionResultBuilder.replace(TAG, inspectProviderOfferResponse.getAcademicId().toString());
                txtvInspectionResult.setText(inspectionResultBuilder);
            }
        }
    }

    // Method called after 'Clear Content' button is pressed
    public void clearContent(View view) {
        EditText serialInput = (EditText) findViewById(R.id.etxt_serial_number);
        serialInput.setText("");

        txtvInspectionResult.setVisibility(View.GONE);
        txtvInspectionResultError.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_single_offer, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_offers:
                startActivity(new Intent(this, OfferList.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, Preferences.class));
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }
}