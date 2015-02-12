package com.kariqu.buyer.service;

import com.kariqu.searchengine.DumpPreHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 用于处理限时折扣索引更新的dump预处理
 * User: Asion
 * Date: 13-4-9
 * Time: 上午10:45
 */
public class ProductLimitTimeDumpPreHandler implements DumpPreHandler {

    private final Log logger = LogFactory.getLog(ProductLimitTimeDumpPreHandler.class);


    private DataSource dataSource;

    @Override
    public void process() {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("update product set updateTime=now() where id in (select productId from productactivity where endDate <= now())");
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("dump预处理发生错误", e);
        }

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
