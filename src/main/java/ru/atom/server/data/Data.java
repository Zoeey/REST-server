package ru.atom.server.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.server.auth.ATH;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Created by Max on 30.10.2016.
 */

@Path("/data")
public class Data {
    private static final Logger log= LogManager.getLogger(Data.class);

    @GET
    @Path("users")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public Response getUsers() {
        log.info("users requested");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            ArrayList<String> json = new ArrayList<>();
            for (String i : ATH.getNameOf().keySet()) {
                json.add(i);
            }
            String S = "{\"users\" : ";
            S += mapper.writeValueAsString(json) + "}";
            log.info("отдан лист зарегистрированных юзеров");
            return Response.ok(S).build();
        } catch (Exception e) {
            log.error("error");
        }
        return Response.serverError().build();
    }
}
