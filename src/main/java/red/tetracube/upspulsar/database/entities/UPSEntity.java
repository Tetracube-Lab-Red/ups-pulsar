package red.tetracube.upspulsar.database.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ups_devices")
public class UPSEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(name = "host", nullable = false)
    public String host;

    @Column(name = "port", nullable = false)
    public Integer port;

    @OneToMany(targetEntity = UPSScanTelemetryEntity.class, fetch = FetchType.LAZY, mappedBy = "ups", orphanRemoval = true)
    public List<UPSScanTelemetryEntity> scanTelemetries;

    @OneToMany(targetEntity = UPSTelemetryEntity.class, fetch = FetchType.LAZY, mappedBy = "ups", orphanRemoval = true)
    public List<UPSTelemetryEntity> telemetries;

}
