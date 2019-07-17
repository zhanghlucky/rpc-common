package com.hui.zhang.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil extends org.apache.commons.lang3.time.DateUtils{

	public final static String DATE_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_DATE_PATTERN = "yyyy-MM-dd";
	public final static String DATE_TIME_PATTERN = "HH:mm:ss";
	public final static String DATEDATEPATTERN = "yyyy-MM-dd";
	public final static String DATE_MM_PATTERN = "MM";
	public final static String DATE_DAY_PATTERN = "dd";

	public static Date currDate() {
		return new Date();
	}

	public static String currDateFull() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		StringBuilder buf = new StringBuilder();
		buf.append(c.get(1));
		buf.append("-");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(2) + 1), 2, "0"));
		buf.append("-");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(5)), 2, "0"));
		buf.append(" ");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(11)), 2, "0"));
		buf.append(":");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(12)), 2, "0"));
		buf.append(":");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(13)), 2, "0"));
		return buf.toString();
	}

	public static String currMonth() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		return StringUtils.leftPad(String.valueOf(c.get(2) + 1), 2, "0");
	}

	public static String currDay() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		return StringUtils.leftPad(String.valueOf(c.get(5)), 2, "0");
	}

	public static Date parseFull(String source) {
		return parse(source, new SimpleDateFormat(DATE_DATETIME_PATTERN));
	}

	public static Date parseDate(String source) {
		return parse(source, new SimpleDateFormat(DATE_DATE_PATTERN));
	}

	public static Date parseTime(String source) {
		return parse(source, new SimpleDateFormat(DATE_TIME_PATTERN));
	}

	public static Date parse(String source, String pattern) {
		return parse(source, new SimpleDateFormat(pattern));
	}

	public static Date parse(String source, SimpleDateFormat sdf) {
		if (StringUtils.isEmpty(source))
			return null;
		try {
			return sdf.parse(source);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	public static Date parse(String source) {
		if (StringUtils.isEmpty(source))
			return null;
		switch (source.length()) {
		case 19:
			return parseFull(source);
		case 10:
			return parseDate(source);
		case 8:
			return parseTime(source);
		}
		throw new IllegalArgumentException("Could not parse date");
	}

	public static String formatFull(Date date) {
		return format(date, new SimpleDateFormat(DATE_DATETIME_PATTERN));
	}

	public static String formatDate(Date date) {
		return format(date, new SimpleDateFormat(DATE_DATE_PATTERN));
	}

	public static String formatTime(Date date) {
		return format(date, new SimpleDateFormat(DATE_TIME_PATTERN));
	}

	public static String format(Date date, String pattern) {
		return format(date, new SimpleDateFormat(pattern));
	}

	public static String format(Date date, DateFormat df) {
		if (date == null)
			return null;
		return df.format(date);
	}

	public static String format(String srcDate, String srcPattern, String descPattern) {
		return format(parse(srcDate, srcPattern), descPattern);
	}

	@SuppressWarnings("deprecation")
	public static String format(Date date) {
		if (date == null)
			return null;
		if (String.valueOf(date.getTime()).endsWith("00000")) {
			return format(date, new SimpleDateFormat(DATE_DATE_PATTERN));
		}
		if ((date.getYear() == 70) && (date.getMonth() == 0) && (date.getDate() == 1)) {
			return format(date, new SimpleDateFormat(DATE_TIME_PATTERN));
		}
		return format(date, new SimpleDateFormat(DATE_DATETIME_PATTERN));
	}

	public static Calendar getCalendar(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	public static Date offset(Date date, int field, int amount) {
		if (date == null)
			return null;
		Calendar newDate = getCalendar(date);
		newDate.add(field, amount);
		return newDate.getTime();
	}

	public static void offsetMinutes(Calendar date, int amount) {
		date.add(12, amount);
	}

	public static Date offsetMinutes(Date date, int amount) {
		return offset(date, 12, amount);
	}

	public static Date offsetSeconds(Date date, int amount) {
		return offset(date, 13, amount);
	}

	public static void offsetDays(Calendar date, int amount) {
		date.add(6, amount);
	}

	public static Date offsetDays(Date date, int amount) {
		return offset(date, 6, amount);
	}

	public static void offsetMonths(Calendar date, int amount) {
		date.add(2, amount);
	}

	public static Date offsetMonths(Date date, int amount) {
		return offset(date, 2, amount);
	}

	public static void offsetYears(Calendar date, int amount) {
		date.add(1, amount);
	}

	public static Date offsetYears(Date date, int amount) {
		return offset(date, 1, amount);
	}

	public static Date nextAnyYear(Date date, int amount) {
		if (date == null)
			return null;
		Calendar srcDate = getCalendar(date);
		Calendar newDate = getCalendar(date);
		newDate.set(1, srcDate.get(1) + amount);
		newDate.set(2, srcDate.get(2));
		newDate.set(5, srcDate.get(5) - 1);
		return newDate.getTime();
	}

	public static Date offsetDayToFloor(Date date) {
		if (date == null) {
			return null;
		}
		return parseFull(formatDate(date) + " 00:00:00");
	}

	public static Date offsetDayToCeiling(Date date) {
		if (date == null) {
			return null;
		}
		return parseFull(formatDate(date) + " 23:59:59");
	}

	public static Date offsetMonthToFloor(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = getCalendar(date);
		c.set(5, 1);
		c.set(11, 0);
		c.set(12, 0);
		c.set(13, 0);
		c.set(14, 0);
		return c.getTime();
	}

	public static Date offsetMonthToCeiling(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = getCalendar(date);
		c.set(5, getLastDayOfMonth(date));
		c.set(11, 23);
		c.set(12, 59);
		c.set(13, 59);
		c.set(14, 999);
		return c.getTime();
	}

	public static int compareTo(Date srcDate, Date descDate) {
		Calendar srcDateC = getCalendar(srcDate);
		Calendar descDateC = getCalendar(descDate);
		int srcYear = srcDateC.get(1);
		int descYear = descDateC.get(1);
		if (srcYear == descYear) {
			int srcDayOfYear = srcDateC.get(6);
			int descDayOfYear = descDateC.get(6);
			if (srcDayOfYear == descDayOfYear) {
				return 0;
			}
			return srcDayOfYear > descDayOfYear ? 1 : -1;
		}
		return srcYear > descYear ? 1 : -1;
	}

	public static int compareToMonthAndDay(Date srcDate, Date descDate) {
		return compareToMonthAndDay(srcDate, descDate, false);
	}

	public static int compareToMonthAndDay(Date srcDate, Date descDate, boolean ignoreLeapYear_2_29) {
		Calendar srcDateC = getCalendar(srcDate);
		Calendar descDateC = getCalendar(descDate);
		int srcMonth = srcDateC.get(2);
		int descMonth = descDateC.get(2);
		if (srcMonth == descMonth) {
			int srcDayOfMonth = srcDateC.get(5);
			int descDayOfMonth = descDateC.get(5);
			if (srcDayOfMonth == descDayOfMonth) {
				return 0;
			}
			if (ignoreLeapYear_2_29) {
				if ((srcDateC.get(5) == 29) && (!isLeapYear(descDate))) {
					srcDayOfMonth--;
				}
				if (srcDayOfMonth == descDayOfMonth) {
					return 0;
				}
			}
			return srcDayOfMonth > descDayOfMonth ? 1 : -1;
		}
		return srcMonth > descMonth ? 1 : -1;
	}

	public static int getDayOfMonth(Date date) {
		return getCalendar(date).get(5);
	}

	public static int getMonth(Date date) {
		return getCalendar(date).get(2);
	}

	public static int getDayOfYear(Date date) {
		return getCalendar(date).get(6);
	}

	public static int getYear(Date date) {
		return getCalendar(date).get(1);
	}

	public static int getDayOfWeek(Date date) {
		return getCalendar(date).get(7);
	}

	public static int getWeekOfYear(Date date) {
		return getCalendar(date).get(3);
	}

	public static int getQuarter(Date date) {
		return (int) Math.ceil((getCalendar(date).get(2) + 1) / 3.0D);
	}

	public static boolean isLeapYear(Date date) {
		int year = getYear(date);
		return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
	}

	public static int getLastDayOfMonth(Date date) {
		int lastDay = 0;
		int month = getMonth(date);
		switch (month) {
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			lastDay = 31;
			break;
		case 1:
			lastDay = isLeapYear(date) ? 29 : 28;
			break;
		case 3:
		case 5:
		case 8:
		case 10:
		default:
			lastDay = 30;
		}
		return lastDay;
	}

	public static int getLastDayOfYear(Date date) {
		return isLeapYear(date) ? 366 : 365;
	}

	public static int getInterval(Date fromDate, Date toDate, int field) {
		Calendar fromDateC = getCalendar(fromDate);
		Calendar toDateC = getCalendar(toDate);
		if (toDateC.before(fromDate)) {
			return 0;
		}
		if (1 == field) {
			return toDateC.get(1) - fromDateC.get(1);
		}
		if (2 == field) {
			int months = 0;
			int intervalYears = toDateC.get(1) - fromDateC.get(1);
			if (intervalYears == 0) {
				months = toDateC.get(2) - fromDateC.get(2);
			} else {
				int months_h = 11 - fromDateC.get(2);
				int months_f = toDateC.get(2) + 1;
				int months_m = intervalYears <= 1 ? 0 : (intervalYears - 1) * 12;

				months = months_h + months_m + months_f;
			}
			return months;
		}
		if (5 == field) {
			int days = 0;
			int intervalYears = toDateC.get(1) - fromDateC.get(1);
			if (intervalYears == 0) {
				days = toDateC.get(6) - fromDateC.get(6);
			} else {
				int days_h = getLastDayOfYear(fromDate) - fromDateC.get(6);
				int days_f = toDateC.get(6);
				int days_m = 0;
				if (intervalYears > 1) {
					Date tmpDate = null;
					for (int i = 1; i < intervalYears; i++) {
						tmpDate = offsetYears(fromDate, 1);
						days_m += getLastDayOfYear(tmpDate);
					}
				}
				days = days_h + days_m + days_f;
			}
			return days;
		}
		return 0;
	}

	public static int getIntervalDays(Date fromDate, Date toDate) {
		return getInterval(fromDate, toDate, 5);
	}

	public static int getIntervalMonths(Date fromDate, Date toDate) {
		return getInterval(fromDate, toDate, 2);
	}

	public static int getIntervalYears(Date fromDate, Date toDate) {
		return getInterval(fromDate, toDate, 1);
	}

	public static int birthdayToAge(Date date) {
		return getIntervalYears(date, currDate());
	}

	public static String formatFriendlyDate(Date date) {
		Calendar currC = getCalendar(currDate());
		Calendar dateC = getCalendar(date);
		int cYear = currC.get(1);
		int year = dateC.get(1);
		switch (cYear - year) {
		case 0:
			break;
		case 1:
			return format(date, "去年M月d日 H:m:s");
		case 2:
			return format(date, "前年M月d日 H:m:s");
		default:
			return format(date, "yyyy年M月d日 H:m:s");
		}
		int cMonth = currC.get(2);
		int month = dateC.get(2);
		switch (cMonth - month) {
		case 0:
			break;
		default:
			return format(date, "M月d日 H:m:s");
		}
		int cDay = currC.get(5);
		int day = dateC.get(5);
		switch (cDay - day) {
		case 0:
			break;
		case 1:
			return format(date, "昨天 H:m:s");
		case 2:
			return format(date, "前天 H:m:s");
		default:
			return format(date, "M月d日 H:m:s");
		}
		int cHour = currC.get(11);
		int hour = dateC.get(11);
		switch (cHour - hour) {
		case 0:
			break;
		default:
			return format(date, "H:m:s");
		}
		int cMinute = currC.get(12);
		int minute = dateC.get(12);
		switch (cMinute - minute) {
		case 0:
			break;
		default:
			return cMinute - minute + "分钟之前";
		}
		int cSecond = currC.get(13);
		int second = dateC.get(13);
		switch (cSecond - second) {
		case 0:
			return "刚刚";
		}
		return cSecond - second + "秒之前";
	}
	/*
 	* 当前日期往前推 N 个月 N 为自然数
 	*/
	public static long getPrevYeay(int month){
		Calendar cl = Calendar.getInstance();
		cl.add(Calendar.MONTH, -month);
		Date dateFrom = cl.getTime();
		return dateFrom.getTime();
	}

	public static int prevDay(long time){
		int days = (int)((System.currentTimeMillis() - time ) / (1000*3600*24));
		return  days;
	}
	public static Long getCurrentTime(){
		return Long.parseLong("" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
	}

	public static  void main(String [] args){
		System.out.println(DateUtil.prevDay(1514873132000L));
	}


}
