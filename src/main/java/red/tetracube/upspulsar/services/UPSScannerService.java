package red.tetracube.upspulsar.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.upspulsar.clients.NUTClient;
import red.tetracube.upspulsar.database.entities.UPSEntity;
import red.tetracube.upspulsar.database.entities.UPSTelemetryEntity;
import red.tetracube.upspulsar.mappers.TelemetryMapper;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;

@ApplicationScoped
public class UPSScannerService {

    private final static Logger LOG = LoggerFactory.getLogger(UPSScannerService.class);

    @Transactional
    public void doUPSsScan() {
        LOG.debug("Collecting UPSs to scan");
        var upsList = UPSEntity.<UPSEntity>findAll().stream()
                .toList();
        LOG.debug("Found {} UPSs to scan", upsList.size());
        upsList.stream()
                .map(ups -> {
                    try {
                        var upsData = new HashMap<>(getUPSData(ups));
                        var telemetryMapper = new TelemetryMapper(ups, upsData);
                        telemetryMapper.buildDeviceTelemetry();
                        return telemetryMapper.upsTelemetryEntity;
                    } catch (ConnectException | UnknownHostException e) {
                        LOG.error("Cannot reach NUT server due error: ", e);
                        return UPSTelemetryEntity.buildUnhealthyConnectivityTelemetry(ups);
                    } catch (IOException e) {
                        LOG.error("Cannot retrieve data due error: ", e);
                        return UPSTelemetryEntity.buildInvalidTelemetry(ups);
                    }
                })
                .forEach(upsTelemetryEntity -> {
                    upsTelemetryEntity.persist();
                //    brokerClient.publishScanTelemetryBit(upsTelemetryEntity.ups.name);
                });
    }

    private HashMap<String, String> getUPSData(UPSEntity ups) throws IOException {
        try (var nutClient = new NUTClient(ups)) {
            return nutClient.retrieveUPSData();
        }
    }

}
