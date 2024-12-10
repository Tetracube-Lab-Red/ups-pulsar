package red.tetracube.upspulsar.telemetry.payloads.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import red.tetracube.upspulsar.enumerations.ConnectivityHealth;
import red.tetracube.upspulsar.enumerations.TelemetryHealth;
import red.tetracube.upspulsar.enumerations.UPSStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "telemetryType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceTelemetryData.UPSTelemetryData.class, name = "UPS_TELEMETRY_DATA")
})
public sealed class DeviceTelemetryData permits DeviceTelemetryData.UPSTelemetryData {

    @JsonProperty
    public UUID deviceId;

    @JsonProperty
    public Instant telemetryTS;

    @JsonProperty
    public ConnectivityHealth connectivityHealth;

    @JsonProperty
    public TelemetryHealth telemetryHealth;

    public static final class UPSTelemetryData extends DeviceTelemetryData {

        @JsonProperty
        public float outFrequency;

        @JsonProperty
        public float outVoltage;

        @JsonProperty
        public float outCurrent;

        @JsonProperty
        public float batteryVoltage;

        @JsonProperty
        public Long batteryRuntime;

        @JsonProperty
        public Long load;

        @JsonProperty
        public float temperature;

        @JsonProperty
        public float inFrequency;

        @JsonProperty
        public float inVoltage;

        @JsonProperty
        public float powerFactor;

        @JsonProperty
        public float batteryCharge;

        @JsonProperty
        public List<UPSStatus> statuses;

    }

}
