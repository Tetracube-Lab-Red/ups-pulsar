package red.tetracube;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import red.tetracube.upspulsar.dto.UPSDevice;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;
import red.tetracube.upspulsar.services.UPSDevicesServices;

@Path("/device")
public class UPSDevicesResource {

    @Inject
    UPSDevicesServices upsDevicesServices;

    @Path("/provisioning")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void deviceProvisioning(@Valid UPSDevice request) {
        var upsCreationResult = upsDevicesServices.createDevice(request);
        if (upsCreationResult.isSuccess()) {
            return;
        }

        if (upsCreationResult.getException() instanceof UPSPulsarException.EntityExistsException) {
            throw new ClientErrorException("Device already exists", Response.Status.CONFLICT);
        } else {
            throw new InternalServerErrorException(upsCreationResult.getException());
        }
    }

    @Path("/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UPSDevice getByName(@PathParam("name") String name) {
        var getByNameResult = upsDevicesServices.getByName(name);
        if (getByNameResult.isSuccess()) {
            return getByNameResult.getContent();
        }
        if (getByNameResult.getException() instanceof UPSPulsarException.EntityNotFoundException) {
            throw new NotFoundException(getByNameResult.getException().getMessage());
        } else {
            throw new InternalServerErrorException(getByNameResult.getException());
        }
    }

}
