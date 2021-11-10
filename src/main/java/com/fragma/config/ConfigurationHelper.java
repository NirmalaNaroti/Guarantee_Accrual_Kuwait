package com.fragma.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "conf")
public class ConfigurationHelper {

    private static String staticDataQuery;

    private static  String transactionDetailsQuery;
    private  static String excelLocation;
    private static String issuedLGQuery;
    private static String activeLGQuery;


    public static String getExcelLocation() {
        return excelLocation;
    }

    public void setExcelLocation(String excelLocation) {
        this.excelLocation = excelLocation;
    }

    public static void setStaticDataQuery(String staticDataQuery) {
        ConfigurationHelper.staticDataQuery = staticDataQuery;
    }

    public static String getStaticDataQuery() {
        return staticDataQuery;
    }

    public static String getTransactionDetailsQuery() {
        return transactionDetailsQuery;
    }

    public static void setTransactionDetailsQuery(String transactionDetailsQuery) {
        ConfigurationHelper.transactionDetailsQuery = transactionDetailsQuery;
    }

    public static String getIssuedLGQuery() {
        return issuedLGQuery;
    }

    public static void setIssuedLGQuery(String issuedLGQuery) {
        ConfigurationHelper.issuedLGQuery = issuedLGQuery;
    }

    public static String getActiveLGQuery() {
        return activeLGQuery;
    }

    public static void setActiveLGQuery(String activeLGQuery) {
        ConfigurationHelper.activeLGQuery = activeLGQuery;
    }
}
