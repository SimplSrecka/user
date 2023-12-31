package si.fri.rso.simplsrecka.user.api.v1;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "User API", version = "v1",
        contact = @Contact(email = "ak1193@student.uni-lj.si"),
        license = @License(name = "dev"), description = "API for managing image metadata."),
        //servers = @Server(url = "http://localhost:8080/"))
        servers = @Server(url = "http://52.226.192.46/user"))
@ApplicationPath("/v1")
public class UserApplication extends Application {

}
