package gr.grnet.academicid.inspector.domain;


@SuppressWarnings("UnusedDeclaration")
public class AcademicId {

    private long serialNumber;
    private String greekFirstname;
    private String greekLastname;
    private String latinFirstname;
    private String latinLastname;
    private String universityLocation;
    private String residenceLocation;
    private String pasoValidity;
    private boolean webSuccess;
    private String validationError;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public AcademicId() {
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getGreekFirstname() {
        return greekFirstname;
    }

    public void setGreekFirstname(String greekFirstname) {
        this.greekFirstname = greekFirstname;
    }

    public String getGreekLastname() {
        return greekLastname;
    }

    public void setGreekLastname(String greekLastname) {
        this.greekLastname = greekLastname;
    }

    public String getLatinFirstname() {
        return latinFirstname;
    }

    public void setLatinFirstname(String latinFirstname) {
        this.latinFirstname = latinFirstname;
    }

    public String getLatinLastname() {
        return latinLastname;
    }

    public void setLatinLastname(String latinLastname) {
        this.latinLastname = latinLastname;
    }

    public String getUniversityLocation() {
        return universityLocation;
    }

    public void setUniversityLocation(String universityLocation) {
        this.universityLocation = universityLocation;
    }

    public String getResidenceLocation() {
        return residenceLocation;
    }

    public void setResidenceLocation(String residenceLocation) {
        this.residenceLocation = residenceLocation;
    }

    public String getPasoValidity() {
        return pasoValidity;
    }

    public void setPasoValidity(String pasoValidity) {
        this.pasoValidity = pasoValidity;
    }

    public boolean isWebSuccess() {
        return webSuccess;
    }

    public void setWebSuccess(boolean webSuccess) {
        this.webSuccess = webSuccess;
    }

    public String getValidationError() {
        return validationError;
    }

    public void setValidationError(String validationError) {
        this.validationError = validationError;
    }

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public Boolean isPasoValid() {
        if (this.getPasoValidity().equals("Ναι")) return true;
        if (this.getPasoValidity().equals("Όχι")) return false;
        return null;
    }

    public boolean thereIsValidationError() {
        return ((validationError != null) && (!validationError.equals("")) && (!validationError.equals("null")));
    }
}