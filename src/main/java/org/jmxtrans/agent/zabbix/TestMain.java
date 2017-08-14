package org.jmxtrans.agent.zabbix;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyf on 2017/8/12.
 */
public class TestMain {
    public static void main(String[] args) {
        try {
            String jmxURL = "service:jmx:rmi:///jndi/rmi://10.211.253.147:10053/jmxrmi";//tomcat jmx url
            JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);

            Map map = new HashMap();
            //String[] credentials = new String[] { "monitorRole", "QED" };
            //map.put("jmx.remote.credentials", credentials);
            JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();
            //端口最好是动态取得
            ObjectName threadObjName = new ObjectName("java.lang:type=Threading");
            MBeanInfo mbInfo = mbsc.getMBeanInfo(threadObjName);
            System.out.println("123456");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
