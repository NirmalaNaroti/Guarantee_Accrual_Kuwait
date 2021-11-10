package com.fragma.dao;

import com.fragma.config.ConfigurationHelper;

import com.fragma.dto.MainDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

@Repository
public class ReportDao {

    static Logger LOG = LoggerFactory.getLogger(com.fragma.dao.ReportDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final ConfigurationHelper configurationHelper;

    @Autowired
    public ReportDao(@Qualifier("hiveJdbcTemplate") JdbcTemplate jdbcTemplate, ConfigurationHelper configurationHelper) {
        this.jdbcTemplate = jdbcTemplate;
        this.configurationHelper = configurationHelper;

    }


    public void getStaticDataFromDB(MainDto mainDto){
        LOG.info("***** executing getStaticDataFromDB *****");
        jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                LOG.info("Query = "+ ConfigurationHelper.getStaticDataQuery() );
                PreparedStatement ps = connection.prepareStatement(ConfigurationHelper.getStaticDataQuery());

                return ps;
            }
        },new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {

                String ref_no = resultSet.getString("ref_no");
                String expiryDate = resultSet.getString("expiry_date");
                String issueDate = resultSet.getString("issue_date");
                String contractAmount = resultSet.getString("contract_amount");
                String contractccy = resultSet.getString("contract_ccy");
                String appName = resultSet.getString("app_name");
                String benName= resultSet.getString("ben_name");

                LOG.info("Reference No -> "+ref_no + "; issue_date -> "+issueDate + "; expiry_date -> " + expiryDate);

                mainDto.populateStaticData(ref_no,expiryDate,issueDate,contractAmount,contractccy,appName,benName);

            }
        });

    }

    public void getTransactionDetailsData(MainDto mainDto){
        LOG.info("***** executing getTransactionDetailsData *****");
        jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                LOG.info("Query = "+ ConfigurationHelper.getTransactionDetailsQuery() );
                PreparedStatement ps = connection.prepareStatement(ConfigurationHelper.getTransactionDetailsQuery());

                return ps;
            }
        },new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {




                String ref_no = resultSet.getString("Trn_ref_no");
                Double transAmount = Double.parseDouble(resultSet.getString("lc_amount"));
                String transYear = resultSet.getString("txn_year");
                String transMonth = resultSet.getString("txn_month");
                String tagType=resultSet.getString("tag_type");

                if(tagType==null)
                {
                    tagType="";
                }

                LOG.info("Reference No -> "+ref_no + "; transAmount -> "+transAmount + "; transYear -> " + transYear+"; transMonth ->"+transMonth+";tagType->"+tagType);



                mainDto.populateTransactionDetailsData(ref_no,transMonth,transYear,transAmount,tagType);

            }
        });
    }

    public void getIssuedLGCount(MainDto mainDto){
        LOG.info("***** executing getIssuedLGCount *****");
        jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                LOG.info("Query = "+ ConfigurationHelper.getIssuedLGQuery() );
                PreparedStatement ps = connection.prepareStatement(ConfigurationHelper.getIssuedLGQuery());
                
                return ps;
            }
        },new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {

                String uaeIssuedcount = resultSet.getString("count");

                LOG.info("IssuedCount:"+uaeIssuedcount);

                mainDto.setIssuedCount(Integer.parseInt(uaeIssuedcount));

            }
        });
    }

    public void getActiveLGCount(MainDto mainDto){
        LOG.info("***** executing getActiveLGCount *****");
        jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                LOG.info("Query = "+ ConfigurationHelper.getActiveLGQuery() );
                PreparedStatement ps = connection.prepareStatement(ConfigurationHelper.getActiveLGQuery());

                return ps;
            }
        },new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {

                String uaeActivecount = resultSet.getString("count");

                LOG.info("ActiveCount:"+uaeActivecount);

                mainDto.setActiveCount(Integer.parseInt(uaeActivecount));

            }
        });
    }

}
