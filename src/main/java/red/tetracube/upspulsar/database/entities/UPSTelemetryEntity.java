package red.tetracube.upspulsar.database.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import red.tetracube.upspulsar.enumerations.ConnectivityHealth;
import red.tetracube.upspulsar.enumerations.TelemetryHealth;
import red.tetracube.upspulsar.enumerations.UPSStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ups_telemetry")
public class UPSTelemetryEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "out_frequency")
    public float outFrequency;

    @Column(name = "out_frequency_nominal")
    public float outFrequencyNominal;

    @Column(name = "out_voltage")
    public float outVoltage;

    @Column(name = "out_current")
    public float outCurrent;

    @Column(name = "out_current_nominal")
    public long outCurrentNominal;

    @Column(name = "out_voltage_nominal")
    public float outVoltageNominal;

    @Column(name = "battery_voltage")
    public float batteryVoltage;

    @Column(name = "battery_runtime")
    public Long batteryRuntime;

    @Column(name = "battery_voltage_nominal")
    public float batteryVoltageNominal;

    @Column(name = "load")
    public Long load;

    @Column(name = "temperature")
    public float temperature;

    @Column(name = "in_frequency")
    public float inFrequency;

    @Column(name = "in_voltage_nominal")
    public float inVoltageNominal;

    @Column(name = "in_voltage")
    public float inVoltage;

    @Column(name = "power_factor")
    public float powerFactor;

    @Column(name = "battery_charge")
    public float batteryCharge;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_status")
    public UPSStatus primaryStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "secondary_status")
    public UPSStatus secondaryStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "connectivity_health", nullable = false)
    public ConnectivityHealth connectivityHealth;

    @Enumerated(EnumType.STRING)
    @Column(name = "telemetry_health", nullable = false)
    public TelemetryHealth telemetryHealth;

    @Column(name = "telemetry_ts", nullable = false)
    public Instant telemetryTS;

    @ManyToOne(targetEntity = UPSEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ups_device_id", nullable = false)
    public UPSEntity ups;

    public static UPSTelemetryEntity buildUnhealthyConnectivityTelemetry(UPSEntity ups) {
        var entity = new UPSTelemetryEntity();
        entity.id = UUID.randomUUID();
        entity.ups = ups;
        entity.connectivityHealth = ConnectivityHealth.UNREACHABLE;
        entity.telemetryHealth = TelemetryHealth.NOT_TRANSMITTING;
        entity.telemetryTS = Instant.now();
        return entity;
    }

    public static UPSTelemetryEntity buildInvalidTelemetry(UPSEntity ups) {
        var entity = new UPSTelemetryEntity();
        entity.id = UUID.randomUUID();
        entity.ups = ups;
        entity.connectivityHealth = ConnectivityHealth.ONLINE;
        entity.telemetryHealth = TelemetryHealth.INVALID_RESPONSE;
        entity.telemetryTS = Instant.now();
        return entity;
    }

}