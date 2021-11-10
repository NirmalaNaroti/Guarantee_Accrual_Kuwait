package com.fragma.client;

import com.fragma.config.ConfigurationHelper;
import com.fragma.config.SMTPMailThreadConfig;
import com.fragma.dao.ReportDao;

import com.fragma.dto.MainDto;
import com.fragma.service.ExcelFileCreator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
public class Runner implements ApplicationRunner {
    static Logger LOG = LoggerFactory.getLogger(com.fragma.client.Runner.class);

    private ReportDao reportDao;
    private ExcelFileCreator excelFileCreator;
    private Session session;
    private TemplateEngine templateEngine;
    private SMTPMailThreadConfig smtpMailThreadConfig;
    private ConfigurationHelper configurationHelper;


    @Autowired
    public Runner(TemplateEngine templateEngine,ReportDao reportDao,ExcelFileCreator excelFileCreator ,Session session, SMTPMailThreadConfig smtpMailThreadConfig, ConfigurationHelper configurationHelper) {

        this.reportDao = reportDao;
        this.excelFileCreator = excelFileCreator;
        this.session = session;
        this.templateEngine=templateEngine;
        this.smtpMailThreadConfig = smtpMailThreadConfig;
        this.configurationHelper = configurationHelper;

    }

    @Override
    public void run(ApplicationArguments args) {

        try {
            Context context = new Context();
            StringWriter writer = new StringWriter();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LOG.info("run started");
            
            List<String> businessDates = args.getOptionValues("businessDate");
            LocalDate businessDate;

            String bdString;
            if (businessDates != null && businessDates.size() > 0) {
                bdString = businessDates.get(0);
                businessDate = LocalDate.parse(bdString, formatter);
               // businessDate=businessDate.minusMonths(0).withDayOfMonth(businessDate.minusMonths(0).lengthOfMonth());
            } else {
                LocalDate toDay= LocalDate.now();
                businessDate=toDay.minusMonths(1).withDayOfMonth(toDay.minusMonths(1).lengthOfMonth());
            }

            MainDto mainDto=new MainDto();
            LOG.info("BusinessDate="+businessDate);
            mainDto.setLocalBD(businessDate);

            reportDao.getStaticDataFromDB(mainDto);
            reportDao.getTransactionDetailsData(mainDto);
            reportDao.getIssuedLGCount(mainDto);
            reportDao.getActiveLGCount(mainDto);

           // mainDto.getMapData();

            LOG.info("Excel File Location:"+ConfigurationHelper.getExcelLocation());

            excelFileCreator.createAllSheets(ConfigurationHelper.getExcelLocation(),mainDto);


            context.setVariable("mainDto", mainDto);
            context.setVariable("excelFileCreator",excelFileCreator);

            templateEngine.process("ACCURALReport", context, writer);

            LOG.info("Hit Count:"+excelFileCreator.getCount());

          sendMailWithAttachment(writer.toString());
        }
           catch (Exception e) {
                LOG.error("Exception : " + ExceptionUtils.getStackTrace(e));
            }
        }

    public void sendMailWithAttachment(String body) {

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(smtpMailThreadConfig.getFromAddress(), smtpMailThreadConfig.getFromName()));
            msg.setReplyTo(InternetAddress.parse(smtpMailThreadConfig.getFromAddress(), false));
            msg.setContent(body, "text/html");


            msg.setSubject(smtpMailThreadConfig.getSubject(), "UTF-8");

            MimeBodyPart messageBodyPart1 = new MimeBodyPart();

            messageBodyPart1.addHeader("Content-type", "text/HTML; charset=UTF-8");
            messageBodyPart1.addHeader("format", "flowed");
            messageBodyPart1.addHeader("Content-Transfer-Encoding", "8bit");
           // messageBodyPart1.setText(st.toString());
            messageBodyPart1.setContent(body, "text/html");


            Multipart multipart = new MimeMultipart();


            multipart.addBodyPart(messageBodyPart1);


            LOG.info("Excel Location ->"+configurationHelper.getExcelLocation());

            addAttachment(multipart, configurationHelper.getExcelLocation());

            msg.setContent(multipart);
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(smtpMailThreadConfig.getToAddress(), false));
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(smtpMailThreadConfig.getCcAddress(), false));
            Transport.send(msg);
            LOG.info("Mail sent Succesfully");

        } catch (Exception e) {
            LOG.error("ERROR RAISED WHILE SENDING MAIL WITH AN ATTACHMENT..=" + ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    public void addAttachment(Multipart multipart, String fileName) throws MessagingException {

        DataSource source = new FileDataSource(fileName);
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(new File(fileName).getName());
        multipart.addBodyPart(messageBodyPart);

    }
    }
