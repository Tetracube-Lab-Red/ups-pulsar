package red.tetracube.upspulsar.dto;

import java.util.List;

public record AllDevicesResponse(
        List<UPSDevice> devices
) {
}
