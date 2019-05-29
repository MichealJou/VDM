package com.ingbyr.vdm.common;

public class Proxy {
    public enum ProxyType {
        SOCKS5,
        HTTP;
    }

    public ProxyType type;
    public String ip;
    public String port;
}
