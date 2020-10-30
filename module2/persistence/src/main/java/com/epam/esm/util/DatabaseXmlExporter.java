package com.epam.esm.util;

import com.epam.esm.config.JdbcConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.io.FileOutputStream;

/**
 * Creates simple dataset to test something
 */
final class DatabaseXmlExporter {
    private static ApplicationContext context;
    private static final String TAG_TABLE_SQL_QUERY = "SELECT * FROM tag";
    private static final String GIFT_CERTIFICATE_TABLE_SQL_QUERY = "SELECT * FROM giftCertificate";
    private static final String TAG_GIFT_CERTIFICATE_TABLE_SQL_QUERY = "SELECT * FROM tag_giftCertificate";

    private DatabaseXmlExporter() {
    }

    public static void main(String[] args) throws Exception {
        context = new AnnotationConfigApplicationContext(JdbcConfig.class);
        exportDataSetsToXml();
    }

    private static void exportDataSetsToXml() throws Exception {
        QueryDataSet queryDataSet = getQueryDataSet();
        setDataSets(queryDataSet);
        export(queryDataSet);
    }

    private static void setDataSets(QueryDataSet queryDataSet) throws Exception {
        queryDataSet.addTable("tag", TAG_TABLE_SQL_QUERY);
        queryDataSet.addTable("giftCertificate", GIFT_CERTIFICATE_TABLE_SQL_QUERY);
        queryDataSet.addTable("tag_giftCertificate", TAG_GIFT_CERTIFICATE_TABLE_SQL_QUERY);
    }

    private static void export(QueryDataSet queryDataSet) throws Exception {
        FlatXmlDataSet.write(queryDataSet, new FileOutputStream("giftCertificateDataSet.xml"));
    }

    private static QueryDataSet getQueryDataSet() throws Exception {
        DataSource dataSource = (DataSource) context.getBean("getDataSource");
        return new QueryDataSet(new DatabaseConnection(dataSource.getConnection()));
    }
}
