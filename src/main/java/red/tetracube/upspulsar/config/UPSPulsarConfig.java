package red.tetracube.upspulsar.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "ups-pulsar")
public interface UPSPulsarConfig {
    MqttConfig mqtt();
}
