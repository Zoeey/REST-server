package ru.atom.server.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.model.MyToken;
import ru.atom.model.MyTokenStorage;
import ru.atom.model.MyUser;
import ru.atom.server.auth.ATH;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Max on 30.10.2016.
 */
@Path("/profile")
public class Profile {

    private static final Logger log = LogManager.getLogger(ATH.class);

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    @Path("name")
    public Response cName(ContainerRequestContext requestContext, @FormParam("name") String name) {

         MyTokenStorage tokenStorage = ATH.getTokenStorage();
         ConcurrentHashMap<String,MyUser> nameOf=ATH.getNameOf();

        if (name == null) {
            log.info("имя где?");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("no token");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String rawToken = authorizationHeader.substring("Bearer".length()).trim();

        try {
            // Validate the token
            tokenStorage.validateToken(rawToken);
        } catch (Exception e) {
            log.info("не валидный токен");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Long ltoken = Long.parseLong(rawToken);
        if (nameOf.get(name) != null) {
            log.info("Ошибка изменения имени: имя занято или такое же");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        MyUser user = tokenStorage.getUserByToken(ltoken);
        MyToken token = tokenStorage.getTokenByuser(user);
        nameOf.remove(user.getName());
        nameOf.put(name, user);
        user.setName(name);
        tokenStorage.deleteUser(user);
        tokenStorage.insertUser(user, token);
        log.info(user.getName() + ", поздравляем с ноым именем");
        return Response.ok("ok").build();
    }
}
