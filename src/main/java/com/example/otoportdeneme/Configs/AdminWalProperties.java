package com.example.otoportdeneme.Configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.wal")
public class AdminWalProperties {
    private boolean enabled = true;
    private boolean captureBodies = true;
    private int maxBodyChars = 6000;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isCaptureBodies() { return captureBodies; }
    public void setCaptureBodies(boolean captureBodies) { this.captureBodies = captureBodies; }

    public int getMaxBodyChars() { return maxBodyChars; }
    public void setMaxBodyChars(int maxBodyChars) { this.maxBodyChars = maxBodyChars; }
}
