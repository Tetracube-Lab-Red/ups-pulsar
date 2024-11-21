package red.tetracube.upspulsar.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.upspulsar.config.UPSPulsarConfig;

import java.util.UUID;

@ApplicationScoped
public class BrokerClient {

    @Inject
    ObjectMapper objectMapper;

    private final Mqtt5AsyncClient client;

    private final static Logger LOGGER = LoggerFactory.getLogger(BrokerClient.class);

    public BrokerClient(UPSPulsarConfig upsPulsarConfig) {
        client = MqttClient.builder()
                .identifier(upsPulsarConfig.mqtt().clientName() + "-" + UUID.randomUUID())
                .automaticReconnectWithDefaultConfig()
                .automaticReconnect()
                .applyAutomaticReconnect()
                .serverHost(upsPulsarConfig.mqtt().address())
                .useMqttVersion5()
                .build()
                .toAsync();
    }

    void startup(@Observes StartupEvent event) {
        Uni.createFrom()
                .completionStage(
                        client.connect()
                )
                .call(mqtt5ConnAck -> {
                    if (mqtt5ConnAck.getReasonCode() == Mqtt5ConnAckReasonCode.SUCCESS) {
                        LOGGER.info("Successfully connected to MQTT broker, ready to listen incoming messages");
                    }
                    return Uni.createFrom().voidItem();
                })
                .subscribe()
                .with(connectAck ->
                        LOGGER.info("MQTT connection result code {}", connectAck.getReasonCode())
                );
    }

    public void publishScanTelemetryBit(String upsName) {
        try {
            this.publishMessage(
                    "devices/telemetry/UPS",
                    upsName
            );
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot send telemetry statistics for the error");
        }
    }

    private void publishMessage(String topic, Object message) throws JsonProcessingException {
        var serializedMessage = new byte[0];
        if (message instanceof String) {
            serializedMessage = ((String) message).getBytes();
        } else {
            serializedMessage = objectMapper.writeValueAsBytes(message);
        }
        var publishPublisher = this.client
                .publishWith()
                .topic(topic)
                .qos(MqttQos.EXACTLY_ONCE)
                .payload(serializedMessage)
                .send();
        Uni.createFrom().completionStage(publishPublisher)
                .subscribe()
                .with(publishResult -> {
                    if (publishResult.getError().isPresent()) {
                        LOGGER.error("Publish finished with error {}", publishResult.getError());
                        return;
                    }
                    LOGGER.info("Publish completes successfully");
                });
    }

}
