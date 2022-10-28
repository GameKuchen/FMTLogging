package de.gamekuchen.utils;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCUtilsUsers {
    private static final BasicDataSource userDataSource = new BasicDataSource();

    static {
        userDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        userDataSource.setUrl("jdbc:mysql://localhost:3306/fmtlogging_users");
        userDataSource.setUsername("fmtlogging");
        userDataSource.setPassword("Yb7o%20riltV8*Ob");
        userDataSource.setMaxTotal(10);
        userDataSource.setMaxIdle(6);
        userDataSource.setMinIdle(3);
        userDataSource.setInitialSize(6);
        userDataSource.setDefaultAutoCommit(true);
    }
    public static BasicDataSource getUserDataSource() {return userDataSource;}

    public static void dbUserCreate(long discordID, int recID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsUsers.getUserDataSource());
        String sql = "INSERT IGNORE INTO users SET discordID=?, recID=?";
        try {
            qr.update(sql, discordID, recID);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean dbUserExists(long discordID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsUsers.getUserDataSource());
        ResultSetHandler<Boolean> resultHandler = ResultSet::next;
        try{
            String sql = "SELECT * FROM users WHERE discordID=?";
            return qr.query(sql, resultHandler, discordID);
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long dbUserGetRecID(long discordID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsUsers.getUserDataSource());
        ResultSetHandler<Long> resultHandler;
        try{
            String sql = "SELECT recID FROM users WHERE discordID=?";
            resultHandler = rs -> {
                if(rs.next()){
                    return rs.getLong(1);
                }
                return 404L;
            };
            return qr.query(sql, resultHandler, discordID);


        }catch (SQLException e) {
            e.printStackTrace();
            return 404;
        }
    }

    public static void dbUserDelete(long discordID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsUsers.getUserDataSource());
        try{
            String sql = "DELETE FROM users WHERE discordID=?";
            qr.execute(sql, discordID);
        }catch (SQLException e) {
            e.printStackTrace();
            return;
            }
        }
}
