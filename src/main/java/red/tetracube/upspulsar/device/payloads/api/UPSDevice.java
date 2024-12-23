package red.tetracube.upspulsar.device.payloads.api;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UPSDevice {

    @JsonProperty
    public UUID deviceId;

    @JsonProperty
    public String deviceAddress;

    @JsonProperty
    public Integer devicePort;

    @JsonProperty
    public String internalName;

    public UPSDevice(UUID deviceId, String deviceAddress, Integer devicePort, String internalName) {
        this.deviceAddress = deviceAddress;
        this.devicePort = devicePort;
        this.internalName = internalName;
        this.deviceId = deviceId;
    }    

}
