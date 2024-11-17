package red.tetracube.upspulsar;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.upspulsar.services.UPSScannerService;

@ApplicationScoped
public class UPSScannerScheduler {

    @Inject
    UPSScannerService upsScannerService;

    private final static Logger LOG = LoggerFactory.getLogger(UPSScannerScheduler.class);

    @Scheduled(every = "1m")
    void runUPSScanners() {
        LOG.info("It's time to scan the UPSs");
        upsScannerService.doUPSsScan();
    }

}
