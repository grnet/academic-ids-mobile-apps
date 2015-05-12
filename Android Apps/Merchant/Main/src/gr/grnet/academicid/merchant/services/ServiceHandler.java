package gr.grnet.academicid.merchant.services;

import android.util.Log;
import gr.grnet.academicid.merchant.domain.Offer;
import gr.grnet.academicid.merchant.utilities.Constants;
import gr.grnet.academicid.merchant.utilities.Tools;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ServiceHandler {

    // The URL of the server that handles the requests
    private static final String URL = "http://academicidapp.grnet.gr:8080/";
//    private static final String URL = "http://academicidappbuilder.grnet.gr:8080/";

    // Path of web services
    private static final String PATH = "admin/web/ws/users/";

    // Each string represents a different call to a web service
    private static final String LOAD_USER = "loadUser"; // Get method
    private static final String LOAD_PROVIDER_OFFERS = "loadProviderOffers"; // Get method
    private static final String INSPECT_PROVIDER_OFFER = "inspectProviderOffer"; // Post method
    private static final String CHANGE_PSW = "updatePassword"; // Post method

    // This is the content type dictated by server side.
    private static final String DEFAULT_CONTENT_TYPE = "application/json";

    // This is the accepted encoding dictated by server side.
    private static final String ACCEPTED_ENCODING = "application/json";

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    /**
     * Prevents this class from being instantiated.
     */
    private ServiceHandler() {
    }

    /**
     * This method verifies that a user is a valid user.
     *
     * @param username     The username of the user.
     * @param passwordHash The password of the user hashed by {@link Tools#getHash(String)}.
     * @return A json string.
     */
    public static String makeSingInCall(String username, String passwordHash) {
        try {
            HttpGet httpMethod = new HttpGet(URL + PATH + LOAD_USER);
            httpMethod.addHeader("Content-Type", DEFAULT_CONTENT_TYPE);
            httpMethod.addHeader("Accept-Encoding", ACCEPTED_ENCODING);

            String credentialsEncoded = Tools.encodeBase64(username, passwordHash);
            httpMethod.addHeader("Authorization", "Basic " + credentialsEncoded);

            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpMethod);
            HttpEntity httpEntity = httpResponse.getEntity();

            return EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            Log.e(Constants.LOGTAG, "ServiceHandler.makeSignInCall(): Error while user: " + username + " trying to sign in ", e);
            return null;
        }
    }

    /**
     * This method retrieves the offers of the provider that user belongs to.
     *
     * @param username     The username of the user.
     * @param passwordHash The password of the user hashed by {@link Tools#getHash(String)}.
     * @return a json string.
     */
    public static String makeLoadProviderOffersCall(String username, String passwordHash) {
        try {
            HttpGet httpMethod = new HttpGet(URL + PATH + LOAD_PROVIDER_OFFERS);
            httpMethod.addHeader("Content-Type", DEFAULT_CONTENT_TYPE);
            httpMethod.addHeader("Accept-Encoding", ACCEPTED_ENCODING);

            String credentialsEncoded = Tools.encodeBase64(username, passwordHash);
            httpMethod.addHeader("Authorization", "Basic " + credentialsEncoded);

            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpMethod);
            HttpEntity httpEntity = httpResponse.getEntity();

            return EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            Log.e(Constants.LOGTAG, "ServiceHandler.makeLoadProviderOffersCall(): Error while user: " + username + " trying to retrieve provider offers", e);
            return null;
        }
    }

    /**
     * This method verifies if an owner of an academic ID is eligible of an offer.
     *
     * @param username     The username of the user.
     * @param passwordHash The password of the user hashed by {@link Tools#getHash(String)}.
     * @param offer        The offer for which we enquire if the owner of the academic Id is eligible.
     * @param academicId   The serial number of the Academic ID card.
     * @return a json string.
     */
    public static String makeInspectProviderOfferCall(String username, String passwordHash, Offer offer, String academicId) {
        try {
            HttpPost httpMethod = new HttpPost(URL + PATH + INSPECT_PROVIDER_OFFER);
            httpMethod.addHeader("Content-Type", DEFAULT_CONTENT_TYPE);
            httpMethod.addHeader("Accept-Encoding", ACCEPTED_ENCODING);

            String credentialsEncoded = Tools.encodeBase64(username, passwordHash);
            httpMethod.addHeader("Authorization", "Basic " + credentialsEncoded);

            JSONObject jsonOffer = new JSONObject();
            jsonOffer.put("offerId", offer.getId());
            jsonOffer.put("offerStatus", offer.getStatus());
            jsonOffer.put("beneficiaries", offer.getBeneficiaries());
            jsonOffer.put("availableToAllAcademics", offer.isAvailableAllAcademics());
            jsonOffer.put("academicID", academicId);

            httpMethod.setEntity(new StringEntity(jsonOffer.toString()));

            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpMethod);
            HttpEntity httpEntity = httpResponse.getEntity();

            return EntityUtils.toString(httpEntity);
        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "ServiceHandler.makeInspectProviderOfferCall(): Error while trying to construct JSON Offer", e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.LOGTAG, "ServiceHandler.makeInspectProviderOfferCall(): Error while user: " + username + " trying to validate AcademicID: " + academicId + " with offer: " + offer.getId(), e);
            return null;
        }
    }

    /**
     * This method performs a password change request.
     *
     * @param username        The username of the user.
     * @param passwordHash    The password of the user hashed by {@link Tools#getHash(String)}.
     * @param newPasswordHash The new password of the user hashed by {@link Tools#getHash(String)}.
     * @return A json string.
     */
    public static String makeChangePswCall(String username, String passwordHash, String newPasswordHash) {
        try {
            HttpPost httpMethod = new HttpPost(URL + PATH + CHANGE_PSW);
            httpMethod.addHeader("Content-Type", DEFAULT_CONTENT_TYPE);
            httpMethod.addHeader("Accept-Encoding", ACCEPTED_ENCODING);

            String credentialsEncoded = Tools.encodeBase64(username, passwordHash);
            httpMethod.addHeader("Authorization", "Basic " + credentialsEncoded);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("newPassword", newPasswordHash);

            httpMethod.setEntity(new StringEntity(jsonObject.toString()));

            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpMethod);
            HttpEntity httpEntity = httpResponse.getEntity();

            return EntityUtils.toString(httpEntity);
        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "ServiceHandler.makeChangePswCall(): Error while trying to construct JSON string", e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.LOGTAG, "ServiceHandler.makeChangePswCall(): Error while user: " + username + " trying to change password", e);
            return null;
        }
    }
}
