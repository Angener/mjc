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
public final class DatabaseXmlExporter {
    private static ApplicationContext context;

    private DatabaseXmlExporter() {
    }

    public static void main(String[] args) throws Exception {
        context = new AnnotationConfigApplicationContext(JdbcConfig.class);
        exportDataSetsToXml();
    }

    private static void exportDataSetsToXml() throws Exception {
        exportTable("tag", "SELECT * FROM tag", "tags.xml");
        exportTable("giftCertificate",
                "SELECT * FROM giftCertificate", "giftCertificate.xml");
        exportTable("tag_giftCertificate",
                "SELECT * FROM tag_giftCertificate", "tag_giftCertificate.xml");
    }

    private static void exportTable(String tableName, String sqlQuery, String filename) throws Exception {
        QueryDataSet queryDataSet = getQueryDataSet();
        queryDataSet.addTable(tableName, sqlQuery);
        FlatXmlDataSet.write(queryDataSet, new FileOutputStream(filename));
    }

    private static QueryDataSet getQueryDataSet() throws Exception {
        DataSource dataSource = (DataSource) context.getBean("dataSource");
        return new QueryDataSet(new DatabaseConnection(dataSource.getConnection()));
    }
}
