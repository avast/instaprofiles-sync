package com.avast.server.instaprofiles.model;

import java.util.*;

/**
 * @author Vitasek L.
 */
public class ProfileInfo {
    private String projectName;
    private String projectExtId;
    private String profileId;
    private String profileName;
    private String profileDescription;
    private String sdkUrl;
    private Map<String, String> profileParameters = Collections.emptyMap();
    private List<String> templates;
    private boolean profileEnabled;
    private Map<String, ImageConfigJson> imageConfigJson;
    private String vcenterAccount;

    public String getVcenterAccount() {
        return vcenterAccount;
    }

    public void setVcenterAccount(String vcenterAccount) {
        this.vcenterAccount = vcenterAccount;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectExtId() {
        return projectExtId;
    }

    public void setProjectExtId(String projectExtId) {
        this.projectExtId = projectExtId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getSdkUrl() {
        return sdkUrl;
    }

    public void setSdkUrl(String sdkUrl) {
        this.sdkUrl = sdkUrl;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

    public Map<String, ImageConfigJson> getImageConfigJson() {
        return imageConfigJson;
    }

    public void setImageConfigJson(Map<String, ImageConfigJson> imageConfigJson) {
        this.imageConfigJson = imageConfigJson;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public Map<String, String> getProfileParameters() {
        return profileParameters;
    }

    public void setProfileParameters(Map<String, String> profileParameters) {
        this.profileParameters = profileParameters;
    }

    public boolean isProfileEnabled() {
        return profileEnabled;
    }

    public void setProfileEnabled(boolean profileEnabled) {
        this.profileEnabled = profileEnabled;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ProfileInfo.class.getSimpleName() + "[", "]")
                .add("projectName='" + projectName + "'")
                .add("projectExtId='" + projectExtId + "'")
                .add("profileId='" + profileId + "'")
                .add("profileName='" + profileName + "'")
                .add("profileEnabled='" + profileEnabled + "'")
                .add("profileDescription='" + profileDescription + "'")
                .add("sdkUrl='" + sdkUrl + "'")
                .add("profileParameters=" + profileParameters)
                .add("templates=" + templates)
                .add("vCenterAccount=" + vcenterAccount)
                .add("imageConfigJson=" + imageConfigJson)
                .toString();
    }
}
