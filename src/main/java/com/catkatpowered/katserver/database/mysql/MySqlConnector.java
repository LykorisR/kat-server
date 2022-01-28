package com.catkatpowered.katserver.database.mysql;

import com.catkatpowered.katserver.KatServer;
import com.catkatpowered.katserver.database.interfaces.KatDatabaseConnector;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 谁在乎 JDBC 版本啊 <br> 一个 try catch 抹平 JDBC 对应 Mysql 版本问题
 *
 * @author hanbings
 * @author suibing112233
 */
public class MySqlConnector{
    Logger logger = KatServer.KatLoggerAPI.getLogger("Database Manager");
    Connection connection;
    public void loadDatabase(String url, String username, String password) {
        // 先处理 8.x 版本的 Mysql JDBC
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException found) {
            // 版本不对 处理版本
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException exception) {
                logger.error("load jdbc error.", exception);
            }
        }
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException exception) {
            logger.error("load mysql database error.", exception);
        }
    }
}
