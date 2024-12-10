package red.tetracube.upspulsar.telemetry;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import red.tetracube.upspulsar.telemetry.payloads.kafka.DeviceTelemetryData;

import java.util.UUID;

@ApplicationScoped
public class TelemetryKafka {

    @Inject
    UPSTelemetryServices upsTelemetryServices;

    @Inject
    @Channel("device-telemetry-response")
    Emitter<DeviceTelemetryData> deviceTelemetryEmitter;

    @RunOnVirtualThread
    @Incoming("device-telemetry-request")
    public void getUPSTelemetry(UUID deviceId) {
        upsTelemetryServices.getUPSTelemetry(deviceId)
                .forEach(telemetry -> deviceTelemetryEmitter.send(telemetry));
    }

}
