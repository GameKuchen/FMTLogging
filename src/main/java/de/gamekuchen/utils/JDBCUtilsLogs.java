package de.gamekuchen.utils;

import de.gamekuchen.FMTLogging;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class JDBCUtilsLogs {

    private static final BasicDataSource logDataSource = new BasicDataSource();

    static {
        logDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        logDataSource.setUrl("jdbc:mysql://localhost:3306/fmtlogging_logs");
        logDataSource.setUsername("fmtlogging");
        logDataSource.setPassword("Yb7o%20riltV8*Ob");
        logDataSource.setMaxTotal(10);
        logDataSource.setMaxIdle(6);
        logDataSource.setMinIdle(3);
        logDataSource.setInitialSize(6);
        logDataSource.setDefaultAutoCommit(true);
    }

    public static BasicDataSource getLogDataSource() {return logDataSource;}

    public static boolean dbUserExists(String username) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        int userID = RecAPI.recAccountID(username);
        ResultSetHandler<Boolean> resultHandler = ResultSet::next;

        try{
            String sql = "SELECT * FROM logging WHERE userid=?";
            boolean exists = qr.query(sql, resultHandler, userID);
            return exists;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void dbUserCreate(String username, String strikeReason) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        String sql = "INSERT IGNORE INTO logging SET userid=?, username=?, strikeReason1=?, strikes=1, last_strike=?";
        int userID = RecAPI.recAccountID(username);
        long timestamp = System.currentTimeMillis();
        try {
            qr.update(sql, userID, username, strikeReason, timestamp);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void dbUserRemove(String username) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        int userID = RecAPI.recAccountID(username);
        String sql = "DELETE FROM logging WHERE userid=?";
        try {
            qr.update(sql, userID);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void dbUserRemoveByID(int accountID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        String sql = "DELETE FROM logging WHERE userid=?";
        try {
            qr.update(sql, accountID);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void dbUserSetBanned(String username, int setBanned) throws IOException{
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        int userID = RecAPI.recAccountID(username);
        try {
            String sql = "UPDATE logging SET isBanned=? WHERE userid=?";
            qr.update(sql, setBanned, userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void dbUserAddStrike(String username, String strikeReason) throws ExecutionException, InterruptedException, IOException {
        int strikesFuture = dbUserGetStrikes(username);
        int userID = RecAPI.recAccountID(username);
        FMTLogging.logger.debug(Integer.toString(strikesFuture));
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        long timestamp = System.currentTimeMillis();
        if(strikesFuture == 1){
            try {
                String sql = "UPDATE logging SET strikes = strikes + 1, strikeReason2=?, last_strike=? WHERE userid=?";
                qr.update(sql, strikeReason, timestamp, userID);
                FMTLogging.logger.debug(String.format("UserID: %s", userID));
                FMTLogging.logger.debug("Executed Successfully and updated database");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else if (strikesFuture == 2){
            try {
                String sql = "UPDATE logging SET strikes = strikes + 1, strikeReason3=?, last_strike=? WHERE userid=?";
                FMTLogging.logger.debug("Executed Successfully and updated database");
                qr.update(sql, strikeReason, timestamp, userID);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }else  {
            FMTLogging.logger.error("dbUserAddStrike Failure. Trying to recover...");
            //TODO: Add recovering instead of just exiting.
            System.exit(203);
        }

    }

    public static Integer dbUserGetStrikes(String username) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        int userID = RecAPI.recAccountID(username);
        ResultSetHandler<Integer> resultHandler;
        try{
            String sql = "SELECT strikes FROM logging WHERE userid=?";
            resultHandler = rs -> {
                if(rs.next()){
                    return rs.getInt(1);
                }
                return null;
            };
            return qr.query(sql, resultHandler, userID);


        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dbUserGetStrikeReason(String username, Integer reason) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        ResultSetHandler<String> resultHandler;
        int userID = RecAPI.recAccountID(username);
        try{
            String sql = String.format("SELECT strikeReason%s FROM logging WHERE userID=?",reason);
            resultHandler = rs -> {
                if(rs.next()){
                    return rs.getString(1);
                }
                FMTLogging.logger.error("dbUserGetStrikeReason() did some weird duckery. Contact GameKuchen. OwO");
                return "What The fuck";
            };
            return qr.query(sql, resultHandler, userID);


        }catch (SQLException e) {
            e.printStackTrace();
            return "Cringe";
        }

    }

    public static Integer dbUserGetLastStrikeTime(String username) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        int userID = RecAPI.recAccountID(username);
        ResultSetHandler<Integer> resultHandler;
        try{
            String sql = "SELECT last_strike FROM logging WHERE userid=?";
            resultHandler = rs -> {
                if(rs.next()){
                    return rs.getInt(1);
                }
                return null;
            };
            return qr.query(sql, resultHandler, userID);


        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean dbUserGetIsBanned(String username) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
        int userID = RecAPI.recAccountID(username);
        ResultSetHandler<Boolean> resultHandler;
        try{
            String sql = "SELECT isBanned FROM logging WHERE userid=?";
            resultHandler = rs -> {
                if(rs.next()){
                    return rs.getInt(1) == 1;
                }
                return false;
            };
            return qr.query(sql, resultHandler, userID);


        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
