package com.daycle.daycleapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by neoam on 2016-08-24.
 * 각종 편리한 메소드 정의
 */
// 유틸 클래스
public class NeoUtil {

    public NeoUtil(){

    }

    // null에 안전한 오브젝트 비교
    public static boolean Equals(Object o1, Object o2){

        if(o1 == null || o2 == null)
            return false;

        if(o1.equals(o2))
            return true;

        return false;
    }

    // int형 List를 배열로
    public static int[] toArrayInt(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    // String형 List를 배열로
    public static String[] toArrayString(List<Integer> integers)
    {
        String[] ret = new String[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = String.valueOf(iterator.next().intValue());
        }
        return ret;
    }

    // url 링크
    public static void link(Context context, String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    // url 밸리데이션
    public static boolean validateUrl(String url){
        return Pattern.compile("^(file|gopher|news|nntp|telnet|https?|ftps?|sftp)://([a-z0-9-]+\\.)+[a-z0-9]{2,4}.*$").matcher(url).find();
    }

    // 숫자인지 체크
    public static boolean isNumber(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    // 큰 값을 문자로 대체
    public static String formatCount(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatCount(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatCount(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    // 유니크 아이디 가져오기
    public static String getDeviceUUID(final Context context) {
        final String id = null;

        UUID uuid = null;
        if (id != null) {
            uuid = UUID.fromString(id);
        } else {
            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            try {
                if (!"9774d56d682e549c".equals(androidId)) {
                    try {
                        uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return uuid.toString();
    }

    // 현재 앱 버전 가져오기
    public static String getVersion(Context context){
        String version = "";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pi .versionName;
        } catch(PackageManager.NameNotFoundException e) {

        }

        return version;
    }

    public static String getLocaleLanguage(Context context){
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }

        return  locale.getLanguage();
    }

    public static void setLocaleLanguage(Context context, String langCode){

        Configuration config = new Configuration();
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Locale.forLanguageTag(langCode);
            config.setLocale(locale);
        } else {
            locale = new Locale(langCode);
            config.locale = locale;
        }

        Locale.setDefault(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
