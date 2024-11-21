package red.tetracube.upspulsar.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import red.tetracube.upspulsar.database.entities.UPSEntity;
import red.tetracube.upspulsar.database.entities.UPSScanTelemetryEntity;
import red.tetracube.upspulsar.database.entities.UPSTelemetryEntity;
import red.tetracube.upspulsar.dto.Result;
import red.tetracube.upspulsar.dto.UPSTelemetryData;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;

import java.util.Arrays;
import java.util.Optional;

@ApplicationScoped
public class UPSTelemetryServices {

    @Transactional
    public Result<UPSTelemetryData> getUPSTelemetry(String upsInternalName) {
        var optionalDevice = UPSEntity.<UPSEntity>find("name", upsInternalName)
                .firstResultOptional();
        if (optionalDevice.isEmpty()) {
            return Result.failed(new UPSPulsarException.EntityNotFoundException("No device found with this name"));
        }
        var optionalTelemetry = UPSTelemetryEntity.<UPSTelemetryEntity>find("ups", Sort.descending("telemetryTS"), optionalDevice.get())
                .firstResultOptional();
        var optionalConnectionTelemetry = UPSScanTelemetryEntity.<UPSScanTelemetryEntity>find("ups", Sort.descending("telemetryTS"), optionalDevice.get())
                .firstResultOptional();
        if (optionalConnectionTelemetry.isEmpty()) {
            return Result.failed(new UPSPulsarException.EntityNotFoundException("No connection telemetry found for device"));
        }

        var basicTelemetryData = getUpsTelemetryData(
                optionalConnectionTelemetry.get(),
                optionalTelemetry,
                optionalDevice.get()
        );
        return Result.success(basicTelemetryData);
    }

    private static UPSTelemetryData getUpsTelemetryData(
            UPSScanTelemetryEntity connectionTelemetry,
            Optional<UPSTelemetryEntity> telemetry,
            UPSEntity device
    ) {
        return new UPSTelemetryData(
                device.name,
                telemetry.map(t -> t.outFrequency).orElse(0f),
                telemetry.map(t -> t.outVoltage).orElse(0f),
                telemetry.map(t -> t.outCurrent).orElse(0f),
                telemetry.map(t -> t.batteryVoltage).orElse(0f),
                telemetry.map(t -> t.batteryRuntime).orElse(0L),
                telemetry.map(t -> t.load).orElse(0L),
                telemetry.map(t -> t.temperature).orElse(0f),
                telemetry.map(t -> t.inFrequency).orElse(0f),
                telemetry.map(t -> t.inVoltage).orElse(0f),
                telemetry.map(t -> t.powerFactor).orElse(0f),
                telemetry.map(t -> t.batteryCharge).orElse(0f),
                telemetry.map(t ->
                                Arrays.asList(t.primaryStatus, t.secondaryStatus)
                        )
                        .orElse(null),
                connectionTelemetry.connectivity,
                connectionTelemetry.telemetryStatus,
                connectionTelemetry.telemetryTS
        );
    }

}
