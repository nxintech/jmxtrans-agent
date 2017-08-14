package org.jmxtrans.agent.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyf on 2017/8/6.
 */
public class OpenFalconGroupMessage {
    private boolean isFinished;
    private long runIntervalMillis;
    private List<OpenFalconOutputObject> openFalconOutputObjectList;

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public long getRunIntervalMillis() {
        return runIntervalMillis;
    }

    public void setRunIntervalMillis(long runIntervalMillis) {
        this.runIntervalMillis = runIntervalMillis;
    }

    public List<OpenFalconOutputObject> getOpenFalconOutputObjectList() {
        return openFalconOutputObjectList;
    }

    public void setOpenFalconOutputObjectList(List<OpenFalconOutputObject> openFalconOutputObjectList) {
        this.openFalconOutputObjectList = openFalconOutputObjectList;
    }
    public void addOpenFalconOutObject(OpenFalconOutputObject openFalconOutputObject){
        if(null==this.getOpenFalconOutputObjectList())
            openFalconOutputObjectList=new ArrayList<>();
        openFalconOutputObjectList.add(openFalconOutputObject);
    }

    @Override
    public String toString() {
        return "OpenFalconGroupMessage{" +
                "isFinished=" + isFinished +
                ", runIntervalMillis=" + runIntervalMillis +
                ", openFalconOutputObjectList=" + openFalconOutputObjectList +
                '}';
    }
}
