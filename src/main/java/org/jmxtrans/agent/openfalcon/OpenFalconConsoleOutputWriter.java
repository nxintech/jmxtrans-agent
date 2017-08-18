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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    @Override
    public void postConstruct(@Nonnull Map<String, String> settings) {
        this.metricPathPrefix = StringUtils2.trimToEmpty(settings.get("namePrefix"));
        try {
            endPoint= InetAddress.getLocalHost().getHostName();
            String aggregateXml = ConfigurationUtils.getString(settings, "aggregates");
            initAggregate(aggregateXml);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.info("OpenFalconConsoleOutputWriter postConstruct invoked");
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
                System.out.println("aggregate is "+ i +" : " +resultAlias+";type="+type+";"+className);
                NodeList aggregateQueryResultList = aggregateElement.getElementsByTagName("queryResultKey");
                for (int j = 0; j < aggregateQueryResultList.getLength(); j++) {
                    //System.out.println("next is "+ag);
                    Element aggregateQuery = (Element) aggregateQueryResultList.item(j);
                    String queryResultKey=aggregateQuery.getTextContent();
                    resultKeyList.add(queryResultKey);
                    System.out.println("find  query result key is "+queryResultKey);
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
        //System.out.println(metricPathPrefix + invocationName + " " + value + " " + TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }

    @Override
    public void postCollect() throws IOException {
        logger.info("postCellect doing");
        for(Aggregate aggregate : aggregateList){
            System.out.println("aggregate value is "+aggregate);
            AggregateMethod <Double> aggregateMethod=aggregate.getAggregateMethod();
            List<Double> valueList=new ArrayList<>();
            for(String resultKey : aggregate.getResultKeyList()){
                Object value =queryValueMap.get(resultKey);
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
        logger.info(JSON.toJSONString(openFalconOutputObjectList));
        openFalconOutputObjectList.clear();
    }
}
