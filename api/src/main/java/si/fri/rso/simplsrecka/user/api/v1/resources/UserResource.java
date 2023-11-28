package si.fri.rso.simplsrecka.user.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.simplsrecka.user.lib.User;
import si.fri.rso.simplsrecka.user.services.beans.UserBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private Logger log = Logger.getLogger(UserResource.class.getName());

    @Inject
    private UserBean userBean;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all users.", summary = "Get all users")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of users",
                    content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of users in list")}
            )
    })
    @GET
    public Response getUsers() {
        List<User> users = userBean.getUsersFilter(uriInfo);
        return Response.status(Response.Status.OK).entity(users).build();
    }

    @Operation(description = "Get details for a specific user.", summary = "Get user details")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "User details",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    @GET
    @Path("/{userId}")
    public Response getUser(@Parameter(description = "User ID.", required = true)
                            @PathParam("userId") Integer userId) {
        User user = userBean.getUser(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @Operation(description = "Add a new user.", summary = "Create user")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "User successfully created"),
            @APIResponse(responseCode = "400", description = "Invalid user data")
    })
    @POST
    public Response createUser(@RequestBody(description = "DTO object with user details.",
            required = true,
            content = @Content(schema = @Schema(implementation = User.class)))
                                   User user) {
        if (user == null || user.getUsername() == null || user.getEmail() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        user = userBean.createUser(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @Operation(description = "Update details for a specific user.", summary = "Update user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "User successfully updated"),
            @APIResponse(responseCode = "404", description = "User not found"),
            @APIResponse(responseCode = "400", description = "Invalid user data")
    })
    @PUT
    @Path("/{userId}")
    public Response updateUser(@Parameter(description = "User ID.", required = true)
                               @PathParam("userId") Integer userId,
                               @RequestBody(description = "DTO object with updated user details.",
                                       required = true,
                                       content = @Content(schema = @Schema(implementation = User.class)))
                               User user) {
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        User updatedUser = userBean.updateUser(userId, user);
        if (updatedUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(updatedUser).build();
    }

    @Operation(description = "Delete a specific user.", summary = "Delete user")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "User successfully deleted"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@Parameter(description = "User ID.", required = true)
                               @PathParam("userId") Integer userId) {
        boolean deleted = userBean.deleteUser(userId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
