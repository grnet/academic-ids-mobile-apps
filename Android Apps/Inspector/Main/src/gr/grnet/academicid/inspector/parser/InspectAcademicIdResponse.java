package gr.grnet.academicid.inspector.parser;

import gr.grnet.academicid.inspector.domain.AcademicId;

@SuppressWarnings("UnusedDeclaration")
public class InspectAcademicIdResponse {

    private String response;
    private String error;
    private AcademicId academicId;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public InspectAcademicIdResponse(String response, String error, AcademicId academicId) {
        this.response = response;
        this.error = error;
        this.academicId = academicId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public AcademicId getAcademicId() {
        return academicId;
    }

    public void setAcademicId(AcademicId academicId) {
        this.academicId = academicId;
    }

    public boolean isSuccessful() {
        return (response.equals(JSONParser.RESPONSE_SUCCESSFUL));
    }
}