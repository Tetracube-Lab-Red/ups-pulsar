package red.tetracube.upspulsar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeviceProvisioningRequest(
        @JsonProperty String hostname,
        @JsonProperty Integer port,
        @JsonProperty String internalName
) {
}
