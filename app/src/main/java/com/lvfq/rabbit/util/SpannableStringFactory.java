package com.lvfq.rabbit.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;

import com.lvfq.rabbit.R;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpannableStringFactory {

    public static Context context;

    private static String expressionPatternStr="\\[(.*?)\\]";
    private static Pattern expressionPattern=Pattern.compile(expressionPatternStr);

    private static String urlPatternStr="(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static Pattern urlPattern=Pattern.compile(urlPatternStr);

    private static String atPatternStr="@([\\u4e00-\\u9fa5a-zA-Z0-9_-]{4,30})";
    private static Pattern atPattern=Pattern.compile(atPatternStr);

    private static String huatiPatternStr="#([^#]+)#";
    private static Pattern huatiPattern=Pattern.compile(huatiPatternStr);

    public static SpannableString createSpannableText(String maintext) {
        SpannableString spannableString=new SpannableString(maintext);
        //找表情
        Matcher expressionMatcher= expressionPattern.matcher(maintext);
        while(expressionMatcher.find()) {
            int id=-1;
            try {
                Field stringFiled=(Field)R.string.class.getDeclaredField(expressionMatcher.group(1));
                int strId=stringFiled.getInt(R.string.class);
                String expressionStr=context.getString(strId);
                Field field=(Field)R.drawable.class.getDeclaredField(expressionStr);
                id=field.getInt(R.drawable.class);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(id!=-1) {
                Drawable expression = ResourcesCompat.getDrawable(context.getResources(), id, null);
                expression.setBounds(0, 0, expression.getIntrinsicWidth(), expression.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(expression, ImageSpan.ALIGN_BASELINE);
                spannableString.setSpan(span, expressionMatcher.start(), expressionMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        //找Url
        Matcher urlMatcher=urlPattern.matcher(maintext);
        while(urlMatcher.find()) {
            spannableString.setSpan(new URLSpan(urlMatcher.group(0)), urlMatcher.start(), urlMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //找at
        Matcher atMatcher=atPattern.matcher(maintext);
        while(atMatcher.find()) {
            spannableString.setSpan(new URLSpan("http://weibo.com/n/"+atMatcher.group(1)+"?from=feed&loc=at"), atMatcher.start(), atMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //找话题
        Matcher huatiMatcher=huatiPattern.matcher(maintext);
        while(huatiMatcher.find()) {
            spannableString.setSpan(new URLSpan("http://huati.weibo.com/k/"+huatiMatcher.group(1)), huatiMatcher.start(), huatiMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }
}