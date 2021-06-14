package com.avast.server.instaprofiles.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Vitasek L.
 */
public class CloudProfileRequest {
    private String cloudCode = CloudProfileConsts.CLOUD_CODE;
    private String extProjectId;
    private String profileId;
    private String profileName;
    private String description;
    private Boolean enabled = true;
    private Long terminateIdleTime = TimeUnit.MINUTES.toMillis(30);
    private Map<String, String> customProfileParameters = new HashMap<>();
    private String vCenterAccount;

    public String getCloudCode() {
        return cloudCode;
    }

    public void setCloudCode(String cloudCode) {
        this.cloudCode = cloudCode;
    }

    public String getExtProjectId() {
        return extProjectId;
    }

    public void setExtProjectId(String extProjectId) {
        this.extProjectId = extProjectId;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getTerminateIdleTime() {
        return terminateIdleTime;
    }

    public void setTerminateIdleTime(Long terminateIdleTime) {
        this.terminateIdleTime = terminateIdleTime;
    }

    public Map<String, String> getCustomProfileParameters() {
        return customProfileParameters;
    }

    public void setCustomProfileParameters(Map<String, String> customProfileParameters) {
        this.customProfileParameters = customProfileParameters;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }


    public String getvCenterAccount() {
        return vCenterAccount;
    }

    public void setvCenterAccount(String vCenterAccount) {
        this.vCenterAccount = vCenterAccount;
    }

    public void setImageJson(String imageConfigJson) {
        customProfileParameters.put(CloudProfileConsts.PROP_IMAGES, imageConfigJson);
    }

}
