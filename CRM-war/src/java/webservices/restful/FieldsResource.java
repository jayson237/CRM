/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package webservices.restful;

import java.util.Set;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import session.CustomerSessionLocal;

/**
 *
 * @author jayso
 */
@Path("fields")
public class FieldsResource {

    @EJB
    private CustomerSessionLocal customerSessionLocal;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFields() {
        Set<String> fields = customerSessionLocal.getAllFieldNames();
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (String f : fields) {
            builder.add(f);
        }

        JsonArray result = builder.build();
        return Response.status(200).entity(
                result
        ).build();
    } //end getAllFields

}
