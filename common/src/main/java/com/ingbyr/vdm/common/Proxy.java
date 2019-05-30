package com.ingbyr.vdm.common;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Proxy {
    public enum ProxyType {
        SOCKS5,
        HTTP
    }

    public ProxyType type;
    public String ip;
    public String port;
}
