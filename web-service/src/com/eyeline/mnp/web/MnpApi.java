package com.eyeline.mnp.web;

import com.eyeline.mnp.Mno;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Chukanov
 */
@Path("mnp")
public class MnpApi {
    private static Logger log = Logger.getLogger(MnpApi.class);
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Mno lookup(@QueryParam("subscriber") String subscriber) {
        try {
            Mno mno = RestServer.getStorage().lookup(subscriber);
            log.info("number: "+subscriber+" is mno: "+mno);
            return mno;
        } catch (Exception e) {
            log.warn("",e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
