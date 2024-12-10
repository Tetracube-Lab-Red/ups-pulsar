package red.tetracube.upspulsar.telemetry;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.smallrye.reactive.messaging.kafka.Record;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import red.tetracube.upspulsar.enumerations.DeviceType;
import red.tetracube.upspulsar.telemetry.payloads.kafka.DeviceTelemetryData;

import java.util.UUID;

@ApplicationScoped
public class TelemetryKafka {

    @Inject
    UPSTelemetryServices upsTelemetryServices;

    @Inject
    @Channel("device-telemetry-response")
    Emitter<Record<UUID, DeviceTelemetryData>> deviceTelemetryEmitter;

    @RunOnVirtualThread
    @Incoming("device-telemetry-request")
    public void getUPSTelemetry(ConsumerRecord<String, UUID> request) {
        if (!request.key().equals(DeviceType.UPS.name())) {
            return;
        }
        upsTelemetryServices.getUPSTelemetry(request.value())
                .forEach(telemetry ->
                        deviceTelemetryEmitter.send(
                                Record.of(request.value(), telemetry)
                        )
                );
    }

}
