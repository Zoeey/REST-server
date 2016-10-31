package ru.atom.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.server.auth.AuthenticationFilter;

/**
 * Created by Макс on 24.10.2016.
 */
public class MyUser {
    private static final Logger log = LogManager.getLogger(MyUser.class);
    private String name;
    private String password;

    public void  setName(String s){
        name=s;
    }
    public void setPassword(String s){
        password=s;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }

    public boolean isCorrectAuth(String nname, String npass){
        //log.info(nname+" " +npass);
        //log.info((nname.equals(name)) +" " +( npass.equals(password)));
        return( nname.equals(name) && npass.equals(password));
    }

    public void MyUser(){};
}
