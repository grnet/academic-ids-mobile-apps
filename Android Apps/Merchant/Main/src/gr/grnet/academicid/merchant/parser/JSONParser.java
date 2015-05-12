package gr.grnet.academicid.merchant.parser;

import android.util.Log;
import gr.grnet.academicid.merchant.domain.*;
import gr.grnet.academicid.merchant.utilities.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    // JSON Tags
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_RESPONSE_ERROR = "errorReason";

    public static final String RESPONSE_SUCCESSFUL = "SUCCESS";
    public static final String RESPONSE_FAILED = "FAILURE";

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    /**
     * Prevents this class from being instantiated.
     */
    private JSONParser() {
    }

    /**
     * This method parses a json string and creates a {@link gr.grnet.academicid.merchant.parser.LoadUserResponse} object.
     * It is called after {@link gr.grnet.academicid.merchant.services.ServiceHandler#makeSingInCall(String, String)}
     *
     * @param jsonString The string that should be parsed.
     * @return A {@link gr.grnet.academicid.merchant.parser.LoadUserResponse} object.
     */
    public static LoadUserResponse getResultOfLoadUser(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String response = jsonObject.getString(TAG_RESPONSE);

            if (response.equals(RESPONSE_SUCCESSFUL)) {
                Inspector inspector = new Inspector();

                JSONObject user = jsonObject.getJSONObject("user");
                inspector.setFirstname(user.getString("firstName"));
                inspector.setLastname(user.getString("lastName"));
                inspector.setUsername(user.getString("username"));
                inspector.setChangePswAtLogin(user.getBoolean("changePassword"));

                JSONObject organization = user.getJSONObject("organization");
                inspector.setOrgId(organization.getLong("id"));
                inspector.setOrgName(organization.getString("name"));
                inspector.setOrgDescription(organization.getString("description"));

                return new LoadUserResponse(RESPONSE_SUCCESSFUL, null, inspector);
            } else {
                String error = jsonObject.getString(TAG_RESPONSE_ERROR);
                return new LoadUserResponse(RESPONSE_FAILED, error, null);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "JSONParser.getResultOfLoadUser(): Error while parsing string: " + jsonString);
            return null;
        }
    }

    /**
     * This method parses a json string and creates a {@link gr.grnet.academicid.merchant.parser.LoadProviderOffersResponse} object
     * It is called after {@link gr.grnet.academicid.merchant.services.ServiceHandler#makeLoadProviderOffersCall(String, String)}
     *
     * @param jsonString The string that should be parsed.
     * @return A {@link gr.grnet.academicid.merchant.parser.LoadProviderOffersResponse} object.
     */
    public static LoadProviderOffersResponse getResultOfLoadProviderOffers(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String response = jsonObject.getString(TAG_RESPONSE);

            if (response.equals(RESPONSE_SUCCESSFUL)) {
                List<Offer> offerList = new ArrayList<Offer>();
                JSONArray jsonArray = jsonObject.getJSONArray("discountOffers");

                for (int i = 0; i < jsonArray.length(); i++) {
                    Offer offer = new Offer();
                    JSONObject jsonOffer = jsonArray.getJSONObject(i);

                    offer.setId(jsonOffer.getLong("offerId"));
                    offer.setTitle(jsonOffer.getString("title"));
                    offer.setDescription(jsonOffer.getString("description"));
                    offer.setStatus(jsonOffer.getInt("offerStatus"));
                    offer.setStartDate(jsonOffer.getLong("startDate"));
                    offer.setEndDate(jsonOffer.getLong("endDate"));
                    offer.setBeneficiaries(jsonOffer.getInt("beneficiaries"));
                    offer.setAvailableAllPrefectures(jsonOffer.getBoolean("availableToAllPrefectures"));
                    offer.setAvailableAllAcademics(jsonOffer.getBoolean("availableToAllAcademics"));
                    offer.setCriteria(jsonOffer.getString("criteria"));

                    offerList.add(offer);
                }

                return new LoadProviderOffersResponse(RESPONSE_SUCCESSFUL, null, offerList);
            } else {
                String error = jsonObject.getString(TAG_RESPONSE_ERROR);
                return new LoadProviderOffersResponse(RESPONSE_FAILED, error, null);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "JSONParser.LoadProviderOffersResponse(): Error while parsing string: " + jsonString);
            return null;
        }
    }

    /**
     * This method parses a json string and creates a {@link gr.grnet.academicid.merchant.parser.InspectProviderOfferResponse} object
     * It is called after {@link gr.grnet.academicid.merchant.services.ServiceHandler#makeInspectProviderOfferCall(String, String, gr.grnet.academicid.merchant.domain.Offer, String)}
     *
     * @param jsonString The string that should be parsed.
     * @return A {@link gr.grnet.academicid.merchant.parser.InspectProviderOfferResponse} object.
     */
    public static InspectProviderOfferResponse getResultsOfInspectProviderOffer(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String response = jsonObject.getString(TAG_RESPONSE);

            if (response.equals(RESPONSE_SUCCESSFUL)) {
                JSONObject offerInspection = jsonObject.getJSONObject("inspectionResult");
                Long academicId = Long.valueOf(offerInspection.getString("academicId"));
                Boolean validOffer = offerInspection.getBoolean("valid");
                String errorDescription = offerInspection.getString("error");

                return new InspectProviderOfferResponse(RESPONSE_SUCCESSFUL, academicId, validOffer, errorDescription);
            } else {
                String error = jsonObject.getString(TAG_RESPONSE_ERROR);
                return new InspectProviderOfferResponse(RESPONSE_FAILED, error);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "JSONParser.LoadProviderOffersResponse(): Error while parsing string: " + jsonString);
            return null;
        }
    }

    /**
     * This method parses a json string and creates a {@link gr.grnet.academicid.merchant.parser.ChangePasswordResponse} object.
     *
     * @param jsonString The string that should be parsed.
     * @return A {@link gr.grnet.academicid.merchant.parser.ChangePasswordResponse} object.
     */
    public static ChangePasswordResponse getResultOfUpdatePassword(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String response = jsonObject.getString(TAG_RESPONSE);

            if (response.equals(RESPONSE_SUCCESSFUL)) {
                return new ChangePasswordResponse(RESPONSE_SUCCESSFUL, null);
            } else {
                String error = jsonObject.getString(TAG_RESPONSE_ERROR);
                return new ChangePasswordResponse(RESPONSE_FAILED, error);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "JSONParser.getResultOfUpdatePassword(): Error while parsing string: " + jsonString);
            return null;
        }
    }
}
