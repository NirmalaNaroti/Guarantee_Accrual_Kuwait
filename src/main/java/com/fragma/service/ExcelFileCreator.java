package com.fragma.service;

import com.fragma.dto.MainDto;
import com.fragma.dto.TransDetailsDto;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelFileCreator {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelFileCreator.class);

    XSSFWorkbook workbook = new XSSFWorkbook();
    Map<String, TransDetailsDto> hitMap = new HashMap<>();
    int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    public void createAllSheets(String excelFileLocation, MainDto mainDto) throws Exception {

        createCountSheet(mainDto, "COUNT");
        createSumSheet(mainDto, "SUM");
        createSumSheet(hitMap, "GUARANTEE ACCURAL REPORT");

        FileOutputStream out = new FileOutputStream(excelFileLocation);
        this.workbook.write(out);
        out.close();
        LOG.info(" Excel file written successfully on disk at :" + excelFileLocation);
        LOG.info("Hit Count excel:"+getCount());
    }

    private void createCountSheet(MainDto mainDto, String sheetName) throws Exception {

        LOG.info("***** executing createCountSheet ****** ");

        List<String> yearList = new LinkedList<>();
        List<String> monthList = new LinkedList<>();

        Font headingFont = workbook.createFont();
        headingFont.setBold(true);

        XSSFColor myColor = new XSSFColor(new java.awt.Color(182, 207, 242));

        XSSFCellStyle headingCellStyle = workbook.createCellStyle();

        headingCellStyle.setFont(headingFont);
        headingCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headingCellStyle.setFillForegroundColor(myColor);
        headingCellStyle.setBorderBottom(BorderStyle.THIN);
        headingCellStyle.setBorderLeft(BorderStyle.THIN);
        headingCellStyle.setBorderRight(BorderStyle.THIN);
        headingCellStyle.setBorderTop(BorderStyle.THIN);
        headingCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headingCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headingCellStyle.setWrapText(true);

        CellStyle bordersOnly = workbook.createCellStyle();
        bordersOnly.setBorderBottom(BorderStyle.THIN);
        bordersOnly.setBorderLeft(BorderStyle.THIN);
        bordersOnly.setBorderRight(BorderStyle.THIN);
        bordersOnly.setBorderTop(BorderStyle.THIN);

        Sheet sheet = workbook.createSheet(sheetName);

        Row headingRow = sheet.createRow(0);
        headingRow.setHeight((short) 900);

        int headingColmIndx = 0;

        createCellAddData(headingRow, headingColmIndx++, "Transaction Reference Number", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Issue Date", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Expiry Date", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Contract Amount", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Contract ccy", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "app name", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "ben name", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Tag Type", headingCellStyle);

        LocalDate bd = mainDto.getLocalBD();

        int startYear = 2013;
        while (startYear < bd.getYear() ){//for years
            createCellAddData(headingRow, headingColmIndx++, String.valueOf(startYear), headingCellStyle);
            yearList.add(String.valueOf(startYear));
            startYear++;
        }



        for (int month = 1; month <= bd.getMonthValue(); month++) {
            monthList.add(String.valueOf(month));

            String monthName = DateTime.now().withMonthOfYear(month).toString("MMM");
            createCellAddData(headingRow, headingColmIndx++, monthName + "-" + startYear, headingCellStyle);

        }
        createCellAddData(headingRow, headingColmIndx++, "HIT Status", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Pre/Post", headingCellStyle);
        //pre/post current year

        int rowNum = 1;

        String refNo;
        TransDetailsDto transDetailsDto=new TransDetailsDto();


        for (Map.Entry<String, TransDetailsDto> tdEntry : mainDto.getMap().entrySet()) {


            refNo=tdEntry.getKey();
            transDetailsDto=tdEntry.getValue();

            Row row = sheet.createRow(rowNum++);
            int cell = 0;

            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getRef_no()), bordersOnly);
            createDateCellAddData(row, cell++,getFullDate( tdEntry.getValue().getIssueDate()), bordersOnly);
            createDateCellAddData(row, cell++,getFullDate( tdEntry.getValue().getExpiryDate()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getContractAmount()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getContractCcy()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getAppName()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getBenName()), bordersOnly);
            if(tdEntry.getValue().getTagType() == null)
            {
                createCellAddData(row, cell++, "", bordersOnly);
            }
            else{
                createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getTagType()), bordersOnly);
            }



            String HitStatus="NO";
            String prePost="";




            Map<String, Integer> countMap = tdEntry.getValue().getCountMap();

            for (String yr : yearList) {

                if (!countMap.containsKey(yr)) {
                    createCellAddData(row, cell++,"", bordersOnly);
                    if((Integer.parseInt(yr)>=getYearFromDate(tdEntry.getValue().getIssueDate())) && (Integer.parseInt(yr)<=getYearFromDate(tdEntry.getValue().getExpiryDate())) )
                    {
                        if(!((tdEntry.getValue().getRef_no().contains("LGLB")) ||  (tdEntry.getValue().getBenName().contains("MINISTRY OF LABOUR")))) {
                            HitStatus = "YES";
                            prePost = "Pre";
                        }
                    }
                }
                else {
                    createCellAddData(row, cell++, String.valueOf(countMap.get(yr)), bordersOnly);
                }
            }

            for (String mnth : monthList) {

                if (!countMap.containsKey(mnth)) {

                    createCellAddData(row, cell++,"", bordersOnly);

                    if((getYearFromDate(tdEntry.getValue().getExpiryDate())== bd.getYear())&& (Integer.parseInt(mnth)<= getMonthFromDate(tdEntry.getValue().getExpiryDate())) &&(getYearFromDate(tdEntry.getValue().getIssueDate())==bd.getYear() && Integer.parseInt(mnth)>=(getMonthFromDate(tdEntry.getValue().getIssueDate())))){
                        if(!((tdEntry.getValue().getRef_no().contains("LGLB")) ||  (tdEntry.getValue().getBenName().contains("MINISTRY OF LABOUR")))) {
                            HitStatus = "YES";
                            LOG.info("oooooooooooooooo");

                            LOG.info("Pre:    "+prePost);

                            if(!(prePost.equalsIgnoreCase("Pre"))) {
                                prePost = "Post";
                            }
                            else {
                                prePost = "Pre/Post";
                            }
                        }
                    }
                }
                else {
                    createCellAddData(row, cell++, String.valueOf(countMap.get(mnth)), bordersOnly);

                }
            }



            createCellAddData(row, cell++, HitStatus, bordersOnly);
            createCellAddData(row, cell++, prePost, bordersOnly);

            if(HitStatus.equalsIgnoreCase("YES")){

                LOG.info("Hit Status Yes Map");

                hitMap.put(refNo,transDetailsDto);


            }
            if((HitStatus.equalsIgnoreCase("YES")) && !(countMap.containsKey(String.valueOf(bd.getMonthValue()))) &&(((getYearFromDate(tdEntry.getValue().getExpiryDate())> bd.getYear()) )|| ((getYearFromDate(tdEntry.getValue().getExpiryDate())== bd.getYear()) &&(getMonthFromDate(tdEntry.getValue().getExpiryDate())>=bd.getMonthValue()))) ) {

                setCount(getCount()+1);
            }

        }

        for (int i = 0; i <= sheet.getRow(0).getLastCellNum(); i++) {

            sheet.autoSizeColumn(i);
            int columnWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, columnWidth + 1000);

        }
    }


    private void createSumSheet(MainDto mainDto, String sheetName) throws ParseException {

        LOG.info("***** executing createSumSheet ****** ");

        List<String> yearList = new LinkedList<>();
        List<String> monthList = new LinkedList<>();

        Font headingFont = workbook.createFont();
        headingFont.setBold(true);

        XSSFColor myColor = new XSSFColor(new java.awt.Color(182, 207, 242));

        XSSFCellStyle headingCellStyle = workbook.createCellStyle();

        headingCellStyle.setFont(headingFont);
        headingCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headingCellStyle.setFillForegroundColor(myColor);
        headingCellStyle.setBorderBottom(BorderStyle.THIN);
        headingCellStyle.setBorderLeft(BorderStyle.THIN);
        headingCellStyle.setBorderRight(BorderStyle.THIN);
        headingCellStyle.setBorderTop(BorderStyle.THIN);
        headingCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headingCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headingCellStyle.setWrapText(true);

        CellStyle bordersOnly = workbook.createCellStyle();
        bordersOnly.setBorderBottom(BorderStyle.THIN);
        bordersOnly.setBorderLeft(BorderStyle.THIN);
        bordersOnly.setBorderRight(BorderStyle.THIN);
        bordersOnly.setBorderTop(BorderStyle.THIN);


        Sheet sheet = workbook.createSheet(sheetName);

        Row headingRow = sheet.createRow(0);
        headingRow.setHeight((short) 900);

        int headingColmIndx = 0;


        createCellAddData(headingRow, headingColmIndx++, "Transaction Reference Number", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Issue Date", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Expiry Date", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Contract Amount", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Contract ccy", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "app name", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "ben name", headingCellStyle);


        LocalDate bd = mainDto.getLocalBD();
        int startYear = 2013;
        while (startYear < bd.getYear()) {//for years
            createCellAddData(headingRow, headingColmIndx++, String.valueOf(startYear), headingCellStyle);
            yearList.add(String.valueOf(startYear));
            startYear++;
        }

        for (int month = 1; month <= bd.getMonthValue(); month++) {
            monthList.add(String.valueOf(month));

            String monthName = DateTime.now().withMonthOfYear(month).toString("MMM");
            createCellAddData(headingRow, headingColmIndx++, monthName + "-" + startYear, headingCellStyle);

        }
        createCellAddData(headingRow, headingColmIndx++, "HIT Status", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Pre/Post", headingCellStyle);

        int rowNum = 1;

        for (Map.Entry<String, TransDetailsDto> tdEntry : mainDto.getMap().entrySet()) {

            Row row = sheet.createRow(rowNum++);
            int cell = 0;

            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getRef_no()), bordersOnly);
            createDateCellAddData(row, cell++,getFullDate( tdEntry.getValue().getIssueDate()), bordersOnly);
            createDateCellAddData(row, cell++,getFullDate( tdEntry.getValue().getExpiryDate()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getContractAmount()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getContractCcy()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getAppName()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getBenName()), bordersOnly);

            Map<String, Double> sumMap = tdEntry.getValue().getSumMap();
            String HitStatus="NO";
            String prePost="";

            for (String yr : yearList) {
                if (!sumMap.containsKey(yr)) {
                    createCellAddData(row, cell++,"", bordersOnly);
                    if((Integer.parseInt(yr)>=getYearFromDate(tdEntry.getValue().getIssueDate())) && (Integer.parseInt(yr)<=getYearFromDate(tdEntry.getValue().getExpiryDate())) )
                    {
                        if(!((tdEntry.getValue().getRef_no().contains("LGLB")) ||  (tdEntry.getValue().getBenName().contains("MINISTRY OF LABOUR")))) {

                            HitStatus = "YES";
                            prePost = "Pre";
                        }
                    }
                }
                else{
                createCellAddData(row, cell++, String.valueOf(sumMap.get(yr)), bordersOnly);
                }
            }

            for (String mnth : monthList) {
                if (!sumMap.containsKey(mnth)) {
                    createCellAddData(row, cell++,"", bordersOnly);
                    LOG.info("Pre->"+prePost);
                    if((getYearFromDate(tdEntry.getValue().getExpiryDate())== bd.getYear())&& (Integer.parseInt(mnth)<= getMonthFromDate(tdEntry.getValue().getExpiryDate())) &&(getYearFromDate(tdEntry.getValue().getIssueDate())==bd.getYear() && Integer.parseInt(mnth)>=(getMonthFromDate(tdEntry.getValue().getIssueDate())))){
                        if(!((tdEntry.getValue().getRef_no().contains("LGLB")) ||  (tdEntry.getValue().getBenName().contains("MINISTRY OF LABOUR")))) {
                            LOG.info("oooooooooooooooo");
                            HitStatus = "YES";

                           if((prePost.equals("Pre"))||prePost.equalsIgnoreCase("Pre/Post")) {
                               prePost = "Pre/Post";
                           }
                           else {
                               prePost = "Post";
                           }
                       }
                    }

                    if((getYearFromDate(tdEntry.getValue().getExpiryDate())> bd.getYear())&& (Integer.parseInt(mnth)<= 12 &&(getYearFromDate(tdEntry.getValue().getIssueDate())==bd.getYear() && Integer.parseInt(mnth)>=(getMonthFromDate(tdEntry.getValue().getIssueDate()))))){
                        if(!((tdEntry.getValue().getRef_no().contains("LGLB")) ||  (tdEntry.getValue().getBenName().contains("MINISTRY OF LABOUR")))) {
                            LOG.info("oooooooooooooooo");
                            HitStatus = "YES";
                            if((prePost.equals("Pre"))||prePost.equalsIgnoreCase("Pre/Post")) {
                                prePost = "Pre/Post";
                            }
                            else {
                                prePost = "Post";
                            }
                        }
                    }

                    if((getYearFromDate(tdEntry.getValue().getExpiryDate())== bd.getYear())&& (Integer.parseInt(mnth)<= getMonthFromDate(tdEntry.getValue().getExpiryDate())) &&(getYearFromDate(tdEntry.getValue().getIssueDate())<bd.getYear() && Integer.parseInt(mnth)>=1)){
                        if(!((tdEntry.getValue().getRef_no().contains("LGLB")) ||  (tdEntry.getValue().getBenName().contains("MINISTRY OF LABOUR")))) {
                            LOG.info("oooooooooooooooo");
                            HitStatus = "YES";
                            if((prePost.equals("Pre"))||prePost.equalsIgnoreCase("Pre/Post")) {
                                prePost = "Pre/Post";
                            }
                            else {
                                prePost = "Post";
                            }
                        }
                    }

                    if((getYearFromDate(tdEntry.getValue().getExpiryDate())> bd.getYear())&& (Integer.parseInt(mnth)<= 12 &&(getYearFromDate(tdEntry.getValue().getIssueDate())<bd.getYear() && Integer.parseInt(mnth)>=1))){
                        if(!((tdEntry.getValue().getRef_no().contains("LGLB")) ||  (tdEntry.getValue().getBenName().contains("MINISTRY OF LABOUR")))) {
                            LOG.info("oooooooooooooooo");
                            HitStatus = "YES";
                            if((prePost.equals("Pre"))||prePost.equalsIgnoreCase("Pre/Post")) {
                                prePost = "Pre/Post";
                            }
                            else {
                                prePost = "Post";
                            }
                        }
                    }


                }else{
                createCellAddData(row, cell++, String.valueOf(sumMap.get(mnth)), bordersOnly);
                }
            }
            createCellAddData(row, cell++, HitStatus, bordersOnly);
            createCellAddData(row, cell++, prePost, bordersOnly);

        }

        for (int i = 0; i <= sheet.getRow(0).getLastCellNum(); i++) {

            sheet.autoSizeColumn(i);
            int columnWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, columnWidth + 1000);
        }
    }


    private void createSumSheet(Map<String,TransDetailsDto> map, String sheetName) throws ParseException {

        LOG.info("***** executing createSumSheet ****** ");

        Font headingFont = workbook.createFont();
        headingFont.setBold(true);

        XSSFColor myColor = new XSSFColor(new java.awt.Color(182, 207, 242));

        XSSFCellStyle headingCellStyle = workbook.createCellStyle();

        headingCellStyle.setFont(headingFont);
        headingCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headingCellStyle.setFillForegroundColor(myColor);
        headingCellStyle.setBorderBottom(BorderStyle.THIN);
        headingCellStyle.setBorderLeft(BorderStyle.THIN);
        headingCellStyle.setBorderRight(BorderStyle.THIN);
        headingCellStyle.setBorderTop(BorderStyle.THIN);
        headingCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headingCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headingCellStyle.setWrapText(true);

        CellStyle bordersOnly = workbook.createCellStyle();
        bordersOnly.setBorderBottom(BorderStyle.THIN);
        bordersOnly.setBorderLeft(BorderStyle.THIN);
        bordersOnly.setBorderRight(BorderStyle.THIN);
        bordersOnly.setBorderTop(BorderStyle.THIN);


        Sheet sheet = workbook.createSheet(sheetName);

        Row headingRow = sheet.createRow(0);
        headingRow.setHeight((short) 900);

        int headingColmIndx = 0;


        createCellAddData(headingRow, headingColmIndx++, "Transaction Reference Number", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Issue Date", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Expiry Date", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Contract Amount", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "Contract ccy", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "app name", headingCellStyle);
        createCellAddData(headingRow, headingColmIndx++, "ben name", headingCellStyle);



        createCellAddData(headingRow, headingColmIndx++, "HIT Status", headingCellStyle);


        int rowNum = 1;

        for (Map.Entry<String, TransDetailsDto> tdEntry : hitMap.entrySet()) {

            Row row = sheet.createRow(rowNum++);
            int cell = 0;

            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getRef_no()), bordersOnly);
            createDateCellAddData(row, cell++,getFullDate( tdEntry.getValue().getIssueDate()), bordersOnly);
            createDateCellAddData(row, cell++, getFullDate(tdEntry.getValue().getExpiryDate()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getContractAmount()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getContractCcy()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getAppName()), bordersOnly);
            createCellAddData(row, cell++, String.valueOf(tdEntry.getValue().getBenName()), bordersOnly);
            createCellAddData(row, cell++, "YES", bordersOnly);


        }

        for (int i = 0; i <= sheet.getRow(0).getLastCellNum(); i++) {

            sheet.autoSizeColumn(i);
            int columnWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, columnWidth + 1000);
        }
    }





    public void createCellAddData(Row row, int cellNo, String cellValue, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNo);
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);
    }

    public void createDateCellAddData(Row row, int cellNo, Date cellValue, CellStyle cellStyle) {

        Cell cell = row.createCell(cellNo);
        cell.setCellValue(cellValue);
        CreationHelper creationHelper = workbook.getCreationHelper();


        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(
                "dd-mm-yyyy"));
        cell.setCellStyle(cellStyle);

    }


    private int getYearFromDate(String strIssueDate) {
        LocalDate issueDate = LocalDate.parse(strIssueDate, formatter);
        return issueDate.getYear();
    }

    private int getMonthFromDate(String strIssueDate) {
        LocalDate issueDate = LocalDate.parse(strIssueDate, formatter);
        return issueDate.getMonthValue();
    }

    private Date getFullDate(String strdate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date= dateFormat.parse(strdate);
        System.out.println("Date:"+date);
        return date;
    }
}