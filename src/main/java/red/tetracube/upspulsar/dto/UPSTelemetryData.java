package red.tetracube.upspulsar.dto;

import red.tetracube.upspulsar.enumerations.ConnectivityStatus;
import red.tetracube.upspulsar.enumerations.TelemetryStatus;
import red.tetracube.upspulsar.enumerations.UPSStatus;

import java.time.Instant;
import java.util.List;

public record UPSTelemetryData(
        String deviceInternalName,
        float outFrequency,
        float outVoltage,
        float outCurrent,
        float batteryVoltage,
        Long batteryRuntime,
        Long load,
        float temperature,
        float inFrequency,
        float inVoltage,
        float powerFactor,
        float batteryCharge,
        List<UPSStatus> statuses,
        ConnectivityStatus connectivityStatus,
        TelemetryStatus telemetryStatus,
        Instant telemetryTS
) {
}
