package org.vhosts.plugin.domain;

import java.io.Serializable;
import java.util.List;

public class DNS implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> dns4;
    private List<String> dns6;

    public List<String> getDns4() {
        return dns4;
    }

    public void setDns4(List<String> dns4) {
        this.dns4 = dns4;
    }

    public List<String> getDns6() {
        return dns6;
    }

    public void setDns6(List<String> dns6) {
        this.dns6 = dns6;
    }
}
