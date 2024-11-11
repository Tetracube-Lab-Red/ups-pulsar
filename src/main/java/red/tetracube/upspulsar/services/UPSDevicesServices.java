package red.tetracube.upspulsar.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.upspulsar.database.entities.UPSEntity;
import red.tetracube.upspulsar.dto.UPSDevice;
import red.tetracube.upspulsar.dto.Result;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;

import java.util.UUID;

@ApplicationScoped
public class UPSDevicesServices {

    private final static Logger LOGGER = LoggerFactory.getLogger(UPSDevicesServices.class);

    @Transactional
    public Result<Void> createDevice(UPSDevice request) {
        LOGGER.info("Searching for another device with same data");
        var exists = UPSEntity.count(
                "name = ?1 and host = ?2 and port = ?3",
                request.internalName().trim(),
                request.hostname().trim(),
                request.port()
        ) == 1;

        if (exists) {
            LOGGER.error("There is another device with the same data {}@{}:{}", request.internalName(), request.hostname(), request.port());
            return Result.failed(new UPSPulsarException.EntityExistsException("Device already provisioned with the same data"));
        }

        LOGGER.info("Creating a new device");
        var device = new UPSEntity();
        device.host = request.hostname().trim();
        device.port = request.port();
        device.name = request.internalName().trim();
        device.id = UUID.randomUUID();
        device.persist();

        return Result.success(null);
    }

    @Transactional
    public Result<UPSDevice> getByName(String name) {
        return UPSEntity.<UPSEntity>find("name", name)
                .firstResultOptional()
                .map(d ->
                        new UPSDevice(
                                d.host,
                                d.port,
                                d.name
                        )
                )
                .map(Result::success)
                .orElseGet(() -> Result.failed(new UPSPulsarException.EntityNotFoundException("Device with name " + name + " not found")));
    }

}
