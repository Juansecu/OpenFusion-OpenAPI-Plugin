package com.juansecu.openfusion.openfusionopenapiplugin.shared.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class HostDetailsProvider {
    @Value("${PUBLIC_HOST_ADDRESS:localhost}")
    private String publicHostAddress;
    @Value("${SHOULD_INCLUDE_SERVER_PORT:true}")
    private boolean shouldIncludeServerPort;
    @Value("${server.port}")
    private int port;

    public String getHostAddress() {
        return this.publicHostAddress.trim();
    }

    public String getHostPath() {
        return "https://" +
            this.getHostAddress() +
            (
                this.shouldIncludeServerPort
                    ? ":" + this.getServerPort()
                    : ""
            );
    }

    public int getServerPort() {
        return this.port;
    }
}
