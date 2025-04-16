package com.example.todolistapp;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    // Date format constants
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public static String getTodayDate() {
        return dbDateFormat.format(new Date());
    }

    public static String formatDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return dbDateFormat.format(cal.getTime());
    }
}