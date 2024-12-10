package red.tetracube.upspulsar.telemetry;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import red.tetracube.upspulsar.database.entities.UPSTelemetryEntity;
import red.tetracube.upspulsar.telemetry.payloads.kafka.DeviceTelemetryData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UPSTelemetryServices {

    @Transactional
    public List<DeviceTelemetryData> getUPSTelemetry(UUID deviceId) {
        var timeLimit = Instant.now().minus(1, ChronoUnit.HOURS);
        return UPSTelemetryEntity.<UPSTelemetryEntity>find(
                "ups.id = ?1 AND telemetryTS > ?2",
                        Sort.descending("telemetryTS"),
                        deviceId,
                        timeLimit
                )
                .stream()
                .map(UPSTelemetryServices::getUpsTelemetryData)
                .toList();
    }

    private static DeviceTelemetryData getUpsTelemetryData(UPSTelemetryEntity telemetry) {
        var telemetryData = new DeviceTelemetryData.UPSTelemetryData();
        telemetryData.telemetryHealth = telemetry.telemetryHealth;
        telemetryData.telemetryTS = telemetry.telemetryTS;
        telemetryData.connectivityHealth = telemetry.connectivityHealth;
        telemetryData.batteryCharge = telemetry.batteryCharge;
        telemetryData.deviceId = telemetry.ups.id;
        telemetryData.outFrequency = telemetry.outFrequency;
        telemetryData.outVoltage = telemetry.outVoltage;
        telemetryData.outCurrent = telemetry.outCurrent;
        telemetryData.batteryVoltage = telemetry.batteryVoltage;
        telemetryData.batteryRuntime = telemetry.batteryRuntime;
        telemetryData.load = telemetry.load;
        telemetryData.temperature = telemetry.temperature;
        telemetryData.inFrequency = telemetry.inFrequency;
        telemetryData.inVoltage = telemetry.inVoltage;
        telemetryData.powerFactor = telemetry.powerFactor;
        telemetryData.batteryCharge = telemetry.batteryCharge;
        telemetryData.statuses = Arrays.asList(telemetry.primaryStatus, telemetry.secondaryStatus);
        return telemetryData;
    }

}
