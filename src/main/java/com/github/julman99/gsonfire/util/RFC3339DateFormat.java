package com.github.julman99.gsonfire.util;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @autor: julio
 */
public class RFC3339DateFormat extends DateFormat {

    private static final Pattern TIMEZONE_PATTERN = Pattern.compile("(.*)([+-][0-9][0-9])\\:?([0-9][0-9])$");
    private static final Pattern MILLISECONDS_PATTERN = Pattern.compile("(.*)\\.([0-9]+)(.*)");

    private final SimpleDateFormat rfc3339Parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private final SimpleDateFormat rfc3339Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public RFC3339DateFormat(TimeZone serializationTimezone) {
        this.rfc3339Formatter.setTimeZone(serializationTimezone);
    }

    public RFC3339DateFormat() {
        this(TimeZone.getTimeZone("UTC"));
    }

    private String generateTimezone(long time, TimeZone serializationTimezone){
        if(serializationTimezone.getOffset(time) == 0){
            return "Z";
        }

        int offset = (int) (serializationTimezone.getOffset(time) / 1000L);
        int hours = offset / 3600;
        int minutes = Math.abs((offset - hours * 3600) / 60);
        String sign = hours >= 0 ? "+" : "-";

        return sign + String.format("%02d", Math.abs(hours)) + ":" + String.format("%02d", minutes);
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {

        StringBuffer formatted = new StringBuffer();

        formatted.append(rfc3339Formatter.format(date).toString());

        //Add milliseconds
        long time = date.getTime();
        if(time % 1000L != 0){
            String fraction = Long.toString((time % 1000L));
            formatted.append("." + fraction);
        }

        //Timezone
        String timezoneStr = generateTimezone(time, this.rfc3339Formatter.getTimeZone());
        formatted.append(timezoneStr);

        return formatted;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {

        //Filter milliseconds
        long millis = 0;
        if(source.contains(".")){
            Matcher matcher = MILLISECONDS_PATTERN.matcher(source);
            String millisStr = matcher.replaceAll("$2");
            millis = Long.parseLong(millisStr);
            source = matcher.replaceAll("$1") + matcher.replaceAll("$3");
        }

        //Filter ending in Z
        if(source.endsWith("Z")){
            source = source.substring(0, source.length() -1) + "-0000";
        } else {
            //Check if we have timezone information present
            Matcher matcher = TIMEZONE_PATTERN.matcher(source);
            if (matcher.matches()) {
                //Filter colon in timezone
                source = matcher.replaceAll("$1") + matcher.replaceAll("$2") + matcher.replaceAll("$3");
            } else {
                //It appears we don't have any timezone info or Z at the end of the date
                //We will assume it is RFC3339
                source += "-0000";
            }
        }

        try {
            Date res = rfc3339Parser.parse(source);
            if(millis > 0){
                res = new Date(res.getTime() + millis);
            }
            pos.setIndex(source.length());
            return res;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
