package gr.grnet.academicid.inspector.parser;

import android.util.Log;
import gr.grnet.academicid.inspector.domain.*;
import gr.grnet.academicid.inspector.utilities.Constants;
import org.json.JSONException;
import org.json.JSONObject;

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
     * This method parses a json string and creates a {@link gr.grnet.academicid.inspector.parser.LoadUserResponse} object.
     * It is called after {@link gr.grnet.academicid.inspector.services.ServiceHandler#makeSingInCall(String, String)}
     *
     * @param jsonString The string that should be parsed.
     * @return A {@link gr.grnet.academicid.inspector.parser.LoadUserResponse} object.
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
     * This method parses a json string and creates a {@link gr.grnet.academicid.inspector.parser.LoadUserResponse} object.
     * It is called after {@link gr.grnet.academicid.inspector.services.ServiceHandler#makeInspectAcademicIdCall(String, String, String)}
     *
     * @param jsonString The string that should be parsed.
     * @return A {@link gr.grnet.academicid.inspector.parser.InspectAcademicIdResponse} object.
     */
    public static InspectAcademicIdResponse getResultOfInspectAcademicId(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String response = jsonObject.getString(TAG_RESPONSE);

            if (response.equals(RESPONSE_SUCCESSFUL)) {
                AcademicId academicId = new AcademicId();

                JSONObject inspectionResult = jsonObject.getJSONObject("inspectionResult");
                academicId.setSerialNumber(inspectionResult.getLong("academicId"));
                academicId.setGreekFirstname(inspectionResult.getString("greekFirstName"));
                academicId.setGreekLastname(inspectionResult.getString("greekLastName"));
                academicId.setLatinFirstname(inspectionResult.getString("latinFirstName"));
                academicId.setLatinLastname(inspectionResult.getString("latinLastName"));
                academicId.setUniversityLocation(inspectionResult.getString("universityLocation"));
                academicId.setResidenceLocation(inspectionResult.getString("residenceLocation"));
                academicId.setPasoValidity(inspectionResult.getString("pasoValidity"));
                academicId.setWebSuccess(inspectionResult.getBoolean("webServiceSuccess"));
                academicId.setValidationError(inspectionResult.getString("validationError"));

                return new InspectAcademicIdResponse(RESPONSE_SUCCESSFUL, null, academicId);
            } else {
                String error = jsonObject.getString(TAG_RESPONSE_ERROR);
                return new InspectAcademicIdResponse(RESPONSE_FAILED, error, null);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOGTAG, "JSONParser.getResultOfInspectAcademicId(): Error while parsing string: " + jsonString);
            return null;
        }
    }

    /**
     * This method parses a json string and creates a {@link gr.grnet.academicid.inspector.parser.ChangePasswordResponse} object.
     *
     * @param jsonString The string that should be parsed.
     * @return A {@link gr.grnet.academicid.inspector.parser.ChangePasswordResponse} object.
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