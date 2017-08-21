/*
 * Copyright (c) 2010-2013 the original author or authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.jmxtrans.agent.openfalcon;

import com.alibaba.fastjson.JSON;
import org.jmxtrans.agent.AbstractOutputWriter;
import org.jmxtrans.agent.JmxTransExporter;
import org.jmxtrans.agent.OutputWriter;
import org.jmxtrans.agent.util.ConfigurationUtils;
import org.jmxtrans.agent.util.OpenFalconOutputObject;
import org.jmxtrans.agent.util.StringUtils2;
import org.jmxtrans.agent.util.io.IoRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class OpenFalconConsoleOutputWriter extends AbstractOutputWriter implements OutputWriter {

    private String metricPathPrefix;
    private List<OpenFalconOutputObject> openFalconOutputObjectList = new ArrayList<>();
    private String endPoint="#unknown";
    private List<Aggregate> aggregateList=new ArrayList<>();
    private Map<String,Object> queryValueMap=new HashMap<>();
    private int connectTimeoutMillis=10000;//单位毫秒
    private int readTimeoutMillis=2000;//读取超时 单位毫秒
    private URL url;
    private final static String aggregateKey="aggregates";
    @Override
    public void postConstruct(@Nonnull Map<String, String> settings) {
        this.metricPathPrefix = StringUtils2.trimToEmpty(settings.get("namePrefix"));
        try {
            endPoint= InetAddress.getLocalHost().getHostName();
            if(settings.containsKey(aggregateKey)){
                String aggregateXml = ConfigurationUtils.getString(settings, aggregateKey);
                initAggregate(aggregateXml);
            }
            url=new URL(ConfigurationUtils.getString(settings, "url", null));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        logger.info("OpenFalconHttpWriter postConstruct invoked");
    }

    private void initAggregate(String aggregateXml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        InputSource source = null;
        StringReader reader = null;
        try {
            builder = factory.newDocumentBuilder();
            reader = new StringReader(aggregateXml);
            source = new InputSource(reader);//使用字符流创建新的输入源
            doc = builder.parse(source);
            NodeList nodeList = doc.getElementsByTagName("aggregate");
            for (int i = 0; i <nodeList.getLength(); i++) {
                Aggregate aggregate=new Aggregate();
                List<String> resultKeyList=new ArrayList<>();
                Element aggregateElement = (Element) nodeList.item(i);
                String resultAlias = aggregateElement.getAttribute("resultAlias");
                String type = aggregateElement.getAttribute("type");
                String className = aggregateElement.getAttribute("class");
                NodeList aggregateQueryResultList = aggregateElement.getElementsByTagName("queryResultKey");
                for (int j = 0; j < aggregateQueryResultList.getLength(); j++) {
                    Element aggregateQuery = (Element) aggregateQueryResultList.item(j);
                    String queryResultKey=aggregateQuery.getTextContent();
                    resultKeyList.add(queryResultKey);
                }
                aggregate.setResultAlias(resultAlias);
                aggregate.setType(type);
                aggregate.setAggregateMethod((AggregateMethod) Class.forName(className).newInstance());
                aggregate.setResultKeyList(resultKeyList);
                aggregateList.add(aggregate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("initAggregate error "+e.getMessage());
        } finally {
            if(reader != null){
                reader.close();
            }
        }

    }

    @Override
    public void writeQueryResult(@Nonnull String name, @Nullable String type, @Nullable Object value) {
        try{
            OpenFalconOutputObject openFalconOutputObject=OpenFalconOutputObject.createNewOpenFalconOutObject(null,type,value,name, JmxTransExporter.staticRunIntervalMillis,endPoint);
            openFalconOutputObjectList.add(openFalconOutputObject);
            queryValueMap.put(name,value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void writeInvocationResult(@Nonnull String invocationName, @Nullable Object value) throws IOException {
        System.out.println("writeInvocationResult  invocationName is  "+invocationName+" ,  value is  "+value+" ;  TimeUnit is "+TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }

    @Override
    public void postCollect() throws IOException {
        logger.info("postCellect doing");
        for(Aggregate aggregate : aggregateList){
            AggregateMethod <Double> aggregateMethod=aggregate.getAggregateMethod();
            List<Double> valueList=new ArrayList<>();
            for(String resultKey : aggregate.getResultKeyList()){
                Object value =queryValueMap.get(resultKey);
                if(null==value || value.toString().isEmpty())
                    continue;
                if(value instanceof Integer){
                    valueList.add(new Double(value.toString()));
                }else if(value instanceof Long){
                    valueList.add(((Long)value).doubleValue());
                }

            }
            Double value =aggregateMethod.execute(valueList);
            OpenFalconOutputObject openFalconOutputObject=OpenFalconOutputObject.createNewOpenFalconOutObject(null,aggregate.getType(),value,aggregate.getResultAlias(),JmxTransExporter.staticRunIntervalMillis,endPoint);
            openFalconOutputObjectList.add(openFalconOutputObject);
        }
        String json=JSON.toJSONString(openFalconOutputObjectList);
        logger.info(String.format("send json body %s  to server url %s ",json,url));
        openFalconOutputObjectList.clear();

    }

    private HttpURLConnection getHttpConnection() {
        try {
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeoutMillis);
            conn.setReadTimeout(readTimeoutMillis);
            conn.setRequestMethod("POST");// 提交模式
            conn.setDoOutput(true);// 是否输入参数
            return conn;
        } catch (IOException | ClassCastException e) {
            throw new IoRuntimeException("Failed to create HttpURLConnection to " + url + " - is it a valid HTTP url?",
                    e);
        }
    }

    private void sendHttpRequest(HttpURLConnection conn, String body)
            throws UnsupportedEncodingException, IOException {
        byte[] toSendBytes = body.getBytes("UTF-8");
        OutputStream outputStream =conn.getOutputStream();
        //conn.setRequestProperty("Content-Length", Integer.toString(toSendBytes.length));
        try {
            outputStream.write(toSendBytes);// 输入参数
            outputStream.flush();
            InputStream inStream=conn.getInputStream();
            int reponseCode=conn.getResponseCode();
            if(200!=reponseCode)
                logger.warning("sendHttpRequest error,responseCode is  " + reponseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
