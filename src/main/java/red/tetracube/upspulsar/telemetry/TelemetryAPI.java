package red.tetracube.upspulsar.telemetry;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.UUID;

@Path("/telemetry")
public class TelemetryAPI {

    @Inject
    UPSTelemetryServices upsTelemetryServices;

    @HEAD
    @Path("/{deviceId}")
    @RunOnVirtualThread
    public void getUPSTelemetry(@PathParam("deviceId") UUID deviceId) {
        upsTelemetryServices.publishHistoryTelemetry(deviceId);
    }

}
