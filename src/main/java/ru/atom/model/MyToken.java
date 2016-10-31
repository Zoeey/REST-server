package ru.atom.model;

import com.sun.org.apache.xpath.internal.operations.Equals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.server.auth.AuthenticationFilter;

public class MyToken {
    private static final Logger log = LogManager.getLogger(MyToken.class);
    private Long token;

    public Long getToken(){
        return token;
    }

    public void setToken(Long t){
        token=t;
    }

    public MyToken(Long a){
        token=a;
    }
    public boolean Equals(MyToken t){
        return token==t.getToken();
    }

    public MyToken(){};
}
