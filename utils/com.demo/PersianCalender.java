
import android.text.TextUtils;

import com.cvte.tv.common.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 波斯日历
 * demo:
 * String format = "yyyy/MM/dd HH:mm:ss";
 * PersianCalender persian = new PersianCalender(format);
 * persian.formatPersianCalender();
 */
public class PersianCalender extends GregorianCalendar {

    private static final double PERSIAN_EPOCH = 1948320.5;
    private static final double GREGORIAN_EPOCH = 1721425.5;
    private Locale mDefaultLocale = Locale.US;
    private int MONTHS = 12;

    public static int[] persianDaysInMonth = {31, 31, 31, 31, 31, 31, 30,
        30, 30, 30, 30, 29};
    // 每个月最后一天是整年的第几天
    public static int[] persianDaysOfYear = {31, 62, 93, 124, 155, 186, 216,
        246, 276, 306, 336, 365};

    public static int DAY_OF_YEAR = 365;
    private String format;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("", mDefaultLocale);

    // 波斯月
    public static final String[] month_str = {"Farvardin", "Ordibehesht", "Khordad",
        "Tir", "Mordad", "Shahrivar",
        "Mehr", "Aban", "Azar",
        "Dey", "Bahman", "Esfand"};

    public PersianCalender() {
        super();
    }

    public PersianCalender(String format) {
        super();
        this.format = format;
    }

    public void applyPattern(String format) {
        this.format = format;
    }

    public double mod(double a, double b) {
        return (a - b * Math.floor(a / b));
    }

    private double persianToJd(int year, int month, int day) {

        int epbase = year - 474;
        double epyear = 474 + mod(epbase, 2820);
        if (month > 7) {
            month = (month - 1) * 30 + 6;
        } else {
            month = (month - 1) * 31;
        }
        return day + month
            + Math.floor(((epyear * 682) - 110) / 2816) + (epyear - 1) * 365
            + Math.floor(epbase / 2820) * 1029983.0 + (PERSIAN_EPOCH - 1);
    }

    private double gregorianToJd(int year, int month, int day) {
        int v;
        if (month <= 2) {
            v = 0;
        } else if (isLeapGregorian(year)) {
            v = -1;
        } else {
            v = -2;
        }
        return (GREGORIAN_EPOCH - 1) + (365 * (year - 1))
            + Math.floor((year - 1) / 4)
            + (-Math.floor((year - 1) / 100))
            + Math.floor((year - 1) / 400)
            + Math.floor((((367 * month) - 362) / 12) + v + day);
    }

    /**
     * 是否是闰年
     *
     * @param year 年
     * @return 是否是闰年
     */
    public boolean isLeapGregorian(int year) {
        if (((year % 4) == 0 && (year % 100) != 0) || (year % 400) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用于判断波斯年是否是闰年，公式是：
     * 将这一年加38后，乘以31，再除以128。当结果的小数部分大于或等于0.31时，所指的这一年是一个平年。
     * 另一方面，如果小于0.31，则这一年是闰年，但是如果连续两年小于0.31，则第一年是闰年而第二年是平年。
     *
     * @param persianYear 波斯年
     * @return 是否闰年
     */
    public boolean isLeapPersian(int persianYear) {
        double compareValue = 0.31;
        double result = (persianYear + 38) * 31 % 128;
        if (result / 128 >= compareValue) {
            return false;
        }
        double preYear = (persianYear - 1 + 38) * 31 % 128;
        if (preYear / 128 < compareValue) {
            // 连续两年是小于0.31,第一年是闰年而第二年是平年
            return false;
        }
        return true;
    }

    /**
     * 波斯日历转公历
     *
     * @param persianYear  波斯年
     * @param persianMonth 波斯月
     * @param persianDay   波斯日
     * @return 公历
     */
    public YearMonthDate persianToGregorian(int persianYear, int persianMonth, int persianDay) {
        double jd = persianToJd(persianYear, persianMonth, persianDay);
        double wjd = Math.floor(jd - 0.5) + 0.5;
        int depoch = (int) (wjd - GREGORIAN_EPOCH);
        double quadricent = Math.floor(depoch / 146097);
        double dqc = mod(depoch, 146097);
        double cent = Math.floor(dqc / 36524);
        double dcent = mod(dqc, 36524);
        double quad = Math.floor(dcent / 1461);
        double dquad = mod(dcent, 1461);
        double yindex = Math.floor(dquad / 365);
        int year = (int) ((quadricent * 400) + (cent * 100) + (quad * 4) + yindex);
        if (!(cent == 4 || yindex == 4)) {
            year += 1;
        }
        int yearday = (int) (wjd - gregorianToJd(year, 1, 1));
        int leapadj = 0;
        if (wjd < gregorianToJd(year, 3, 1)) {
            leapadj = 0;
        } else if (isLeapGregorian(year)) {
            leapadj = 1;
        } else {
            leapadj = 2;
        }
        int month = (int) Math.floor((((yearday + leapadj) * 12) + 373) / 367);
        int day = (int) ((wjd - gregorianToJd(year, month, 1)) + 1);
        return new YearMonthDate(year, month, day);
    }

    /**
     * 公历转波斯日历
     *
     * @param gregorianYear  年
     * @param gregorianMonth 月
     * @param gregorianDay   日
     * @return 波斯日历
     */
    public YearMonthDate gregorianToPersian(int gregorianYear,
                                            int gregorianMonth, int gregorianDay) {
        double jd = gregorianToJd(gregorianYear, gregorianMonth, gregorianDay);
        jd = Math.floor(jd) + 0.5;
        double depoch = jd - persianToJd(475, 1, 1);
        double cycle = Math.floor(depoch / 1029983);
        double cyear = mod(depoch, 1029983);
        int ycycle;
        if (cyear == 1029982) {
            ycycle = 2820;
        } else {
            double aux1 = Math.floor(cyear / 366);
            double aux2 = mod(cyear, 366);
            ycycle = (int) (Math.floor(((2134 * aux1)
                + (2816 * aux2) + 2815) / 1028522) + aux1 + 1);
        }

        int year = (int) (ycycle + (2820 * cycle) + 474);
        if (year <= 0) {
            year = year - 1;
        }
        double yday = (jd - persianToJd(year, 1, 1)) + 1;

        int month;
        if (yday <= 186) {
            month = (int) Math.ceil(yday / 31);
        } else {
            month = (int) Math.ceil((yday - 6) / 30);
        }
        int day = (int) ((jd - persianToJd(year, month, 1)) + 1);
        return new YearMonthDate(year, month, day);
    }

    /**
     * 获取波斯日历的format
     *
     * @return format 结果
     */
    public String formatPersianCalender() {
        return formatPersianCalender(getTimeInMillis());
    }

    /**
     * 获取波斯日历 format
     *
     * @param timeInMillis 时间戳
     * @return format结果
     */
    public String formatPersianCalender(long timeInMillis) {
        if (TextUtils.isEmpty(format)) {
            throw new IllegalArgumentException("please applyPattern first");
        }
        Calendar calendar = Calendar.getInstance();
        String formatDate = format.trim();
        calendar.setTimeInMillis(timeInMillis);
        YearMonthDate persian = gregorianToPersian(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH));
        String persianDate = formatDate.replace("yyyy", String.valueOf(persian.getYear()));
        persianDate = persianDate.replace("MM", StringUtils.format("%02d", persian.getMonth()));
        persianDate = persianDate.replace("dd", StringUtils.format("%02d", persian.getDate()));
        simpleDateFormat.applyPattern(persianDate);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(timeInMillis);
    }

    @Override
    public void add(int field, int amount) {
        if (amount == 0) {
            return;
        }
        switch (field) {
            default:
            case HOUR:
            case HOUR_OF_DAY:
            case MINUTE:
            case SECOND:
            case MILLISECOND:
                super.add(field, amount);
                break;
            case YEAR:
                // 年月日的加减需要转为波斯日历再加减，完成后再转为公历
                addYear(gregorianToPersian(get(YEAR), get(MONTH) + 1, get(DAY_OF_MONTH)),
                        amount);
                break;
            case MONTH:
                addMonth(gregorianToPersian(get(YEAR), get(MONTH) + 1, get(DAY_OF_MONTH)),
                        amount);
                break;
            case DAY_OF_MONTH:
                addDayOfMonth(gregorianToPersian(get(YEAR), get(MONTH) + 1, get(DAY_OF_MONTH)),
                        amount);
                break;
        }
    }

    /**
     * 加减年份
     *
     * @param yearMonthDate 波斯年月日
     * @param amount        加减的值
     */
    private void addYear(YearMonthDate yearMonthDate, int amount) {
        yearMonthDate.setYear(yearMonthDate.getYear() + amount);
        yearMonthDate = persianToGregorian(
            yearMonthDate.getYear(),
            yearMonthDate.getMonth(),
            yearMonthDate.getDate());
        set(Calendar.YEAR, yearMonthDate.getYear());
        set(Calendar.MONTH, yearMonthDate.getMonth() - 1);
        set(Calendar.DAY_OF_MONTH, yearMonthDate.getDate());
    }

    /**
     * 加减月份
     *
     * @param yearMonthDate 波斯年月日
     * @param amount        加减的值
     */
    private void addMonth(YearMonthDate yearMonthDate, int amount) {
        int year = yearMonthDate.getYear();
        int month = yearMonthDate.getMonth();
        int day = yearMonthDate.getDate();
        int addedMonthIndex = month + amount - 1;
        while (addedMonthIndex < 0) {
            addedMonthIndex += MONTHS;
            year--;
        }
        if (addedMonthIndex >= MONTHS) {
            year += (addedMonthIndex / MONTHS);
            addedMonthIndex %= MONTHS;
        }
        month = addedMonthIndex + 1;
        if (persianDaysInMonth[addedMonthIndex] < day) {
            if (addedMonthIndex == (persianDaysInMonth.length - 1) && isLeapPersian(year)) {
                // 加上值后的月份是12月，并且当前年是闰年
                day = persianDaysInMonth[addedMonthIndex] + 1;
            } else {
                day = persianDaysInMonth[addedMonthIndex];
            }
        }
        yearMonthDate = persianToGregorian(year, month, day);
        set(Calendar.YEAR, yearMonthDate.getYear());
        set(Calendar.MONTH, yearMonthDate.getMonth() - 1);
        set(Calendar.DAY_OF_MONTH, yearMonthDate.getDate());
    }

    /**
     * 加减天数
     *
     * @param yearMonthDate 波斯年月日
     * @param amount        加减的值
     */
    private void addDayOfMonth(YearMonthDate yearMonthDate, int amount) {
        // 当前是整年第几天
        int curDaysInYear = yearMonthDate.getMonth() == 1
            ? 0 : persianDaysOfYear[yearMonthDate.getMonth() - 2];
        curDaysInYear += yearMonthDate.getDate();
        // 加上后的天数
        int addedDays = curDaysInYear + amount;
        if (amount > 0) {
            // 加上 amount 有可能变为下一年
            while (true) {
                int curYear = yearMonthDate.getYear();
                int daysOfYear = DAY_OF_YEAR + (isLeapPersian(curYear) ? 1 : 0);
                if (addedDays <= daysOfYear) {
                    break;
                }
                yearMonthDate.setYear(yearMonthDate.getYear() + 1);
                addedDays -= daysOfYear;
            }
        } else {
            while (addedDays <= 0) {
                int curYear = yearMonthDate.getYear() - 1;
                int daysOfYear = DAY_OF_YEAR + (isLeapPersian(curYear) ? 1 : 0);
                addedDays += daysOfYear;
                yearMonthDate.setYear(curYear);
            }
        }
        int month = 0;
        for (int daysOfMoth : persianDaysOfYear) {
            month++;
            if (addedDays <= daysOfMoth) {
                break;
            }
            addedDays -= persianDaysInMonth[month - 1];
        }
        yearMonthDate.setMonth(month);
        yearMonthDate.setDate(addedDays);
        yearMonthDate = persianToGregorian(yearMonthDate.getYear(),
            yearMonthDate.getMonth(),
            yearMonthDate.getDate());
        set(Calendar.YEAR, yearMonthDate.getYear());
        set(Calendar.MONTH, yearMonthDate.getMonth() - 1);
        set(Calendar.DAY_OF_MONTH, yearMonthDate.getDate());
    }

    /**
     * 用于保存年月日
     */
    public static class YearMonthDate {

        public YearMonthDate(int year, int month, int date) {
            this.year = year;
            this.month = month;
            this.date = date;
        }

        // 实际的年
        private int year;
        // 实际的月
        private int month;
        // 实际的日
        private int date;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }
    }

}
