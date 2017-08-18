package org.jmxtrans.agent.zabbix;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyf on 2017/8/12.
 */
public class TestMain {
    public static void main(String[] args) {
        try {
            String jmxURL = "service:jmx:rmi:///jndi/rmi://10.211.253.56:10053/jmxrmi";//tomcat jmx url
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
            TestMain testMain=new TestMain();
            testMain.sendEms();
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    /**
     * 通过HttpURLConnection模拟post表单提交
     * @throws Exception
     */
    public void sendEms() throws Exception {
        String wen = "MS2201828";
        String btnSearch = "EMS快递查询";
        URL url = new URL("http://pm.t.nxin.com/login.jsp");
        for (int i = 0; i < 10; i++) {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            conn.setDoOutput(true);// 是否输入参数

            StringBuffer params = new StringBuffer();
            // 表单参数与get形式一样
            params.append("wen").append("=").append(wen).append("&")
                    .append("btnSearch").append("=").append(btnSearch);
            byte[] bypes = params.toString().getBytes();
            conn.getOutputStream().write(bypes);// 输入参数
            InputStream inStream=conn.getInputStream();
        }

        //System.out.println();

    }
}
