package org.jmxtrans.agent.util;

/**
 * Created by lyf on 2017/8/6.
 */
public class OpenFalconGroupThread {
    private static final ThreadLocal <OpenFalconGroupMessage>threadMessage = new ThreadLocal<OpenFalconGroupMessage>() {
        protected OpenFalconGroupMessage initialValue() {
            OpenFalconGroupMessage openFalconGroupMessage=new OpenFalconGroupMessage();
            return openFalconGroupMessage;
        }
    };

    public static OpenFalconGroupMessage getCurrentThreadGroupMessage() {
        return  threadMessage.get();
    }

    public static void setCurrentThreadGroupMessage(OpenFalconGroupMessage openFalconGroupMessage) {
        threadMessage.set(openFalconGroupMessage);
    }
}
