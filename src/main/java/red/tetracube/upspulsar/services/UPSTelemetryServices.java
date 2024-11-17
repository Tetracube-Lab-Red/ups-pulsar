package red.tetracube.upspulsar.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import red.tetracube.upspulsar.database.entities.UPSEntity;
import red.tetracube.upspulsar.database.entities.UPSScanTelemetryEntity;
import red.tetracube.upspulsar.database.entities.UPSTelemetryEntity;
import red.tetracube.upspulsar.dto.Result;
import red.tetracube.upspulsar.dto.UPSBasicTelemetryData;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;

import java.util.Arrays;

@ApplicationScoped
public class UPSTelemetryServices {

    @Transactional
    public Result<UPSBasicTelemetryData> getBasicUPSTelemetry(String upsInternalName) {
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
        var basicTelemetryData = new UPSBasicTelemetryData(
                optionalDevice.get().name,
                Arrays.asList(optionalTelemetry.get().primaryStatus, optionalTelemetry.get().secondaryStatus),
                optionalConnectionTelemetry.get().connectivity,
                optionalConnectionTelemetry.get().telemetryStatus,
                optionalTelemetry.get().telemetryTS
        );
        return Result.success(basicTelemetryData);
    }


}
