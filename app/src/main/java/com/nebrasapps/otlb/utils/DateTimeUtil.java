package com.nebrasapps.otlb.utils;


import android.text.format.DateFormat;


import com.nebrasapps.otlb.BuildConfig;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ============================================================================
 *                                   ⁽(◍˃̵͈̑ᴗ˂̵͈̑)⁽
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public class DateTimeUtil {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static String getMonthName(int num) {
        String month = BuildConfig.FLAVOR;
        String[] months = new DateFormatSymbols().getShortMonths();
        if (num < 0 || num > 11) {
            return month;
        }
        return months[num];
    }


    public static String convertDateToMonthName(String date) {
        Calendar c = Calendar.getInstance();
        String convertedDate = BuildConfig.FLAVOR;
        String[] splits = date.split("-");
        String month = BuildConfig.FLAVOR;
        String day = BuildConfig.FLAVOR;
        String year = BuildConfig.FLAVOR;
        if (splits != null) {
            day = splits[2];
            month = splits[1];
            year = splits[0];
            month = getMonthName(Integer.parseInt(month) - 1);
        }
        return  month + " " + day +" " + year;
    }

    public static String getDayName(int year, int month, int day) {
        Date d = null;
        try {
            d = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(day + "/" + month + "/" + year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            return new SimpleDateFormat("EEEE").format(d);
        } catch (Exception e2) {
            e2.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }
    public static int getDayDiff(String startdate, String enddate) {
        int diffInDays=0;
        try {


        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date d = null;
        Date d1 = null;
        Calendar cal = Calendar.getInstance();
        try {
            d = dfDate.parse(enddate);
            d1 = dfDate.parse(startdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

         diffInDays = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60 * 24));
        }catch (Exception e)
        {

        }
        return diffInDays;
    }
    public static int getTimeDiff(String startdate, String enddate) {
        int diffInHours=0;
        try {


            SimpleDateFormat dfDate  = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            Date d = null;
            Date d1 = null;
            Calendar cal = Calendar.getInstance();
            try {
                d = dfDate.parse(enddate);
                d1 = dfDate.parse(startdate);//Returns 15/10/2012
            } catch (ParseException e) {
                e.printStackTrace();
            }

            diffInHours = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60));
        }catch (Exception e)
        {

        }
        return diffInHours;
    }
    public static int getHourDiff(String startdate, String enddate) {
        int diffInHours=0;
        try {


            SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            Date d = null;
            Date d1 = null;
            Calendar cal = Calendar.getInstance();
            try {
                d = dfDate.parse(enddate);
                d1 = dfDate.parse(startdate);//Returns 15/10/2012
            } catch (ParseException e) {
                e.printStackTrace();
            }

            diffInHours = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60));
        }catch (Exception e)
        {

        }
        return diffInHours;
    }
    public static boolean checkDates(String startdate, String enddate) {
        boolean valid=false;
        try {


            SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date d = null;
            Date d1 = null;
            Calendar cal = Calendar.getInstance();
            try {
                d = dfDate.parse(enddate);
                d1 = dfDate.parse(startdate);//Returns 15/10/2012
                if(d.equals(d1))
                {
                    valid=true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }catch (Exception e)
        {

        }
        return valid;
    }
    public static String parseTime(String time)
    {
        //region header view
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);

        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        //sdfs.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dt;
        String result="";
        try {
            dt = sdf.parse(time);
            result=sdfs.format(dt);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  result;
    }
    public static String parseNotificationTime(String time)
    {
        //region header view
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        //sdfs.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dt;
        String result="";
        try {
            dt = sdf.parse(time);
            result=sdfs.format(dt);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  result;
    }

    public static boolean validEndDate(String startDate, String endDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            if(sdf.parse(endDate).after(sdf.parse(startDate))|| sdf.parse(startDate).equals(sdf.parse(endDate)))
            {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    public static boolean validStartDate(String startDate, String endDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            if(sdf.parse(startDate).before(sdf.parse(endDate)) || sdf.parse(startDate).equals(sdf.parse(endDate)) )
            {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean validateTime(String starttime, String endtime) {

            try
            {
                String string1 =  endtime;
                Date time1 = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH).parse(string1);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(time1);

                String string2 =starttime;
                Date time2 = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH).parse(string2);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(time2);

                if (calendar2.getTime().after(calendar1.getTime()) )
                {

                    return false;
                }
                else{

                    return true;
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }


        return false;
    }

    public static boolean checkDateTime(String startdate) {
        boolean valid=false;
        try {


            SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            Date d = null;
            Date d1 = null;
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            String today=dfDate.format(cal.getTime());
            try {
                d = dfDate.parse(startdate);
                d1 = dfDate.parse(today);//Returns 15/10/2012
                if(d.equals(d1) || d1.before(d))
                {
                    valid=true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }catch (Exception e)
        {

        }
        return valid;
    }
    public static Date parseStringtoDate(String startdate) {
        Date d = null;
        try {


            SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            try {
                d = dfDate.parse(startdate);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }catch (Exception e)
        {

        }
        return d;
    }
    public static boolean checkTimestamp(String startdate, String enddate) {
        boolean valid=false;
        try {


            SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            Date d = null;
            Date d1 = null;
            Calendar cal = Calendar.getInstance();
            try {
                d = dfDate.parse(enddate);
                d1 = dfDate.parse(startdate);//Returns 15/10/2012
                if(d1.before(d))
                {
                    valid=true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }catch (Exception e)
        {

        }
        return valid;
    }

    public static String getFormattedDate(String date) {
        Calendar smsTime = Calendar.getInstance();
        String[] dateval=date.split("-");
        String[] timeval=dateval[2].split(" ");
        smsTime.set(Integer.valueOf(dateval[0]), Integer.valueOf(dateval[1]), Integer.valueOf(timeval[0]),0,0,0);
       // smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EE, MMMM d";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "اليوم " ;
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "أمس " ;
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy", smsTime).toString();
        }
    } public static String getFormattedDateTime(String date) {
        Calendar smsTime = Calendar.getInstance();
        String[] dateval=date.split("-");
        String[] timeval=dateval[2].split(" ");
        smsTime.set(Integer.valueOf(dateval[0]), Integer.valueOf(dateval[1]), Integer.valueOf(timeval[0]),0,0,0);
        // smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EE, MMMM d";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today " ;
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday " ;
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy", smsTime).toString();
        }
    }
    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
   /* public static String getTimeAgo(String date, Context ctx) {
        date=getDateCurrentTimeZone(date);
        long time=getDateInMillis(date);

        long now = System.currentTimeMillis();;
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        String lang = SharedData.getPref(Constants.mLanguage,"ar");
        if(lang.equalsIgnoreCase("en")) {
            if (diff < MINUTE_MILLIS) {
                return ctx.getResources().getString(R.string.just_now);
            } else if (diff < 2 * MINUTE_MILLIS) {
                return ctx.getResources().getString(R.string.a_minute);
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " " + ctx.getResources().getString(R.string.minutes_ago);
            } else if (diff < 90 * MINUTE_MILLIS) {
                return ctx.getResources().getString(R.string.hour_ago);
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " " + ctx.getResources().getString(R.string.hours_ago);
            } else if (diff < 48 * HOUR_MILLIS) {
                return ctx.getResources().getString(R.string.yesterday);
            } else {
                return diff / DAY_MILLIS + "  " + ctx.getResources().getString(R.string.days_ago);
            }
        }else
        {
            if (diff < MINUTE_MILLIS) {
                return ctx.getResources().getString(R.string.just_now);
            } else if (diff < 2 * MINUTE_MILLIS) {
                return ctx.getResources().getString(R.string.a_minute);
            } else if (diff < 50 * MINUTE_MILLIS) {
                return  ctx.getResources().getString(R.string.ago)+" "+diff / MINUTE_MILLIS + " " +ctx.getResources().getString(R.string.minutes_ago);
            } else if (diff < 90 * MINUTE_MILLIS) {
                return ctx.getResources().getString(R.string.hour_ago);
            } else if (diff < 24 * HOUR_MILLIS) {
                return ctx.getResources().getString(R.string.ago)+" "+diff / HOUR_MILLIS + " " +ctx.getResources().getString(R.string.hours_ago);
            } else if (diff < 48 * HOUR_MILLIS) {
                return ctx.getResources().getString(R.string.yesterday);
            } else {
                return  ctx.getResources().getString(R.string.ago)+" "+diff / DAY_MILLIS + "  "+ctx.getResources().getString(R.string.days_ago);
            }
        }
    }*/
    public static String getDateCurrentTimeZone(String date) {
        String result="";
        try{
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsed = sourceFormat.parse(date); // => Date is in UTC now

            TimeZone tz = TimeZone.getDefault();
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            destFormat.setTimeZone(tz);

            result = destFormat.format(parsed);
        }catch (Exception e) {
        }
        return result;
    }
}
