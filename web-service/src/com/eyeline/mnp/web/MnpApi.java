package com.eyeline.mnp.web;

import com.eyeline.mnp.Mno;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;

/**
 * @author Chukanov
 */
@Path("mnp")
public class MnpApi {
    private static Logger log = Logger.getLogger(MnpApi.class);
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String lookup(@QueryParam("subscriber") String subscriber) {
        try {
            Mno mno = RestServer.getStorage().lookup(subscriber);
            log.info("number: "+subscriber+" is mno: "+mno);
            return "{\"id\":\""+escapeQuotas(mno.getId())+"\"," +
                    "\"country\":\""+escapeQuotas(mno.getCountry())+"\"," +
                    "\"title\":\""+escapeQuotas(mno.getTitle())+"\"," +
                    "\"area\":\""+escapeQuotas(mno.getArea())+"\"" +
                    "}";
        } catch (Exception e) {
            log.warn("",e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private static String escapeQuotas(String s) {
        if (s == null) return "";
        else return s.replaceAll("\"","\\\\\"");
    }
}
