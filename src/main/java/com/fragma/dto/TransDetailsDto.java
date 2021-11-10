package com.fragma.dto;

import java.util.HashMap;
import java.util.Map;

public class TransDetailsDto {

    String ref_no;
    String expiryDate;
    String issueDate;
    String contractAmount;
    String contractCcy;
    String appName;
    String benName;
    String tagType;
    Map<String,Integer> countMap = new HashMap<>();
    Map<String,Double> sumMap = new HashMap<>();

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(String contractAmount) {
        this.contractAmount = contractAmount;
    }

    public String getContractCcy() {
        return contractCcy;
    }

    public void setContractCcy(String contractCcy) {
        this.contractCcy = contractCcy;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBenName() {
        return benName;
    }

    public void setBenName(String benName) {
        this.benName = benName;
    }

    public Map<String, Integer> getCountMap() {
        return countMap;
    }

    public void setCountMap(Map<String, Integer> countMap) {
        this.countMap = countMap;
    }

    public Map<String, Double> getSumMap() {
        return sumMap;
    }

    public void setSumMap(Map<String, Double> sumMap) {
        this.sumMap = sumMap;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }
}
