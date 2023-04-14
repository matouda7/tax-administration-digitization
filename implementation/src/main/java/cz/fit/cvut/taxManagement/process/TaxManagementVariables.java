package cz.fit.cvut.taxManagement.process;

public class TaxManagementVariables {
    private String personName;
    private String personAddress;
    private String personEmail;
    private String personPhone;
    private String decisionId;
    private String additionalInformation;
    private String presentedFacts;
    private String testimonies;
    private String expertReport;
    private String specialRecords;
    private String participantContributions;
    private String negotiatedTaxValue;
    private String negotiatedTaxJustification;
    private String taxAmount;
    private String taxJustification;

    public String getPersonName() {
        return personName;
    }

    public TaxManagementVariables setPersonName(String personName) {
        this.personName = personName;
        return this;
    }

    public String getPersonAddress() {
        return personAddress;
    }

    public TaxManagementVariables setPersonAddress(String personAddress) {
        this.personAddress = personAddress;
        return this;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public TaxManagementVariables setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
        return this;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public TaxManagementVariables setPersonPhone(String personPhone) {
        this.personPhone = personPhone;
        return this;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public TaxManagementVariables setDecisionId(String decisionId) {
        this.decisionId = decisionId;
        return this;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public TaxManagementVariables setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public TaxManagementVariables setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
        return this;
    }

    public String getTaxJustification() {
        return taxJustification;
    }

    public TaxManagementVariables setTaxJustification(String taxJustification) {
        this.taxJustification = taxJustification;
        return this;
    }

    public String getPresentedFacts() {
        return presentedFacts;
    }

    public TaxManagementVariables setPresentedFacts(String presentedFacts) {
        this.presentedFacts = presentedFacts;
        return this;
    }

    public String getTestimonies() {
        return testimonies;
    }

    public TaxManagementVariables setTestimonies(String testimonies) {
        this.testimonies = testimonies;
        return this;
    }

    public String getExpertReport() {
        return expertReport;
    }

    public TaxManagementVariables setExpertReport(String expertReport) {
        this.expertReport = expertReport;
        return this;
    }

    public String getSpecialRecords() {
        return specialRecords;
    }

    public TaxManagementVariables setSpecialRecords(String specialRecords) {
        this.specialRecords = specialRecords;
        return this;
    }

    public String getParticipantContributions() {
        return participantContributions;
    }

    public TaxManagementVariables setParticipantContributions(String participantContributions) {
        this.participantContributions = participantContributions;
        return this;
    }

    public String getNegotiatedTaxValue() {
        return negotiatedTaxValue;
    }

    public TaxManagementVariables setNegotiatedTaxValue(String negotiatedTaxValue) {
        this.negotiatedTaxValue = negotiatedTaxValue;
        return this;
    }

    public String getNegotiatedTaxJustification() {
        return negotiatedTaxJustification;
    }

    public TaxManagementVariables setNegotiatedTaxJustification(String negotiatedTaxJustification) {
        this.negotiatedTaxJustification = negotiatedTaxJustification;
        return this;
    }
}
