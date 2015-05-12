package gr.grnet.academicid.merchant.parser;

import gr.grnet.academicid.merchant.domain.Offer;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class LoadProviderOffersResponse {

    private String response;
    private String error;
    private List<Offer> offers;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public LoadProviderOffersResponse(String response, String error, List<Offer> offers) {
        this.response = response;
        this.error = error;
        this.offers = offers;
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

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public boolean isSuccessful() {
        return (response.equals(JSONParser.RESPONSE_SUCCESSFUL));
    }
}
