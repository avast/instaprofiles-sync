package com.avast.server.instaprofiles.model;

import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Vitasek L.
 */
public class CloudProfile {
    private String profileId;
    private String projectId;
    private String cloudCode;
    private String profileName;
    private String description;
    private String vCenterAccount;
    private Long terminateIdleTime;
    private Boolean enabled;
    private Map<String, String> profileProperties;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCloudCode() {
        return cloudCode;
    }

    public void setCloudCode(String cloudCode) {
        this.cloudCode = cloudCode;
    }

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

    public Long getTerminateIdleTime() {
        return terminateIdleTime;
    }

    public void setTerminateIdleTime(Long terminateIdleTime) {
        this.terminateIdleTime = terminateIdleTime;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getProfileProperties() {
        return profileProperties;
    }

    public void setProfileProperties(Map<String, String> profileProperties) {
        this.profileProperties = profileProperties;
    }

    public String getvCenterAccount() {
        return vCenterAccount;
    }

    public void setvCenterAccount(String vCenterAccount) {
        this.vCenterAccount = vCenterAccount;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CloudProfile.class.getSimpleName() + "[", "]")
                .add("profileId='" + profileId + "'")
                .add("projectId='" + projectId + "'")
                .add("cloudCode='" + cloudCode + "'")
                .add("profileName='" + profileName + "'")
                .add("description='" + description + "'")
                .add("vCenterAccount='" + vCenterAccount + "'")
                .add("terminateIdleTime=" + terminateIdleTime)
                .add("enabled=" + enabled)
                .add("profileProperties=" + profileProperties)
                .toString();
    }
}
