package org.jmxtrans.agent.util;

import java.util.concurrent.TimeUnit;

/**
 * Created by lyf on 2017/8/6.
 */
public class OpenFalconOutputObject {
    private String counterType;
    private String endpoint;
    private long timestamp;
    private long step;
    private String tags;
    private String metric;
    private Object value;

    public String getCounterType() {
        return counterType;
    }

    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static OpenFalconOutputObject createNewOpenFalconOutObject(String objectName, String type, Object value, String resultAlias,long step) {
        OpenFalconOutputObject openFalconOutputObject=new OpenFalconOutputObject();
        openFalconOutputObject.setTags(objectName);
        openFalconOutputObject.setCounterType(type);
        openFalconOutputObject.setStep(step);
        openFalconOutputObject.setValue(value);
        openFalconOutputObject.setMetric(resultAlias);
        openFalconOutputObject.setTimestamp(TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
        return openFalconOutputObject;
    }
}
