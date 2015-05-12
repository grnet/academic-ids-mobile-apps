package gr.grnet.academicid.inspector.domain;

public class Inspector {

    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String USERNAME = "username";
    public static final String PASSWORD_HASH = "passwordHash";
    public static final String CHANGE_PSW_AT_LOGIN = "changePassword";
    public static final String ORG_ID = "orgId";
    public static final String ORG_NAME = "orgName";
    public static final String ORG_DESCRIPTION = "orgDescription";

    private String firstname;
    private String lastname;
    private String username;
    private String passwordHash;
    private boolean changePswAtLogin;
    private Long orgId;
    private String orgName;
    private String orgDescription;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public Inspector() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean shouldChangePswAtLogin() {
        return changePswAtLogin;
    }

    public void setChangePswAtLogin(boolean changePswAtLogin) {
        this.changePswAtLogin = changePswAtLogin;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgDescription() {
        return orgDescription;
    }

    public void setOrgDescription(String orgDescription) {
        this.orgDescription = orgDescription;
    }
}