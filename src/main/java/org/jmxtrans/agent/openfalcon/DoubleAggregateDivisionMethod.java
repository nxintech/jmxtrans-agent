package org.jmxtrans.agent.openfalcon;

import java.util.List;

/**
 * Created by lyf on 2017/8/15.
 */
public class DoubleAggregateDivisionMethod implements AggregateMethod <Double> {

    @Override
    public Double execute(List<Double> tlist) {
        double sum=0.0;
        int index=0;
        for (Double value : tlist){
            if(index==0){
                sum=value;
            }else{
                sum=sum/value;
            }
            index ++;
        }
        return sum;
    }
}
