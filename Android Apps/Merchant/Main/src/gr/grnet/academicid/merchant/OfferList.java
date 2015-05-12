package gr.grnet.academicid.merchant;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import gr.grnet.academicid.merchant.domain.Inspector;
import gr.grnet.academicid.merchant.domain.Offer;
import gr.grnet.academicid.merchant.parser.JSONParser;
import gr.grnet.academicid.merchant.parser.LoadProviderOffersResponse;
import gr.grnet.academicid.merchant.services.ServiceHandler;
import gr.grnet.academicid.merchant.utilities.Constants;
import gr.grnet.academicid.merchant.utilities.Tools;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OfferList extends ListActivity {

    private ListView listView;
    private TextView txtvOffersHeader;

    private ProgressDialog progressDialog;
    private List<Offer> activeOfferList;
    private List<Offer> inactiveOfferList;
    private boolean showingActive;
    private boolean doubleBackToExitPressedOnce = false;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_list);

        txtvOffersHeader = (TextView) findViewById(R.id.txt_offers_header);

        listView = getListView();

        // Listview on item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Offer offer = (showingActive ? activeOfferList.get(position) : inactiveOfferList.get(position));

                // Store selected offer for returning to Single Offer when pressing back in camera preview
                MerchantApplication.setSelectedOffer(offer);

                // Starting single offer activity
                Intent intent = new Intent(OfferList.this, SingleOffer.class);
                intent.putExtra(Offer.ID, offer.getId());
                intent.putExtra(Offer.TITLE, offer.getTitle());
                intent.putExtra(Offer.DESCRIPTION, offer.getDescription());
                intent.putExtra(Offer.STATUS, offer.getStatus());
                intent.putExtra(Offer.START_DATE, offer.getStartDate());
                intent.putExtra(Offer.END_DATE, offer.getEndDate());
                intent.putExtra(Offer.BENEFICIARIES, offer.getBeneficiaries());
                intent.putExtra(Offer.CRITERIA, offer.getCriteria());
                startActivity(intent);
            }
        });

        // Get inspector object from application class
        Inspector inspector = MerchantApplication.getInspector();
        new RetrieveOffers().execute(inspector.getUsername(), inspector.getPasswordHash());
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;
    }

    private class RetrieveOffers extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(OfferList.this);
            progressDialog.setMessage(getString(R.string.msg_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... credentials) {
            return ServiceHandler.makeLoadProviderOffersCall(credentials[0], credentials[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            LoadProviderOffersResponse loadProviderOffersResponse = JSONParser.getResultOfLoadProviderOffers(result);

            if (!loadProviderOffersResponse.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_error_retrieving_offers), Toast.LENGTH_SHORT).show();
                Log.e(Constants.LOGTAG, "OfferList.RetrieveOffers.OnPostExecute(): Web service returned unsuccessfully with errorCode: " + loadProviderOffersResponse.getError());
            } else {
                List<Offer> offerList = loadProviderOffersResponse.getOffers();
                activeOfferList = new ArrayList<Offer>();
                inactiveOfferList = new ArrayList<Offer>();

                // Split the offer list in active and inactive offers
                Tools.splitOffers(offerList, activeOfferList, inactiveOfferList);

                // Set flag to show the active offers by default
                showingActive = true;

                // Set the view that should be displayed if no offer exists
                listView.setEmptyView(findViewById(R.id.empty));

                // Prepare the list with offers that will be displayed
                List<Map<String, String>> adapterOfferList = Tools.prepareListForAdapter(activeOfferList);
                ListAdapter adapter = new SimpleAdapter(OfferList.this, adapterOfferList, R.layout.offer_list_item, new String[]{Offer.TITLE, Offer.DESCRIPTION}, new int[]{R.id.txtv_offer_item_title, R.id.txtv_offer_item_description});
                setListAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_offer_list, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle_offers:
                if (showingActive) {
                    showingActive = false;

                    item.setTitle(getString(R.string.lbl_active_offers));
                    item.setIcon(R.drawable.rating_important);

                    txtvOffersHeader.setText(R.string.lbl_inactive_offers);
                    txtvOffersHeader.setTextAppearance(this, R.style.header_orange);

                    List<Map<String, String>> adapterOfferList = Tools.prepareListForAdapter(inactiveOfferList);
                    ListAdapter adapter = new SimpleAdapter(OfferList.this, adapterOfferList, R.layout.offer_list_item, new String[]{Offer.TITLE, Offer.DESCRIPTION}, new int[]{R.id.txtv_offer_item_title, R.id.txtv_offer_item_description});
                    setListAdapter(adapter);
                } else {
                    showingActive = true;

                    item.setTitle(getString(R.string.lbl_inactive_offers));
                    item.setIcon(R.drawable.rating_not_important);

                    txtvOffersHeader.setText(R.string.lbl_active_offers);
                    txtvOffersHeader.setTextAppearance(this, R.style.header_green);

                    List<Map<String, String>> adapterOfferList = Tools.prepareListForAdapter(activeOfferList);
                    ListAdapter adapter = new SimpleAdapter(OfferList.this, adapterOfferList, R.layout.offer_list_item, new String[]{Offer.TITLE, Offer.DESCRIPTION}, new int[]{R.id.txtv_offer_item_title, R.id.txtv_offer_item_description});
                    setListAdapter(adapter);
                }
                return true;
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