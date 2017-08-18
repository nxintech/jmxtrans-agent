package org.jmxtrans.agent.openfalcon;

import java.util.List;

/**
 * Created by lyf on 2017/8/15.
 */
public class Aggregate {
    private String resultAlias;
    private String type;
    private AggregateMethod aggregateMethod;
    private List<String> resultKeyList;
    public Aggregate() {
    }

    public String getResultAlias() {
        return resultAlias;
    }

    public void setResultAlias(String resultAlias) {
        this.resultAlias = resultAlias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AggregateMethod getAggregateMethod() {
        return aggregateMethod;
    }

    public void setAggregateMethod(AggregateMethod aggregateMethod) {
        this.aggregateMethod = aggregateMethod;
    }

    public List<String> getResultKeyList() {
        return resultKeyList;
    }

    public void setResultKeyList(List<String> resultKeyList) {
        this.resultKeyList = resultKeyList;
    }

    @Override
    public String toString() {
        return "Aggregate{" +
                "resultAlias='" + resultAlias + '\'' +
                ", type='" + type + '\'' +
                ", aggregateMethod=" + aggregateMethod +
                ", resultKeyList=" + resultKeyList +
                '}';
    }
}
