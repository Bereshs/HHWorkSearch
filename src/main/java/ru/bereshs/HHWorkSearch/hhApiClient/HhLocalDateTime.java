package ru.bereshs.HHWorkSearch.hhApiClient;

import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

public class HhLocalDateTime {

    public static LocalDateTime decodeData(String data) {
        String pattern="EEE, dd MMM yyyy HH:mm:ss z";
        String pattern1="EEE, dd-MMM-yyyy HH:mm:ss z";

        if(data.contains("-")){
            pattern=pattern1;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDateTime.parse(data, formatter);
    }

    public static LocalDateTime decodeLocalData(String data) {
        String pattern="yyyy-mm-dd HH:mm:ss";
        String resultData = data.substring(0, data.indexOf("+"));//.replace("T", " ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return LocalDateTime.parse(resultData);
    }

}
