package com.avast.server.instaprofiles.config.properties;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Vitasek L.
 */
public class VCenterAccount {
    private String id;
    private String url;
    private String username;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VCenterAccount)) return false;
        VCenterAccount that = (VCenterAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(url, that.url) && Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, username, password);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VCenterAccount.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("url='" + url + "'")
                .add("username='" + username + "'")
                .add("password='***HIDDEN***'")
                .toString();
    }
}
