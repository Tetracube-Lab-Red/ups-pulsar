package red.tetracube.upspulsar.device.payloads.kafka;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UPSProvisioning {

    @JsonProperty
    public UUID deviceId;

    @JsonProperty
    public String deviceAddress;

    @JsonProperty
    public Integer devicePort;

    @JsonProperty
    public String internalName;

    public UPSProvisioning(UUID deviceId, String deviceAddress, Integer devicePort, String internalName) {
        this.deviceAddress = deviceAddress;
        this.devicePort = devicePort;
        this.internalName = internalName;
        this.deviceId = deviceId;
    }    

}
