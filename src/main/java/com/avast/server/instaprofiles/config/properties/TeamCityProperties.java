package com.avast.server.instaprofiles.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Vitasek L.
 */
@ConfigurationProperties("app.teamcity")
@Component
public class TeamCityProperties {
    private String token;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
