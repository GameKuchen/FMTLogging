package de.gamekuchen.utils;

import de.gamekuchen.FMTLogging;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class RemoveLoop {

    public void loop() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        Date timeToRun = cal.getTime();

        if(System.currentTimeMillis()>timeToRun.getTime()) {
            cal.add(Calendar.DATE, 1);
        }

        timeToRun = cal.getTime();
        Timer myTimer = new Timer();

        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                QueryRunner qr = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
                QueryRunner qr2 = new QueryRunner(JDBCUtilsLogs.getLogDataSource());
                Map<Integer, Long> unbanCheckMap = new HashMap<>();
                ResultSetHandler<Integer> resultSetHandler = rs -> {
                    while (rs.next()) {
                        unbanCheckMap.put(rs.getInt(1), rs.getLong(7));
                    }
                    return 0;
                };

                try {
                    String sql = "SELECT * FROM logging";
                    qr.query(sql, resultSetHandler);
                    for (Map.Entry<Integer, Long> entry : unbanCheckMap.entrySet()) {
                        int id = entry.getKey();
                        long unixTime = entry.getValue();
                        long currentTime = System.currentTimeMillis();
                        Calendar unixTimeInSixMonths = unixToCalendar(unixTime);
                        unixTimeInSixMonths.add(Calendar.MONTH, 3);

                        if(currentTime >= unixTimeInSixMonths.getTimeInMillis()) {
                            JDBCUtilsLogs.dbUserRemoveByID(id);
                            FMTLogging.logger.info(String.format("Removed accountID from Database. AccountID: %s", id));
                        }
                    }
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, timeToRun, 24*60*60*1000);

    }

    public static Calendar unixToCalendar(long unixTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime);
        return calendar;
    }
}
