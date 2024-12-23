package red.tetracube.upspulsar.device;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.upspulsar.database.entities.UPSEntity;
import red.tetracube.upspulsar.device.payloads.api.UPSDevice;
import red.tetracube.upspulsar.dto.Result;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;

@ApplicationScoped
public class UPSDevicesServices {

    private final static Logger LOGGER = LoggerFactory.getLogger(UPSDevicesServices.class);

    @Transactional
    public Result<UPSDevice> createDevice(UPSDevice request) {
        LOGGER.info("Searching for another device with same data");
        var exists = UPSEntity.findByIdOptional(request.deviceId).isPresent();
        if (exists) {
            LOGGER.error("There is another device with the same data {}@{}:{}", request.internalName, request.deviceAddress, request.devicePort);
            return Result.failed(new UPSPulsarException.EntityExistsException("Device already provisioned with the same data"));
        }

        LOGGER.info("Creating a new device");
        var device = new UPSEntity();
        device.host = request.deviceAddress.trim();
        device.port = request.devicePort;
        device.name = request.internalName;
        device.id = request.deviceId;
        device.persist();

        return Result.success(
            new UPSDevice(device.id, device.host, device.port, device.name)
        );
    }

    @Transactional
    public Result<UPSDevice> getById(UUID id) {
        return UPSEntity.<UPSEntity>findByIdOptional(id)
                .map(d -> new UPSDevice(d.id, d.host, d.port, d.name))
                .map(Result::success)
                .orElseGet(() -> 
                    Result.failed(
                        new UPSPulsarException.EntityNotFoundException("Device with id " + id.toString() + " not found")
                    )
                );
    } 

}
