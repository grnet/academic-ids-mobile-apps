package gr.grnet.academicid.merchant.domain;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class Offer {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String BENEFICIARIES = "beneficiaries";
    public static final String AVAILABLE_ALL_PREFECTURES = "availableAllPrefectures";
    public static final String AVAILABLE_ALL_ACADEMICS = "availableAllAcademics";
    public static final String CRITERIA = "criteria";

    public static final int ACTIVE = 3;

    private long id;
    private String title;
    private String description;
    private int status;
    private long startDate;
    private Long endDate;
    private int beneficiaries;
    private boolean availableAllPrefectures;
    private List<Integer> prefectures;
    private boolean availableAllAcademics;
    private List<Integer> academics;
    private String criteria;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public Offer() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public int getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(int beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public boolean isAvailableAllPrefectures() {
        return availableAllPrefectures;
    }

    public void setAvailableAllPrefectures(boolean availableAllPrefectures) {
        this.availableAllPrefectures = availableAllPrefectures;
    }

    public List<Integer> getPrefectures() {
        return prefectures;
    }

    public void setPrefectures(List<Integer> prefectures) {
        this.prefectures = prefectures;
    }

    public boolean isAvailableAllAcademics() {
        return availableAllAcademics;
    }

    public void setAvailableAllAcademics(boolean availableAllAcademics) {
        this.availableAllAcademics = availableAllAcademics;
    }

    public List<Integer> getAcademics() {
        return academics;
    }

    public void setAcademics(List<Integer> academics) {
        this.academics = academics;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
}
