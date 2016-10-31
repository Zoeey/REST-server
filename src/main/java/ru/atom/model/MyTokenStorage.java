package ru.atom.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.model.MyUser;
import ru.atom.model.MyToken;
import ru.atom.server.auth.AuthenticationFilter;

import java.util.concurrent.ConcurrentHashMap;

public class MyTokenStorage {
    private static final Logger log = LogManager.getLogger(MyTokenStorage.class);
    private static ConcurrentHashMap<MyToken,MyUser> tokensReversed;
    private static ConcurrentHashMap<Long,MyToken> longtoken;
    private static ConcurrentHashMap<MyUser,MyToken> tokens;

    public ConcurrentHashMap<MyUser,MyToken> getTokens(){
        return tokens;
    }

    public ConcurrentHashMap<MyToken,MyUser> getTokensReversed(){
        return tokensReversed;
    }

    public void insertUser(MyUser user,MyToken token){
        tokensReversed.put(token,user);
        tokens.put(user,token);
        longtoken.put(token.getToken(),token);
    }

    public void deleteUser(MyUser user){
        tokensReversed.remove(tokens.get(user));
        tokens.remove(user);
    }

    public MyUser getUserByToken(MyToken token){
        return tokensReversed.get(token);
    }

    public MyUser getUserByToken(Long token){
        return tokensReversed.get(longtoken.get(token));
    }
    public MyToken getTokenByuser(MyUser user){
        return tokens.get(user);
    }


    public void validateToken(String rawToken) throws Exception {
        MyToken token = new MyToken(Long.parseLong(rawToken));
        if (!longtoken.containsKey(Long.parseLong(rawToken))) {
            throw new Exception("Token validation exception");
        }
        log.info("Correct token from '{}'", tokensReversed.get(token));
    }

    public void deleteToken(String rawToken){
            Long ltoken=Long.parseLong(rawToken);
            tokens.remove(tokensReversed.get(longtoken.get(ltoken)));
            tokensReversed.remove(longtoken.get(ltoken));
            longtoken.remove(ltoken);
    }

    public MyTokenStorage(){
        tokensReversed=new ConcurrentHashMap<>();
        tokens=new ConcurrentHashMap<>();
        longtoken = new ConcurrentHashMap<>();
    };
}
