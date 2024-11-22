package red.tetracube.upspulsar.dto;

import red.tetracube.upspulsar.enumerations.ConnectivityHealth;
import red.tetracube.upspulsar.enumerations.TelemetryHealth;
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
        ConnectivityHealth connectivityHealth,
        TelemetryHealth telemetryHealth,
        Instant telemetryTS
) {
}
