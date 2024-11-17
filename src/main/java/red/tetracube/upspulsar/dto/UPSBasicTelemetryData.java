package red.tetracube.upspulsar.dto;

import red.tetracube.upspulsar.enumerations.ConnectivityStatus;
import red.tetracube.upspulsar.enumerations.TelemetryStatus;
import red.tetracube.upspulsar.enumerations.UPSStatus;

import java.time.Instant;
import java.util.List;

public record UPSBasicTelemetryData(
        String deviceInternalName,
        List<UPSStatus> statuses,
        ConnectivityStatus connectivityStatus,
        TelemetryStatus telemetryStatus,
        Instant telemetryTS
) {
}
