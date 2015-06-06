package com.lvfq.rabbit.data;

import android.text.SpannableString;

import java.io.Serializable;
import java.util.List;

public class RabbitDataItem implements Serializable{
    public int type=0;
    public long id=0;
    public String thumbnail=null;
    public String title=null;
    public String maintext=null;
    public String timetext=null;
    public List<String> extra=null;

    //转发微博
    public long retId=0;
    public String retTitle=null;//给danceItem复用ID
    public String retMaintext=null;
    public List<String> retExtra=null;

    public double duration=0;
}