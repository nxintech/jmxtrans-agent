package org.jmxtrans.agent;

import org.jmxtrans.agent.util.io.ClasspathResource;
import org.jmxtrans.agent.util.io.Resource;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class OpenFalconConsoleOutputWriterTest {
    @Test
    public void testWriteQueryResult() throws UnsupportedEncodingException {
        Resource resource = new ClasspathResource("classpath:jmxtrans-config-openfalconconsole-test.xml");
        JmxTransConfigurationLoader loader = new JmxTransConfigurationXmlLoader(resource);
        JmxTransExporter exporter = new JmxTransExporter(loader);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            exporter.collectAndExport();
            try {
                Thread.sleep(2005);
            } catch (InterruptedException e) {
                // IGNORE
            }
            exporter.collectAndExport();
        } finally {
            //System.setOut(prev);
        }
        String[] splitted = baos.toString().split(System.lineSeparator());
        //assertThat(splitted, arrayContaining(startsWith("jvm.thread "), startsWith("jvm.thread ")));
    }
}
