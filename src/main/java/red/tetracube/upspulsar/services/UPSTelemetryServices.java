package red.tetracube.upspulsar.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import red.tetracube.upspulsar.database.entities.UPSEntity;
import red.tetracube.upspulsar.database.entities.UPSTelemetryEntity;
import red.tetracube.upspulsar.dto.Result;
import red.tetracube.upspulsar.dto.UPSTelemetryData;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;

import java.util.Arrays;

@ApplicationScoped
public class UPSTelemetryServices {

    @Transactional
    public Result<UPSTelemetryData> getUPSTelemetry(String upsInternalName) {
        return UPSEntity.<UPSEntity>find("name", upsInternalName)
                .firstResultOptional()
                .map(upsEntity ->
                        UPSTelemetryEntity.<UPSTelemetryEntity>find(
                                        "ups",
                                        Sort.descending("telemetryTS"),
                                        upsEntity
                                )
                                .firstResultOptional()
                                .map(telemetry -> {
                                    var response = getUpsTelemetryData(telemetry, upsEntity);
                                    return Result.success(response);
                                })
                                .orElse(
                                        Result.failed(new UPSPulsarException.EntityNotFoundException("No connection telemetry found for device"))
                                )
                )
                .orElseGet(() -> Result.failed(new UPSPulsarException.EntityNotFoundException("No device found with this name")));

    }

    private static UPSTelemetryData getUpsTelemetryData(
            UPSTelemetryEntity telemetry,
            UPSEntity device
    ) {
        return new UPSTelemetryData(
                device.name,
                telemetry.outFrequency,
                telemetry.outVoltage,
                telemetry.outCurrent,
                telemetry.batteryVoltage,
                telemetry.batteryRuntime,
                telemetry.load,
                telemetry.temperature,
                telemetry.inFrequency,
                telemetry.inVoltage,
                telemetry.powerFactor,
                telemetry.batteryCharge,
                Arrays.asList(telemetry.primaryStatus, telemetry.secondaryStatus),
                telemetry.connectivityHealth,
                telemetry.telemetryHealth,
                telemetry.telemetryTS
        );
    }

}
