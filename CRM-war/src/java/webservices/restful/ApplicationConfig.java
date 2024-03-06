package webservices.restful;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author jayso
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(webservices.restful.ContactsResource.class);
        resources.add(webservices.restful.CustomersResource.class);
        resources.add(webservices.restful.FieldsResource.class);
    }
}
