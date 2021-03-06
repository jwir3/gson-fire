package com.github.julman99.gsonfire.gson;

import java.util.Date;

/**
 * @autor: julio
 */
public class DateUnixtimeSecondsTypeAdapter extends DateUnixtimeMillisTypeAdapter {

    public DateUnixtimeSecondsTypeAdapter(boolean allowNegativeTimestamp) {
        super(allowNegativeTimestamp);
    }

    @Override
    protected long toTimestamp(Date date) {
        return super.toTimestamp(date) / 1000L;
    }

    @Override
    protected Date fromTimestamp(long timestamp) {
        return super.fromTimestamp(timestamp * 1000L);
    }

}
