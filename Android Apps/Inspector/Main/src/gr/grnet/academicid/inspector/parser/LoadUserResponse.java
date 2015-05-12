package gr.grnet.academicid.inspector.parser;

import gr.grnet.academicid.inspector.domain.Inspector;

@SuppressWarnings("UnusedDeclaration")
public class LoadUserResponse {

    private String response;
    private String error;
    private Inspector inspector;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public LoadUserResponse(String response, String error, Inspector inspector) {
        this.response = response;
        this.error = error;
        this.inspector = inspector;
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

    public Inspector getInspector() {
        return inspector;
    }

    public void setInspector(Inspector inspector) {
        this.inspector = inspector;
    }

    public boolean isSuccessful() {
        return (response.equals(JSONParser.RESPONSE_SUCCESSFUL));
    }
}