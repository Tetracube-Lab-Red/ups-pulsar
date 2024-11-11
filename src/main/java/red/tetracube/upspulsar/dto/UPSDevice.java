package red.tetracube.upspulsar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UPSDevice(
        @JsonProperty String hostname,
        @JsonProperty Integer port,
        @JsonProperty String internalName
) {
}
