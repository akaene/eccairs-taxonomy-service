package com.akaene.eccairs;

import java.util.List;

public class EccairsValue {

    private int id;

    private String description;

    private String detailedDescription;

    private String explanation;

    private String domains;

    private String level;

    private List<EccairsValue> values;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getDomains() {
        return domains;
    }

    public void setDomains(String domains) {
        this.domains = domains;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<EccairsValue> getValues() {
        return values;
    }

    public void setValues(List<EccairsValue> values) {
        this.values = values;
    }
}
