package ru.atom.server.auth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.model.MyToken;
import ru.atom.model.MyTokenStorage;
import ru.atom.model.MyUser;

/**
 * Created by Max on 30.10.2016.
 */
@Path("/auth")
public class ATH {
    private static  final Logger log= LogManager.getLogger(ATH.class);
    private static MyTokenStorage tokenStorage= new MyTokenStorage();
    private static ConcurrentHashMap<String,MyUser> nameOf= new ConcurrentHashMap<>();

    @POST
    @Path("register")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response register(@FormParam("user") String user,
                             @FormParam("password") String password) {

        if (user == null || password == null) {
            log.info("Ошибка регистрации, не заданы параметры");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (nameOf.containsKey(user)) {
            log.info("Ошибка регистрации: имя занято");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        else{
            MyUser uuser=new MyUser();
            uuser.setName(user);
            uuser.setPassword(password);
            nameOf.put(user,uuser);
        }

        log.info("New user '{}' registered", user);
        return Response.ok("User " + user + " registered.").build();
    }

    @POST
    @Path("login")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response authenticateUser(@FormParam("user") String user,
                                     @FormParam("password") String password) {

        if (user == null || password == null) {
            log.info("Ошибка логина, не заданы параметры");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            // Authenticate the user using the credentials provided
            if (!nameOf.get(user).isCorrectAuth(user,password)) {
                log.info("неправильная пара юзер пассворд");
                log.info("###"+(!nameOf.get(user).isCorrectAuth(user,password)));
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            // Issue a token for the user
            MyToken token = issueToken(nameOf.get(user));
            log.info("User '{}' logged in", user);

            // Return the token on the response
            return Response.ok(Long.toString(token.getToken())).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private MyToken issueToken(MyUser user) {
        MyToken token = tokenStorage.getTokenByuser(user);
        if (token != null) {
            return token;
        }
        token=new MyToken();
        Long ltoken = ThreadLocalRandom.current().nextLong();
        token.setToken(ltoken);
        tokenStorage.insertUser(user,token);
        return token;
    }

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    @Path("logout")
    public Response logout(ContainerRequestContext requestContext)  {
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("no token");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            // Validate the token
            tokenStorage.validateToken(token);
        } catch (Exception e) {
            log.info("не валидный токен");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        log.info("User " + tokenStorage.getUserByToken(Long.parseLong(token)).getName()+" had logout");
        tokenStorage.deleteToken(token);
        return Response.ok("Succesfully logout").build();
    }

    public static ConcurrentHashMap<String,MyUser> getNameOf(){
        return nameOf;
    }

    public static MyTokenStorage getTokenStorage(){
        return tokenStorage;
    }
}
