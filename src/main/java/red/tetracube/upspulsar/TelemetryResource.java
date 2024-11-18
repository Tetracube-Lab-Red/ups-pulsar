package red.tetracube.upspulsar;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import red.tetracube.upspulsar.dto.UPSTelemetryData;
import red.tetracube.upspulsar.dto.exceptions.UPSPulsarException;
import red.tetracube.upspulsar.services.UPSTelemetryServices;

@Path("/device/{internalName}/telemetry")
public class TelemetryResource {

    @Inject
    UPSTelemetryServices upsTelemetryServices;

    @RunOnVirtualThread
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public UPSTelemetryData getUPSTelemetry(@PathParam("internalName") String internalName) {
        var telemetryResult = upsTelemetryServices.getUPSTelemetry(internalName);
        if (!telemetryResult.isSuccess()) {
            if (telemetryResult.getException() instanceof UPSPulsarException.EntityNotFoundException) {
                throw new NotFoundException();
            } else {
                throw new InternalServerErrorException();
            }
        }
        return telemetryResult.getContent();
    }

}
