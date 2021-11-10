package com.fragma.dto;

import com.fragma.service.ExcelFileCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MainDto {

    static Logger LOG = LoggerFactory.getLogger(com.fragma.dto.MainDto.class);

    private static DecimalFormat df = new DecimalFormat("0.00");

    LocalDate localBD;

   int issuedCount;
   int activeCount;


    public int getIssuedCount() {
        return issuedCount;
    }

    public void setIssuedCount(int uaeIssuedCount) {
        this.issuedCount = uaeIssuedCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int uaeactiveCount) {
        this.activeCount = uaeactiveCount;
    }

    public LocalDate getLocalBD() {
        return localBD;
    }

    public void setLocalBD(LocalDate localBD) {
        this.localBD = localBD;
    }

    Map<String, TransDetailsDto> map = new HashMap<>();

    public Map<String, TransDetailsDto> getMap() {
        return map;
    }

    public void populateStaticData(String ref_no, String expiryDate, String issueDate, String contractAmount, String contractccy, String appName, String benName) {

        TransDetailsDto transDetailsDto = map.get(ref_no);

        if (transDetailsDto == null) {
            transDetailsDto = new TransDetailsDto();
        }
        transDetailsDto.setRef_no(ref_no);
        transDetailsDto.setExpiryDate(expiryDate);
        transDetailsDto.setIssueDate(issueDate);
        transDetailsDto.setContractAmount(contractAmount);
        transDetailsDto.setContractCcy(contractccy);
        transDetailsDto.setAppName(appName);
        transDetailsDto.setBenName(benName);

        map.put(ref_no, transDetailsDto);

    }

    public void populateTransactionDetailsData(String ref_no, String transMonth, String transYear, Double transAmount, String tagType) {

        TransDetailsDto transDetailsDto = map.get(ref_no);
        if (transDetailsDto != null) {
            Map<String, Integer> countMap = transDetailsDto.getCountMap();
            Map<String, Double> sumMap = transDetailsDto.getSumMap();

            System.out.println("Tag Type:"+tagType);

            transDetailsDto.setTagType(tagType);


            int yearFromDB = Integer.parseInt(transYear);
            if(tagType.equalsIgnoreCase("ACCR") || tagType.equalsIgnoreCase("")){
                if (yearFromDB > getLocalBD().getYear()){
                    return;
                }
                else if (yearFromDB < getLocalBD().getYear() ) {//for years
                    if (!countMap.containsKey(transYear)) {
                        countMap.put(transYear, 1);
                    }else {
                        countMap.put(transYear, countMap.get(transYear) + 1);
                    }

                    if (!sumMap.containsKey(transYear)) {
                        sumMap.put(transYear, Double.parseDouble(df.format(transAmount)));
                    }else {
                        sumMap.put(transYear, Double.parseDouble(df.format(sumMap.get(transYear) +transAmount)));
                    }

                } else {//for months
                    if (!countMap.containsKey(transMonth)) {
                        countMap.put(transMonth, 1);
                    }else {
                        countMap.put(transMonth, countMap.get(transMonth) + 1);
                    }

                    if (!sumMap.containsKey(transMonth)) {
                        sumMap.put(transMonth, Double.parseDouble(df.format(transAmount)));
                    }else {
                        sumMap.put(transMonth, Double.parseDouble(df.format(sumMap.get(transMonth) +transAmount)));
                    }
                }
            }

        }
    }

    public void getMapData(){

        for (Map.Entry<String, TransDetailsDto> mainMap : map.entrySet()) {

            LOG.info("Printing Map Data");

            LOG.info("Ref_No-> " + mainMap.getKey()+" Expiry Date -> " + mainMap.getValue().getExpiryDate()+" Issue Date-> " + mainMap.getValue().getIssueDate()+" Contract Amount-> " + mainMap.getValue().getContractAmount()+ " Contract CCy -> " + mainMap.getValue().getContractCcy()+ " App Name -> " + mainMap.getValue().getAppName() +" Ben Name -> " + mainMap.getValue().getBenName());
            Map<String, Integer> countMap = mainMap.getValue().getCountMap();

            for (Map.Entry<String, Integer> countMapEntry : countMap.entrySet()) {

                LOG.info("CountMap->");
                LOG.info("Year/Month ->"+countMapEntry.getKey());
                LOG.info("Count ->"+countMapEntry.getValue());

            }

            Map<String, Double> sumMap = mainMap.getValue().getSumMap();

            for (Map.Entry<String, Double> sumMapEntry : sumMap.entrySet()) {

                LOG.info("SumMap->");
                LOG.info("Year/Month ->"+sumMapEntry.getKey());
                LOG.info("Sum ->"+sumMapEntry.getValue());
            }
        }

    }


}
