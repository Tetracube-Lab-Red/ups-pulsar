package red.tetracube;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import red.tetracube.upspulsar.dto.DeviceProvisioningRequest;
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
    public void deviceProvisioning(@Valid DeviceProvisioningRequest request) {
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
}
