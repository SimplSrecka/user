package si.fri.rso.simplsrecka.user.api.v1.resources;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
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
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.fri.rso.simplsrecka.user.lib.User;
import si.fri.rso.simplsrecka.user.services.beans.UserBean;
import si.fri.rso.simplsrecka.user.services.externalAPI.EmailValidatorService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "APIs for user operations")
@CrossOrigin(allowOrigin = "http://localhost:4200")
public class UserResource {

    private Logger log = Logger.getLogger(UserResource.class.getName());

    @Inject
    private UserBean userBean;

    @Inject
    private EmailValidatorService emailValidatorService;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all users.", summary = "Get all users")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of users",
                    content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of users in list")}
            ),
            @APIResponse(responseCode = "500",
                    description = "Internal Server Error")
    })
    @GET
    public Response getUsers() {
        try {
            List<User> users = userBean.getUsersFilter(uriInfo);
            return Response.status(Response.Status.OK).entity(users).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error fetching users", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Get details for a specific user.", summary = "Get user details")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "User details",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @APIResponse(responseCode = "404",
                    description = "User not found"),
            @APIResponse(responseCode = "500",
                    description = "Internal Server Error")
    })
    @GET
    @Path("/{userId}")
    public Response getUser(@Parameter(description = "User ID.", required = true)
                            @PathParam("userId") Integer userId) {
        try {
            User user = userBean.getUser(userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.OK).entity(user).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error fetching user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "User login.", summary = "User login")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "User successfully logged in",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @APIResponse(responseCode = "401",
                    description = "Unauthorized"),
            @APIResponse(responseCode = "400",
                    description = "Bad Request"),
            @APIResponse(responseCode = "500",
                    description = "Internal Server Error")
    })
    @POST
    @Path("/login")
    public Response login(@RequestBody(description = "DTO object with user credentials.",
            required = true,
            content = @Content(schema = @Schema(implementation = User.class)))
                          User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            Integer authenticatedUser = userBean.login(user.getUsername(), user.getPassword());
            return Response.status(Response.Status.OK).entity(authenticatedUser).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error during login", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Add a new user.", summary = "Create user")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "User successfully created"),
            @APIResponse(responseCode = "400",
                    description = "Invalid user data"),
            @APIResponse(responseCode = "500",
                    description = "Internal Server Error")
    })
    @POST
    @Path("/registration")
    public Response createUser(@RequestBody(description = "DTO object with user details.",
            required = true,
            content = @Content(schema = @Schema(implementation = User.class)))
                                   User user) {
        if (user == null ||
                user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getName() == null || user.getName().trim().isEmpty() ||
                user.getSurname() == null || user.getSurname().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing or empty required fields").build();
        }
        if (!emailValidatorService.isValidEmail(user.getEmail())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid email address").build();
        }
        try {
            user = userBean.createUser(user);
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error creating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Update details for a specific user.", summary = "Update user")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "User successfully updated"),
            @APIResponse(responseCode = "404",
                    description = "User not found"),
            @APIResponse(responseCode = "400",
                    description = "Invalid user data")
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
        try {
            User updatedUser = userBean.updateUser(userId, user);
            if (updatedUser == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.OK).entity(updatedUser).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error updating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Delete a specific user.", summary = "Delete user")
    @APIResponses({
            @APIResponse(responseCode = "204",
                    description = "User successfully deleted"),
            @APIResponse(responseCode = "404",
                    description = "User not found"),
            @APIResponse(responseCode = "500",
                    description = "Internal Server Error")
    })
    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@Parameter(description = "User ID.", required = true)
                               @PathParam("userId") Integer userId) {
        try {
            boolean deleted = userBean.deleteUser(userId);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error deleting user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}