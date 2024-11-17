package red.tetracube.upspulsar.database.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import red.tetracube.upspulsar.enumerations.ConnectivityStatus;
import red.tetracube.upspulsar.enumerations.TelemetryStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ups_scan_telemetry")
public class UPSScanTelemetryEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "connectivity", nullable = false)
    public ConnectivityStatus connectivity;

    @Enumerated(EnumType.STRING)
    @Column(name = "telemetry_status", nullable = false)
    public TelemetryStatus telemetryStatus;

    @Column(name = "telemetry_ts")
    public Instant telemetryTS;

    @ManyToOne(targetEntity = UPSEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ups_device_id", nullable = false)
    public UPSEntity ups;
}
