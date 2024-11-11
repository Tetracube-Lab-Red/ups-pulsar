package red.tetracube.upspulsar.database.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

}
