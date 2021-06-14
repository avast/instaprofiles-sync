package com.avast.server.instaprofiles.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vitasek L.
 */
public class GitProfileInfo {
    private String profileName;
    private String description;
    private String projectId;
    private String vCenterAccount;
    private Boolean enabled = true;
    private TerminateConditions terminateConditions = new TerminateConditions();

    @JsonIgnore
    private String profileId;

    private Map<String, ImageConfigJson> imageConfigJson = Collections.emptyMap();

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getvCenterAccount() {
        return vCenterAccount;
    }

    public void setvCenterAccount(String vCenterAccount) {
        this.vCenterAccount = vCenterAccount;
    }

    public Map<String, ImageConfigJson> getImageConfigJson() {
        return imageConfigJson;
    }

    public void setImageConfigJson(Map<String, ImageConfigJson> imageConfigJson) {
        this.imageConfigJson = imageConfigJson;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public TerminateConditions getTerminateConditions() {
        return terminateConditions;
    }

    public void setTerminateConditions(TerminateConditions terminateConditions) {
        this.terminateConditions = terminateConditions;
    }

    @JsonIgnore
    public String getIdString() {
        return String.format("[ProfileId = %s, ProfileName = %s, ProjectId = %s]", profileId, profileName, projectId);
    }

    @JsonIgnore
    public Map<String, String> toCustomPropertiesMap() {
        final Map<String, String> map = new HashMap<>();
        map.putAll(terminateConditions.toCustomPropertiesMap());
        return map;
    }

}
