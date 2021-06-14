package com.avast.server.instaprofiles.model;

/**
 * @author Vitasek L.
 */
public class RemoveCloudProfileRequest {
    private String extProjectId;
    private String profileId;

    public String getExtProjectId() {
        return extProjectId;
    }

    public void setExtProjectId(String extProjectId) {
        this.extProjectId = extProjectId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
