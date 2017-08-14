package org.jmxtrans.agent;

import org.jmxtrans.agent.util.io.ClasspathResource;
import org.jmxtrans.agent.util.io.Resource;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ConsoleOutputWriterTest {
    @Test
    public void testWriteQueryResult() throws UnsupportedEncodingException {
        Resource resource = new ClasspathResource("classpath:jmxtrans-config-console-test.xml");
        JmxTransConfigurationLoader loader = new JmxTransConfigurationXmlLoader(resource);
        JmxTransExporter exporter = new JmxTransExporter(loader);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream prev = System.out;
        System.setOut(ps);
        try {
            exporter.collectAndExport();
            try {
                Thread.sleep(2005);
            } catch (InterruptedException e) {
                // IGNORE
            }
            exporter.collectAndExport();
        } finally {
            System.setOut(prev);
        }
        String[] splitted = baos.toString().split(System.lineSeparator());
        for (String string : splitted){
            System.out.println(string);
        }

        //assertThat(splitted, arrayContaining(startsWith("jvm.thread "), startsWith("jvm.thread ")));
    }
}
