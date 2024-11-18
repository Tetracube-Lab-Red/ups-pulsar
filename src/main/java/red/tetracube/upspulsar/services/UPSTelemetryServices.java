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
        if (optionalTelemetry.isEmpty()) {
            return Result.failed(new UPSPulsarException.EntityNotFoundException("No telemetry found for device"));
        }
        var optionalConnectionTelemetry = UPSScanTelemetryEntity.<UPSScanTelemetryEntity>find("ups", Sort.descending("telemetryTS"), optionalDevice.get())
                .firstResultOptional();
        if (optionalConnectionTelemetry.isEmpty()) {
            return Result.failed(new UPSPulsarException.EntityNotFoundException("No connection telemetry found for device"));
        }

        var basicTelemetryData = getUpsTelemetryData(
                optionalConnectionTelemetry.get(),
                optionalTelemetry.get(),
                optionalDevice.get()
        );
        return Result.success(basicTelemetryData);
    }

    private static UPSTelemetryData getUpsTelemetryData(
            UPSScanTelemetryEntity connectionTelemetry,
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
                connectionTelemetry.connectivity,
                connectionTelemetry.telemetryStatus,
                telemetry.telemetryTS
        );
    }

}
