package gr.grnet.academicid.merchant.parser;

@SuppressWarnings("UnusedDeclaration")
public class InspectProviderOfferResponse {

    private String response;
    private String error;
    private Long academicId;
    private Boolean offerValidity;
    private String errorDescription;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public InspectProviderOfferResponse(String response, String error) {
        this.response = response;
        this.error = error;
        this.academicId = null;
        this.offerValidity = null;
        this.errorDescription = null;
    }

    public InspectProviderOfferResponse(String response, Long academicId, Boolean offerValidity, String errorDescription) {
        this.response = response;
        this.error = null;
        this.academicId = academicId;
        this.offerValidity = offerValidity;
        this.errorDescription = errorDescription;
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

    public Long getAcademicId() {
        return academicId;
    }

    public void setAcademicId(Long academicId) {
        this.academicId = academicId;
    }

    public Boolean getOfferValidity() {
        return offerValidity;
    }

    public void setOfferValidity(Boolean offerValidity) {
        this.offerValidity = offerValidity;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public boolean isSuccessful() {
        return (response.equals(JSONParser.RESPONSE_SUCCESSFUL));
    }
}
