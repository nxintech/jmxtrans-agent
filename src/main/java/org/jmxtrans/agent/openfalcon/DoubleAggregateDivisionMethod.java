package org.jmxtrans.agent.openfalcon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by lyf on 2017/8/15.
 */
public class DoubleAggregateDivisionMethod implements AggregateMethod <Double> {

    @Override
    public Double execute(List<Double> tlist) {
        BigDecimal sum=new BigDecimal(0.0);
        int index=0;
        for (Double value : tlist){
            BigDecimal decimalValue=new BigDecimal(value);
            if(index==0){
                sum=decimalValue;
            }else{
                sum=divide(sum,decimalValue,2);
            }
            index ++;
        }

        return sum.multiply(new BigDecimal(100)).doubleValue();
    }
    /**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, Integer scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = dividend;
        BigDecimal b2 = divisor;
        return b1.divide(b2, scale, RoundingMode.HALF_UP);
    }
}
