package red.tetracube.upspulsar.device.mappers;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import red.tetracube.upspulsar.device.payloads.kafka.UPSProvisioning;

public class UPSProvisioningDeserializer extends ObjectMapperDeserializer<UPSProvisioning> {

    public UPSProvisioningDeserializer() {
        super(UPSProvisioning.class);
    }

}