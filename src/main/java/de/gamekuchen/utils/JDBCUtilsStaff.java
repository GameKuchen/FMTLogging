package de.gamekuchen.utils;

import de.gamekuchen.FMTLogging;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCUtilsStaff {

    private static final BasicDataSource staffDataSource = JDBCUtilsLogs.getLogDataSource();
    //todo: neeed to setup own datasource dumbo
    public static BasicDataSource getStaffDataSource() {return staffDataSource;}

    public static void dbUserCreate(String username, int UserID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        String sql = "INSERT IGNORE INTO staff SET userID=?, username=?";
        try {
            qr.update(sql, UserID, username);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void dbUserAddKick(int userID) {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        long timestamp = System.currentTimeMillis();
        try {
            String sql = "UPDATE staff SET kicks=kicks+1, lastConfirm=? WHERE userid=?";
            qr.update(sql, timestamp, userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void dbUserAddKicks(int userID, int kicks) {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        long timestamp = System.currentTimeMillis();
        try {
            String sql = "UPDATE staff SET kicks=kicks+?, lastConfirm=? WHERE userid=?";
            qr.update(sql, kicks, timestamp, userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean dbUserExists(int userID) {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        ResultSetHandler<Boolean> resultHandler = ResultSet::next;

        try{
            String sql = "SELECT * FROM staff WHERE userid=?";
            return qr.query(sql, resultHandler, userID);
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int dbUserGetKicks(int UserID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        ResultSetHandler<Integer> resultHandler;
        try{
            String sql = "SELECT kicks FROM staff WHERE userID=?";
            resultHandler = rs -> {
                if(rs.next()){
                    return rs.getInt(1);
                }
                return 404;
            };
            return qr.query(sql, resultHandler, UserID);


        }catch (SQLException e) {
            e.printStackTrace();
            return 404;
        }
    }

    public static void dbUserAddBan(int userID) {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        long timestamp = System.currentTimeMillis();
        try {
            String sql = "UPDATE staff SET bans=bans+1, lastConfirm=? WHERE userid=?";
            qr.update(sql, timestamp, userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void dbUserAddBans(int userID, int bans) {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        long timestamp = System.currentTimeMillis();
        try {
            String sql = "UPDATE staff SET bans=bans+?, lastConfirm=? WHERE userid=?";
            qr.update(sql, bans, timestamp, userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int dbUserGetBans(int UserID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        ResultSetHandler<Integer> resultHandler;
        try{
            String sql = "SELECT bans FROM staff WHERE userID=?";
            resultHandler = rs -> {
                if(rs.next()){
                    return rs.getInt(1);
                }
                return 404;
            };
            return qr.query(sql, resultHandler, UserID);


        }catch (SQLException e) {
            e.printStackTrace();
            return 404;
        }
    }
    public static void dbUserUpdateConfirm(int userID) {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        long timestamp = System.currentTimeMillis();
        try {
            String sql = "UPDATE staff SET lastConfirm=? WHERE userid=?";
            qr.update(sql, timestamp, userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int dbGetLastConfirm(int UserID) throws IOException {
        QueryRunner qr = new QueryRunner(JDBCUtilsStaff.getStaffDataSource());
        ResultSetHandler<Integer> resultHandler;
        try{
            String sql = "SELECT lastConfirm FROM staff WHERE userID=?";
            resultHandler = rs -> {
                if(rs.next()){
                    return rs.getInt(1);
                }
                return 404;
            };
            return qr.query(sql, resultHandler, UserID);


        }catch (SQLException e) {
            e.printStackTrace();
            return 404;
        }
    }
}
