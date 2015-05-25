package com.lvfq.rabbit.data;

import android.graphics.Bitmap;

import java.util.List;

public class RabbitDataItem {
    public int type=0;
    public Bitmap thumbnail=null;
    public String title=null;
    public String maintext=null;
    public String timetext=null;
    public List<Bitmap> extra=null;

    public java.sql.Date time=null;
}