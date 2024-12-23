package red.tetracube.upspulsar.device;

import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import red.tetracube.upspulsar.device.payloads.api.UPSDevice;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;
import java.util.UUID;

@Path("/devices")
public class DeviceAPI {

    @Inject
    UPSDevicesServices upsDevicesServices;

    private final static Logger LOGGER = LoggerFactory.getLogger(DeviceAPI.class);

    @POST
    @Path("/provisioning")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public UPSDevice deviceProvisioning(UPSDevice upsProvisioning) {
        LOGGER.info("Arrived a message of device provisioning");
        var upsCreationResult = upsDevicesServices.createDevice(upsProvisioning);
        if (upsCreationResult.isSuccess()) {
            LOGGER.info("Sending back provisioning feedback");
            return upsCreationResult.getContent();
        }
        
        if (upsCreationResult.getException() instanceof UPSPulsarException.EntityExistsException) {
            throw new ClientErrorException(StatusCode.CONFLICT, upsCreationResult.getException());
        } else {
            throw new InternalServerErrorException(upsCreationResult.getException());
        }
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UPSDevice getById(@PathParam("id") UUID id) {
        var getByNameResult = upsDevicesServices.getById(id);
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
