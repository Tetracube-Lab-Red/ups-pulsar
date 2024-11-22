package red.tetracube.upspulsar.mappers;

import red.tetracube.upspulsar.database.entities.UPSEntity;
import red.tetracube.upspulsar.database.entities.UPSTelemetryEntity;
import red.tetracube.upspulsar.enumerations.ConnectivityHealth;
import red.tetracube.upspulsar.enumerations.TelemetryHealth;
import red.tetracube.upspulsar.enumerations.UPSStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TelemetryMapper {

    private final Map<String, String> rawTelemetryMap;
    public final UPSTelemetryEntity upsTelemetryEntity = new UPSTelemetryEntity();

    public TelemetryMapper(UPSEntity upsEntity, Map<String, String> rawTelemetryMap) {
        this.rawTelemetryMap = rawTelemetryMap;
        this.upsTelemetryEntity.ups = upsEntity;
        this.upsTelemetryEntity.telemetryTS = Instant.now();
        this.upsTelemetryEntity.id = UUID.randomUUID();
        this.upsTelemetryEntity.telemetryHealth = TelemetryHealth.TRANSMITTING;
        this.upsTelemetryEntity.connectivityHealth = ConnectivityHealth.ONLINE;
    }

    public void buildDeviceTelemetry() {
        this.upsTelemetryEntity.outFrequency = extractFloatValue(rawTelemetryMap.get("output.frequency"));
        this.upsTelemetryEntity.outFrequencyNominal = extractFloatValue(rawTelemetryMap.get("output.frequency.nominal"));
        this.upsTelemetryEntity.outVoltage = extractFloatValue(rawTelemetryMap.get("output.voltage"));
        this.upsTelemetryEntity.outCurrent = extractFloatValue(rawTelemetryMap.get("output.current"));
        this.upsTelemetryEntity.outCurrentNominal = extractLongValue(rawTelemetryMap.get("output.current.nominal"));
        this.upsTelemetryEntity.outVoltageNominal = extractFloatValue(rawTelemetryMap.get("output.voltage.nominal"));
        this.upsTelemetryEntity.batteryVoltage = extractFloatValue(rawTelemetryMap.get("battery.voltage"));
        this.upsTelemetryEntity.batteryRuntime = extractLongValue(rawTelemetryMap.get("battery.runtime"));
        this.upsTelemetryEntity.batteryVoltageNominal = extractFloatValue(rawTelemetryMap.get("battery.voltage.nominal"));
        this.upsTelemetryEntity.load = extractLongValue(rawTelemetryMap.get("ups.load"));
        this.upsTelemetryEntity.temperature = extractFloatValue(rawTelemetryMap.get("ups.temperature"));
        this.upsTelemetryEntity.inFrequency = extractFloatValue(rawTelemetryMap.get("input.frequency"));
        this.upsTelemetryEntity.inVoltageNominal = extractFloatValue(rawTelemetryMap.get("input.voltage.nominal"));
        this.upsTelemetryEntity.inVoltage = extractFloatValue(rawTelemetryMap.get("input.voltage"));
        this.upsTelemetryEntity.powerFactor = extractFloatValue(rawTelemetryMap.get("output.powerfactor"));
        this.upsTelemetryEntity.batteryCharge = extractLongValue(rawTelemetryMap.get("battery.charge"));

        var statuses = extractUPSStatus("ups.status");
        this.upsTelemetryEntity.primaryStatus = !statuses.isEmpty() ? statuses.getFirst() : null;
        this.upsTelemetryEntity.secondaryStatus = statuses.size() > 1 ? statuses.getLast() : null;
    }

    public List<UPSStatus> extractUPSStatus(String field) {
        var rawValue = extractStringValue(field);
        var statusList = new ArrayList<UPSStatus>();
        if (rawValue.contains("OL")) {
            statusList.add(UPSStatus.ONLINE);
        } else if (rawValue.contains("OB")) {
            statusList.add(UPSStatus.ON_BATTERY);
        } else if (rawValue.contains("LB")) {
            statusList.add(UPSStatus.LOW_BATTERY);
        } else if (rawValue.contains("HB")) {
            statusList.add(UPSStatus.HIGH_BATTERY);
        } else if (rawValue.contains("RB")) {
            statusList.add(UPSStatus.REPLACE_BATTERY);
        } else if (rawValue.contains("CHRG")) {
            statusList.add(UPSStatus.CHARGING);
        } else if (rawValue.contains("DISCHRG")) {
            statusList.add(UPSStatus.DISCHARGING);
        } else if (rawValue.contains("BYPASS")) {
            statusList.add(UPSStatus.BYPASS);
        } else if (rawValue.contains("CAL")) {
            statusList.add(UPSStatus.CALIBRATING);
        } else if (rawValue.contains("OFF")) {
            statusList.add(UPSStatus.OFFLINE);
        } else if (rawValue.contains("OVER")) {
            statusList.add(UPSStatus.OVERLOADED);
        } else if (rawValue.contains("TRIM")) {
            statusList.add(UPSStatus.IN_TRIMMING);
        } else if (rawValue.contains("BOOST")) {
            statusList.add(UPSStatus.IN_BOOSTING);
        } else if (rawValue.contains("FSD")) {
            statusList.add(UPSStatus.FORCED_SHUTDOWN);
        }
        return statusList;
    }

    private String extractStringValue(String key) {
        return rawTelemetryMap.getOrDefault(key, "");
    }

    private long extractLongValue(String rawValue) {
        try {
            return Long.parseLong(rawValue);
        } catch (NumberFormatException e) {
            return Long.MIN_VALUE;
        }
    }

    private float extractFloatValue(String rawValue) {
        try {
            return Float.parseFloat(rawValue);
        } catch (NumberFormatException e) {
            return Float.MIN_VALUE;
        }
    }

}
