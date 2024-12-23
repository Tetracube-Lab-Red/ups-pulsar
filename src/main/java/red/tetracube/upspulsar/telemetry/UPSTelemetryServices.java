package red.tetracube.upspulsar.telemetry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import red.tetracube.upspulsar.database.entities.UPSTelemetryEntity;
import red.tetracube.upspulsar.telemetry.payloads.kafka.DeviceTelemetryData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

import io.quarkus.redis.datasource.RedisDataSource;

@ApplicationScoped
public class UPSTelemetryServices {

    @Inject
    RedisDataSource redisDataSource;

    @Transactional
    public void publishHistoryTelemetry(UUID deviceId) {
        var timeLimit = Instant.now().minus(1, ChronoUnit.HOURS);
        UPSTelemetryEntity.<UPSTelemetryEntity>find(
                "ups.id = ?1 AND telemetryTS > ?2",
                deviceId,
                timeLimit
            )
            .stream()
            .map(UPSTelemetryServices::mapTelemetry)
            .forEach(telemetry ->
                redisDataSource.pubsub(DeviceTelemetryData.class)
                    .publish("device-telemetry", telemetry)
            );
    }

    private static DeviceTelemetryData mapTelemetry(UPSTelemetryEntity telemetry) {
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
