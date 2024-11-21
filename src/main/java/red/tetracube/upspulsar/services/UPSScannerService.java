package red.tetracube.upspulsar.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.upspulsar.clients.BrokerClient;
import red.tetracube.upspulsar.clients.NUTClient;
import red.tetracube.upspulsar.database.entities.UPSEntity;
import red.tetracube.upspulsar.database.entities.UPSScanTelemetryEntity;
import red.tetracube.upspulsar.enumerations.ConnectivityStatus;
import red.tetracube.upspulsar.enumerations.TelemetryStatus;
import red.tetracube.upspulsar.mappers.TelemetryMapper;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@ApplicationScoped
public class UPSScannerService {

    @Inject
    BrokerClient brokerClient;

    private final static Logger LOG = LoggerFactory.getLogger(UPSScannerService.class);

    @Transactional
    public void doUPSsScan() {
        LOG.info("Collecting UPSs to scan");
        var upsList = UPSEntity.<UPSEntity>findAll().stream()
                .toList();
        LOG.info("Found {} UPSs to scan", upsList.size());
        upsList.stream()
                .map(ups -> {
                    var upsData = new HashMap<String, String>();
                    var telemetryMapper = new TelemetryMapper(ups, upsData);
                    try {
                        upsData.putAll(getUPSData(ups));
                        storeSuccessfulScan(ups);
                        telemetryMapper.buildDeviceTelemetry();
                    } catch (ConnectException | UnknownHostException e) {
                        LOG.error("No host found for NUT {}:{} for UPS {}",
                                ups.host,
                                ups.port,
                                ups.name,
                                e
                        );
                        storeUnreachableUPS(ups);
                    } catch (IOException e) {
                        LOG.error("Cannot retrieve data from NUT {}:{} for UPS {}",
                                ups.host,
                                ups.port,
                                ups.name,
                                e
                        );
                        storeInvalidResponse(ups);
                    }
                    return telemetryMapper.upsTelemetryEntity;
                })
                .forEach(upsTelemetryEntity -> {
                    upsTelemetryEntity.persist();
                    brokerClient.publishScanTelemetryBit(upsTelemetryEntity.ups.name);
                });
    }

    private HashMap<String, String> getUPSData(UPSEntity ups) throws IOException {
        try (var nutClient = new NUTClient(ups)) {
            return nutClient.retrieveUPSData();
        }
    }

    private void storeUnreachableUPS(UPSEntity ups) {
        storeScan(ups, TelemetryStatus.NOT_TRANSMITTING, ConnectivityStatus.UNREACHABLE);
    }

    private void storeInvalidResponse(UPSEntity ups) {
        storeScan(ups, TelemetryStatus.INVALID_RESPONSE, ConnectivityStatus.ONLINE);
    }

    private void storeSuccessfulScan(UPSEntity ups) {
        storeScan(ups, TelemetryStatus.TRANSMITTING, ConnectivityStatus.ONLINE);
    }

    private void storeScan(UPSEntity ups, TelemetryStatus telemetryStatus, ConnectivityStatus connectivityStatus) {
        var scanTelemetryEntity = new UPSScanTelemetryEntity();
        scanTelemetryEntity.id = UUID.randomUUID();
        scanTelemetryEntity.telemetryTS = Instant.now();
        scanTelemetryEntity.telemetryStatus = telemetryStatus;
        scanTelemetryEntity.connectivity = connectivityStatus;
        scanTelemetryEntity.ups = ups;
        scanTelemetryEntity.persist();
    }

}
