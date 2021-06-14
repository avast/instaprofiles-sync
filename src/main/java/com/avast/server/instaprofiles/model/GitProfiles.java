package com.avast.server.instaprofiles.model;

import java.util.List;

/**
 * @author Vitasek L.
 */
public class GitProfiles {
    private List<GitProfileInfo> profiles = List.of();

    public List<GitProfileInfo> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<GitProfileInfo> profiles) {
        this.profiles = profiles;
    }
}
