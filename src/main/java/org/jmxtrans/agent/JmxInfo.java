package org.jmxtrans.agent;

import org.jmxtrans.agent.util.StringUtils2;

/**
 * Jmx remote config info
 * Created by lyf on 2017/8/14.
 */
public class JmxInfo {
    private String host;
    private String port;
    private String username;
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public JmxInfo(String host, String port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public boolean hasCredentialInfo() {
        return (!StringUtils2.isNullOrEmpty(username) && !StringUtils2.isNullOrEmpty(password));
    }
}
