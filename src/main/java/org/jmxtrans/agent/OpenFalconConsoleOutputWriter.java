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
package org.jmxtrans.agent;

import com.alibaba.fastjson.JSON;
import org.jmxtrans.agent.util.OpenFalconGroupMessage;
import org.jmxtrans.agent.util.OpenFalconGroupThread;
import org.jmxtrans.agent.util.OpenFalconOutputObject;
import org.jmxtrans.agent.util.StringUtils2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class OpenFalconConsoleOutputWriter extends AbstractOutputWriter implements OutputWriter {

    private String metricPathPrefix;


    @Override
    public void postConstruct(@Nonnull Map<String, String> settings) {
        this.metricPathPrefix = StringUtils2.trimToEmpty(settings.get("namePrefix"));
        logger.info("OpenFalconConsoleOutputWriter postConstruct invoked");
    }

    @Override
    public void writeQueryResult(@Nonnull String name, @Nullable String type, @Nullable Object value) {
        try{
            OpenFalconGroupMessage message= OpenFalconGroupThread.getCurrentThreadGroupMessage();
            //logger.info("get message is "+message);
            List<OpenFalconOutputObject> openFalconOutputObjectList=message.getOpenFalconOutputObjectList();
            //logger.info("get openFalconOutputObjectList is "+openFalconOutputObjectList);
            String json= JSON.toJSONString(openFalconOutputObjectList);
            logger.info(String.format("OpenFalconConsole write json is %s",json));
        }catch (Exception e){
            e.printStackTrace();
        }

        //System.out.println(metricPathPrefix + name + " " + value + " " + TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
        //System.out.println("get result name is  "+name+" , type is  "+type + " ; value is  "+value+" ;  TimeUnit is "+TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
        //System.out.println(metricPathPrefix + name + " " + value + " " + TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }

    @Override
    public void writeInvocationResult(@Nonnull String invocationName, @Nullable Object value) throws IOException {
        System.out.println("writeInvocationResult  invocationName is  "+invocationName+" ,  value is  "+value+" ;  TimeUnit is "+TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
        //System.out.println(metricPathPrefix + invocationName + " " + value + " " + TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }
}
