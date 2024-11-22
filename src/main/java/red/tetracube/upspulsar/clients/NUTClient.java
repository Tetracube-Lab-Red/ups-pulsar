package red.tetracube.upspulsar.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.upspulsar.database.entities.UPSEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class NUTClient implements AutoCloseable {

    private final String regex = "^VAR (\\w+) ([\\w\\.]+) (\\\".*\\\")$";
    private final Pattern pattern = Pattern.compile(regex);
    private final Socket socket;
    private final String upsPhysicalName;

    private static final Logger LOG = LoggerFactory.getLogger(NUTClient.class);

    public NUTClient(UPSEntity ups) throws IOException {
        LOG.info("Initializing TCP connection to {}:{}", ups.host, ups.port);
        this.upsPhysicalName = ups.name;
        socket = new Socket(ups.host, ups.port);
    }

    public HashMap<String, String> retrieveUPSData() throws UnknownHostException, IOException {
        var outputStream = socket.getOutputStream();
        var writer = new PrintWriter(outputStream, true);
        var inputStream = socket.getInputStream();
        var reader = new BufferedReader(new InputStreamReader(inputStream));

        var message = String.format("LIST VAR %s\n", upsPhysicalName);
        writer.println(message);
        LOG.info("Sent to server: {}", message);

        var telemetryMap = new HashMap<String, String>();
        while (true) {
            var responseLine = reader.readLine();
            if (responseLine.startsWith("BEGIN")) {
                LOG.debug("Ignoring telemetry begin line");
                continue;
            }
            if (responseLine.startsWith("END")) {
                LOG.debug("Read response completed, ignoring the line");
                break;
            }

            var matcher = pattern.matcher(responseLine);
            if (!matcher.matches() || matcher.groupCount() < 3) {
                LOG.warn("The line {} is not matching the telemetry pattern", responseLine);
                continue;
            }

            var key = matcher.group(2).trim();
            var rawValue = matcher.group(3).replaceAll("\"", "").trim();
            telemetryMap.put(key, rawValue);
            LOG.debug("Received telemetry {} -> {}", key, rawValue);
        }
        if(telemetryMap.isEmpty()) {
            throw new IOException("Empty UPS response");
        }
        return telemetryMap;
    }

    @Override
    public void close() throws IOException {
        LOG.info("Closing TCP connection from {}", upsPhysicalName);
        socket.close();
    }

}
