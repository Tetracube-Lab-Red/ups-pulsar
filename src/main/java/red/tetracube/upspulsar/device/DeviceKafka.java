package red.tetracube.upspulsar.device;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import red.tetracube.upspulsar.device.payloads.kafka.UPSProvisioning;
import red.tetracube.upspulsar.enumerations.DeviceType;

import java.util.UUID;

@ApplicationScoped
public class DeviceKafka {

    @Inject
    UPSDevicesServices upsDevicesServices;

    private final static Logger LOGGER = LoggerFactory.getLogger(DeviceKafka.class);

    @RunOnVirtualThread
    @Incoming("device-provisioning")
    @Outgoing("device-provisioning-ack")
    public UUID deviceProvisioning(ConsumerRecord<DeviceType, UPSProvisioning> upsProvisioning) {
        LOGGER.info("Arrived a message of device provisioning");
        var upsCreationResult = upsDevicesServices.createDevice(upsProvisioning.value());
        if (upsCreationResult.isSuccess()) {
            LOGGER.info("Sending back provisioning feedback");
            return upsProvisioning.value().deviceId;
        }
        return null;
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
