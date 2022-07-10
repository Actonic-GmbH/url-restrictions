package proficient.developer.plugins.jira.urlrestrictions.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class DateFormat {
    public static String yyyyMMdd_hhmm = "yyyy-MM-dd hh:mm";

    public static Date parseDate(String dateString, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(dateString);
    }
}
