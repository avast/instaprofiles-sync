package com.avast.server.instaprofiles.config.properties;

public class Vcs {
    private String url;
    private String apiUrl;
    private VcsTypeEnum type;
    private String token;
    private String tokenEnv;

    public String getTokenEnv() {
        return tokenEnv;
    }

    public void setTokenEnv(String tokenEnv) {
        this.tokenEnv = tokenEnv;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public VcsTypeEnum getType() {
        return type;
    }

    public void setType(VcsTypeEnum type) {
        this.type = type;
    }
}
