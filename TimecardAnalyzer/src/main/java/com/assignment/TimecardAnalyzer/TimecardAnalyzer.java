package com.assignment.TimecardAnalyzer;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimecardAnalyzer {

    public static void main(String[] args) throws IOException, ParseException, CsvException {
    	
        String filePath = "D:/blueray/timecard.csv";
        analyzeTimecard(filePath);
    }

    public static void analyzeTimecard(String filePath) throws IOException, ParseException, CsvException {
    	
        CSVReader reader = new CSVReader(new FileReader(filePath));
        List<String[]> records = reader.readAll();

        Map<String, Integer> consecutiveDaysCount = new HashMap<>();
        Map<String, Date> lastShiftEndTime = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        for (String[] record : records.subList(1, records.size())) {
            String employeeName = record[0];
            String position = record[1];
            String timeInStr = record[2];
            String timeOutStr = record[3];

            if (!timeInStr.isEmpty() && !timeOutStr.isEmpty()) {
                Date timeIn = dateFormat.parse(timeInStr);
                Date timeOut = dateFormat.parse(timeOutStr);

                // Check consecutive days
                int consecutiveDays = consecutiveDaysCount.getOrDefault(employeeName, 0);
                Date lastEndTime = lastShiftEndTime.get(employeeName);

                if (lastEndTime != null && checkNextDay(lastEndTime, timeIn)) {
                    consecutiveDays++;
                } else {
                    consecutiveDays = 1;
                }

                consecutiveDaysCount.put(employeeName, consecutiveDays);

                if (lastEndTime != null && inHoursBetween(lastEndTime, timeIn) < 10 && inHoursBetween(lastEndTime, timeIn) > 1) {
                    System.out.println("Employee: " + employeeName + ", Position: " + position + " [ who have less than 10 hours of time between shifts but greater than 1 hour ]");
                }

                // Check more than 14 hours in a single shift
                int shiftDuration = inHoursBetween(timeIn, timeOut);
                if (shiftDuration > 14) {
                    System.out.println("Employee: " + employeeName + ", Position: " + position + " [ Who has worked for more than 14 hours in a single shift ]");
                }

                // Update last shift end time
                lastShiftEndTime.put(employeeName, timeOut);
            } else {
                System.out.println("Empty dates " + employeeName);
            }
        }

        // Checking for consecutive days
        for (Map.Entry<String, Integer> entry : consecutiveDaysCount.entrySet()) {
            if (entry.getValue() >= 7) {
                System.out.println("Employee: " + entry.getKey() + " [ who has worked for 7 consecutive days ].");
            }
        }
    }

    private static boolean checkNextDay(Date d1, Date d2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return !dateFormat.format(d1).equals(dateFormat.format(d2));
    }

    private static int inHoursBetween(Date date1, Date date2) {
        long ms = date2.getTime() - date1.getTime();
        return (int) (ms / (1000 * 60 * 60));
    }
}
