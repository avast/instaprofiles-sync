package com.avast.server.instaprofiles.config.properties;

import com.avast.server.instaprofiles.model.CloudProfileConsts;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

/**
 * @author Vitasek L.
 */
@ConfigurationProperties("app")
@Component
public class AppProperties {
    private String gitVcsId;
    private String gitRepository;
    private String profilesFile;
    private Resource schemaFile;
    private boolean cleanImageParametersOnUpdate;
    private String listCloudCode = CloudProfileConsts.CLOUD_CODE;
    private String webCloudCode = "vmic";
    private String cloudCode = CloudProfileConsts.CLOUD_CODE;

    public Resource getSchemaFile() {
        return schemaFile;
    }

    public void setSchemaFile(Resource schemaFile) {
        this.schemaFile = schemaFile;
    }

    private LinkedHashMap<String, Vcs> vcs = new LinkedHashMap<>();

    public LinkedHashMap<String, Vcs> getVcs() {
        return vcs;
    }

    public void setVcs(LinkedHashMap<String, Vcs> vcs) {
        this.vcs = vcs;
    }

    public String getGitVcsId() {
        return gitVcsId;
    }

    public void setGitVcsId(String gitVcsId) {
        this.gitVcsId = gitVcsId;
    }

    public String getGitRepository() {
        return gitRepository;
    }

    public void setGitRepository(String gitRepository) {
        this.gitRepository = gitRepository;
    }

    public String getProfilesFile() {
        return profilesFile;
    }

    public void setProfilesFile(String profilesFile) {
        this.profilesFile = profilesFile;
    }

    public boolean isCleanImageParametersOnUpdate() {
        return cleanImageParametersOnUpdate;
    }

    public void setCleanImageParametersOnUpdate(boolean cleanImageParametersOnUpdate) {
        this.cleanImageParametersOnUpdate = cleanImageParametersOnUpdate;
    }

    public String getListCloudCode() {
        return listCloudCode;
    }

    public void setListCloudCode(String listCloudCode) {
        this.listCloudCode = listCloudCode;
    }

    public String getCloudCode() {
        return cloudCode;
    }

    public void setCloudCode(String cloudCode) {
        this.cloudCode = cloudCode;
    }

    public String getWebCloudCode() {
        return webCloudCode;
    }

    public void setWebCloudCode(String webCloudCode) {
        this.webCloudCode = webCloudCode;
    }
}
