package com.lvfq.rabbit.data;

import android.graphics.Bitmap;
import android.text.SpannableString;

import java.util.List;

public class RabbitDataItem {
    public int type=0;
    public String thumbnail=null;
    public String title=null;
    public String maintext=null;
    public SpannableString spannableMaintext=null;
    public String timetext=null;
    public List<String> extra=null;

    //转发微博
    public String retTitle=null;
    public String retMaintext=null;
    public SpannableString retSpannableMaintext=null;
    public List<String> retExtra=null;
}