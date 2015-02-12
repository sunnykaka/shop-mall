package com.kariqu.common.ibatis;

import com.ibatis.common.jdbc.ScriptRunner;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-18
 *        Time: 下午10:33
 */
public class IbatisTest {

    @Before
    public void init() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Properties props = Resources.getResourceAsProperties("database.properties");
        String url = props.getProperty("url");
        String driver = props.getProperty("driver");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        Class.forName(driver).newInstance();
        Connection conn = DriverManager.getConnection(url, username, password);
        try {
            ScriptRunner runner = new ScriptRunner(conn, false, false);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);
            runner.runScript(Resources.getResourceAsReader("pojo.sql"));
        } finally {
            conn.close();
        }
    }

    @Test
    public void test() throws IOException, SQLException {
        Reader reader = Resources.getResourceAsReader("sql-map-config.xml");
        SqlMapClient sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(reader);
        Pojo pojo = (Pojo) sqlMapClient.queryForObject("getPojoById", 1);
        assertEquals("asion", pojo.getName());
    }
}
