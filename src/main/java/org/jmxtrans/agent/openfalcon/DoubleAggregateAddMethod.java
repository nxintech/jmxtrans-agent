package org.jmxtrans.agent.openfalcon;

import java.util.List;

/**
 * Created by lyf on 2017/8/15.
 */
public class DoubleAggregateAddMethod implements AggregateMethod <Double> {

    @Override
    public Double execute(List<Double> tlist) {
        double sum=0.0;
        for (Double value : tlist){
            sum=sum+value;
        }

        return sum;
    }
}
