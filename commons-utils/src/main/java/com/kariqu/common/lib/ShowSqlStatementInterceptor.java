package com.kariqu.common.lib;


import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.StatementInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.Properties;

/**
 * mysql sql拦截器, 把sql语句记录下来输出
 * Created by Canal.wen on 2014/7/15 13:54.
 */
public class ShowSqlStatementInterceptor implements StatementInterceptor {
    private static final Log LOG = LogFactory.getLog("sqlLog");
    SqlFormat sf = new SqlFormat();
    static boolean formatSql = true;

    public void init(Connection connection, Properties properties) throws SQLException {
    }

    public ResultSetInternalMethods preProcess(String sql, Statement statement, Connection connection) throws SQLException {


        String sqlText = "";
        if (sql == null) {
            if ((statement instanceof StatementImpl)) {
                StatementImpl st = (StatementImpl) statement;
                sqlText = st.toString();
                if (sqlText.indexOf(':') > 0) {
                    sqlText = sqlText.substring(sqlText.indexOf(':') + 1).trim();
                }
            }
        } else if (!sql.startsWith("SHOW FULL TABLES FROM")) {
            sqlText = sql;
        }

        String sqlNoFormat = sqlText.trim();

        if ((sqlText.length() < 1024) && formatSql &&
                !sqlText.contains("CREATE TABLE") && !sqlText.contains("commit") &&
                !sqlText.contains("DROP TABLE")) {

            sqlText = this.sf.format(sqlText.trim());
        }

        if (! "select 1".equalsIgnoreCase(sqlNoFormat)) {
            System.out.printf("发往mysql执行==%s\n", sqlNoFormat);
        }
        LOG.info(sqlText);
        return null;
    }

    public ResultSetInternalMethods postProcess(String s, Statement statement, ResultSetInternalMethods resultSetInternalMethods, Connection connection) throws SQLException {
        return null;
    }

    public boolean executeTopLevelOnly() {
        return false;
    }

    public void destroy() {
    }

}
