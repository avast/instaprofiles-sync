package com.avast.server.instaprofiles.service;

import com.avast.server.instaprofiles.config.properties.AppProperties;
import com.avast.server.instaprofiles.config.properties.Vcs;
import com.avast.server.instaprofiles.config.properties.VcsTypeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import okhttp3.OkHttpClient;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Vitasek L.
 */
@Service
public class VcsService {

    private static final Logger logger = LoggerFactory.getLogger(VcsService.class);
    private final Map<String, GitHub> githubs = new HashMap<>();
    private ObjectMapper yamlMapper;
    private AppProperties appProperties;
    private Environment env;

    public VcsService() {
    }

    @Autowired
    public VcsService(AppProperties appProperties, Environment env) {
        this.appProperties = appProperties;
        this.env = env;
        yamlMapper = new ObjectMapper(new YAMLFactory());
        yamlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        appProperties.getVcs().entrySet().stream().filter(entry -> entry.getValue().getType() == VcsTypeEnum.GITHUB).
                forEach(vcs -> {
                    try {
                        final OkHttpClient client = getGithubClient();
                        final GitHub github = GitHubBuilder.fromProperties(new Properties()).
                                withConnector(new OkHttpConnector(client)).
                                withEndpoint(vcs.getValue().getApiUrl()).
                                withOAuthToken(getToken(vcs.getValue())).build();
                        if (!github.isCredentialValid()) {
                            throw new IllegalStateException("Github credentials are not valid for " + vcs.getKey());
                        }
                        githubs.put(vcs.getKey(), github);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @NotNull
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private OkHttpClient getGithubClient() throws IOException {
//        final File cacheDir = new File(FileUtils.getTempDirectory(), "skyringAppCache");
//        cacheDir.mkdirs();
//        FileUtils.cleanDirectory(cacheDir);
        return new OkHttpClient().newBuilder().build();
    }

    private String getToken(Vcs vcs) {
        if (vcs.getToken() != null) {
            return vcs.getToken();
        }
        if (vcs.getTokenEnv() != null) {
            return env.getProperty(vcs.getTokenEnv());
        }
        return null;
    }

    public <T> T convertValue(JsonNode node, Class<T> type) {
        return yamlMapper.convertValue(node, type);
    }

    public Map<String, GitHub> getGithubs() {
        return githubs;
    }

    public <T> T getFileContent(GitHub github, String repository, String file, TypeReference<T> type) {
        final GHContent fileContent;
        try {
            fileContent = getFileContent(github, repository, file);
            if (fileContent.isFile()) {
                return readYaml(fileContent.read(), type);
            }
            return null;
        } catch (IOException e) {
            logger.error("Failed to load file", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T readYaml(InputStream inputStream, TypeReference<T> type) {
        try (InputStreamReader is = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return yamlMapper.readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GHContent getFileContent(final GitHub github, final String repository, final String file) throws IOException {
        try {
            return github.getRepository(repository).getFileContent(file);
        } catch (GHFileNotFoundException e) {
            throw new GHFileNotFoundException("File " + e.getMessage() + " was not found in Git repository");
        }
    }

    public Optional<JsonNode> getJsonNodeContent(GitHub github, String repository, String file) {
        final GHContent fileContent;
        try {
            fileContent = getFileContent(github, repository, file);
            if (fileContent.isFile()) {
                try (InputStreamReader is = new InputStreamReader(fileContent.read(), StandardCharsets.UTF_8)) {
                    return Optional.of(yamlMapper.readTree(is));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load file", e);
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }



    public <T> Optional<T> getFile(String githubId, String vcsFile, Class<T> type, String repository) {
        final GitHub gitHub = getGithubs().get(githubId);

        final GHContent fileContent;
        try {
            fileContent = gitHub.getRepository(repository).getFileContent(vcsFile);
            if (fileContent.isFile()) {
                try {
                    return Optional.of(yamlMapper.readValue(IOUtils.toString(fileContent.read(), StandardCharsets.UTF_8.name()), type));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load file", e);
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


}
