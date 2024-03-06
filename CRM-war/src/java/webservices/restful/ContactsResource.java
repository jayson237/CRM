/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package webservices.restful;

import error.NoResultException;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import session.CustomerSessionLocal;

/**
 * REST Web Service
 *
 * @author jayso
 */
@Path("contacts")
public class ContactsResource {

    @EJB
    private CustomerSessionLocal customerSessionLocal;

    @DELETE
    @Path("/{contact_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteContact(@PathParam("contact_id") Long cId) {
        try {
            customerSessionLocal.deleteContact(cId);
            return Response.status(204).build();
        } catch (NoResultException e) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Customer or field not found")
                    .build();
            return Response.status(404).entity(exception).build();
        }
    }

}
