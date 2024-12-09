package red.tetracube.upspulsar.device;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import red.tetracube.upspulsar.device.payloads.kafka.UPSProvisioning;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;
import red.tetracube.upspulsar.enumerations.DeviceType;

@ApplicationScoped
public class UPSDevicesIngestion {

    @Inject
    UPSDevicesServices upsDevicesServices;

    @Inject
    ObjectMapper objectMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(UPSDevicesIngestion.class);

    @RunOnVirtualThread
    @Incoming("device-provisioning")
    public void deviceProvisioning(ConsumerRecord<DeviceType, UPSProvisioning> upsProvisioning) {
        var upsCreationResult = upsDevicesServices.createDevice(upsProvisioning.value());
        if (upsCreationResult.isSuccess()) {
            return;
        }

        if (upsCreationResult.getException() instanceof UPSPulsarException.EntityExistsException) {
            throw new ClientErrorException("Device already exists", Response.Status.CONFLICT);
        } else {
            throw new InternalServerErrorException(upsCreationResult.getException());
        }
    }

    /* @Path("/{name}")
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
    } */

}
