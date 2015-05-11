package com.lvfq.rabbit.data;

import java.util.Comparator;

public class ComparatorOfRabbitDataItem implements Comparator<RabbitDataItem> {
    @Override
    public int compare(RabbitDataItem arg0, RabbitDataItem arg1) {
        // TODO Auto-generated method stub
        if(arg0.time.getTime()>arg1.time.getTime())
            return -1;
        else if(arg0.time.getTime()>arg1.time.getTime())
            return 1;
        return 0;
    }
}