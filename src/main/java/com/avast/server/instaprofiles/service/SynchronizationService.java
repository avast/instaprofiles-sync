package com.avast.server.instaprofiles.service;

import com.avast.server.instaprofiles.config.properties.AppProperties;
import com.avast.server.instaprofiles.json.NullSerializer;
import com.avast.server.instaprofiles.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vitasek L.
 */
@Service
public class SynchronizationService {

    private final TCInstantProfilesService service;
    private final VcsService vcsService;
    private final SchemaService schemaService;
    private final AppProperties appProperties;

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory()).
            enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);


    private static final Logger logger = LoggerFactory.getLogger(SynchronizationService.class);

    public SynchronizationService(TCInstantProfilesService service, VcsService vcsService,
                                  SchemaService schemaService,
                                  AppProperties appProperties) {
        this.service = service;
        this.vcsService = vcsService;
        this.schemaService = schemaService;
        this.appProperties = appProperties;
    }


    public Mono<String> updateAccounts(Path file, Path publicKey) {
        return service.updateAccounts(file, publicKey).doOnNext((item) -> {
            logger.info("Accounts updated");
        });
    }

    public Mono<SynchronizeResult> doSynchronize(final boolean dryRun, final String profilesYamlFile) {

        final Mono<List<GitProfileInfo>> tcProfiles =
                service.getTCProfilesToGitProfiles().subscribeOn(Schedulers.boundedElastic());
        final Mono<List<GitProfileInfo>> gitProfiles = profilesYamlFile != null ? readProfilesYamlFile(profilesYamlFile) : getGitProfilesConfig().subscribeOn(Schedulers.boundedElastic());

        return gitProfiles.zipWith(tcProfiles, (gitProfileInfos, profileInfos) -> {
            final List<GitProfileInfo> updateList = new ArrayList<>();
            final List<GitProfileInfo> createList = new ArrayList<>();

            logger.info("TC found profiles count {} with listCloudCode {}", profileInfos.size(), appProperties.getListCloudCode());

            gitProfileInfos.forEach(gitProfile -> {
                final Optional<GitProfileInfo> foundTCProfile = profileInfos.stream().
                        filter(profile -> profile.getProjectId().equals(gitProfile.getProjectId()) &&
                                profile.getProfileName().equals(gitProfile.getProfileName())).
                        findAny();
                foundTCProfile.ifPresentOrElse((tcProfile) -> {
                    gitProfile.setProfileId(tcProfile.getProfileId());
                    if (containsUpdateChange(gitProfile, tcProfile)) {
                        updateList.add(gitProfile);
                    }
                }, () -> createList.add(gitProfile));
            });

            final List<GitProfileInfo> removeList = profileInfos.stream().
                    filter(profile ->
                            gitProfileInfos.stream().noneMatch(gitProfile -> profile.getProjectId().equals(gitProfile.getProjectId()) &&
                                    profile.getProfileName().equals(gitProfile.getProfileName()))).
                    collect(Collectors.toList());

            return new SynchronizeResult(createList, updateList, removeList);
        }).flatMap(result -> {
            final SynchronizeResult synchronizeResult = new SynchronizeResult();
            final Mono<List<CloudProfile>> creates = Flux.fromIterable(result.getCreates()).
                    flatMap(create -> service.createProfile(convert(create)).doOnNext(createResult -> {
                        create.setProfileId(createResult.getProfileId());
                        synchronizeResult.getCreates().add(create);
                    })).collectList().defaultIfEmpty(List.of());
            final Mono<List<CloudProfile>> updates = Flux.fromIterable(result.getUpdates()).
                    flatMap(update -> service.updateProfile(convert(update)).doOnNext(updateResult -> {
                        synchronizeResult.getUpdates().add(update);
                    })).collectList().defaultIfEmpty(List.of());
            final Mono<List<String>> removals = Flux.fromIterable(result.getRemovals()).
                    flatMap(removal -> service.removeProfile(convertToRemoveRequest(removal)).doOnNext(removeResult -> {
                        synchronizeResult.getRemovals().add(removal);
                    })).collectList().defaultIfEmpty(List.of());

            logger.info("Plan - Creates #: {} Updates #: {} Removals #: {}", result.getCreates().size(), result.getUpdates().size(), result.getRemovals().size());

            if (!dryRun) {
                return creates.then(updates).then(removals).thenReturn(result);
            }
            return Mono.just(result);
        }).defaultIfEmpty(new SynchronizeResult());
    }

    private void validateSchema(final JsonNode rootNode) {
        try {
            final Set<String> validationErrors = schemaService.validate(rootNode);
            if (!validationErrors.isEmpty()) {
                logger.error("Failed to validate input profiles file: \n" + String.join("\n", validationErrors));
                throw new RuntimeException("Failed to validate input profiles file: \n" + String.join("\n", validationErrors));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateProfiles(List<GitProfileInfo> profiles) {
        final Map<Integer, List<GitProfileInfo>> ids = profiles.stream().collect(Collectors.groupingBy(p -> Objects.hash(p.getProfileName(), p.getProjectId())));

        final List<GitProfileInfo> duplicates = ids.values().stream().filter(gitProfileInfos -> gitProfileInfos.size() > 1).map(items -> items.get(0)).
                collect(Collectors.toList());

        if (!duplicates.isEmpty()) {
            final String dupString = duplicates.stream().map(item -> String.format("ProjectId and ProfileName must be unique - found duplicate for %s %s in profile %s",
                    item.getProjectId(), item.getProfileId(), item.getProfileName())).collect(Collectors.joining("\n"));
            logger.error(dupString);
            throw new IllegalStateException(dupString);
        }
    }

    private Mono<List<GitProfileInfo>> getGitProfilesConfig() {
        final Mono<List<GitProfileInfo>> configProvider = Mono.fromCallable(() -> {
            final GitHub github = vcsService.getGithubs().get(appProperties.getGitVcsId());
            final String gitRepository = appProperties.getGitRepository();
            final String profilesFile = appProperties.getProfilesFile();

            return vcsService.getJsonNodeContent(github, gitRepository, profilesFile).orElseThrow();}).
                    doOnNext(this::validateSchema).
                    map(jsonNode -> vcsService.convertValue(jsonNode, GitProfiles.class)).
                    map(GitProfiles::getProfiles);

        return getProfilesConfig(configProvider);
    }

    private Mono<List<GitProfileInfo>> readProfilesYamlFile(String profilesYamlFile) {
        return getProfilesConfig(Mono.fromCallable(() -> {
            return vcsService.readYaml(new FileInputStream(profilesYamlFile), new TypeReference<JsonNode>() {
            });
        }).doOnNext(this::validateSchema).
                map(jsonNode -> vcsService.convertValue(jsonNode, GitProfiles.class)).
                map(GitProfiles::getProfiles));
    }

    private Mono<List<GitProfileInfo>> getProfilesConfig(Mono<List<GitProfileInfo>> configProvider) {
        return configProvider.doOnNext(this::validateProfiles).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<CloudProfile>> createProfiles(List<GitProfileInfo> newProfiles) {
        return Flux.fromStream(newProfiles.stream()).
                map(this::convert).
                flatMap(service::createProfile).
                collectList().
                subscribeOn(Schedulers.boundedElastic());
    }

    private RemoveCloudProfileRequest convertToRemoveRequest(GitProfileInfo profileInfo) {
        final RemoveCloudProfileRequest removeCloudProfileRequest = new RemoveCloudProfileRequest();
        removeCloudProfileRequest.setProfileId(profileInfo.getProfileId());
        removeCloudProfileRequest.setExtProjectId(profileInfo.getProjectId());
        return removeCloudProfileRequest;
    }

    private CloudProfileRequest convert(final GitProfileInfo profileInfo) {
        final CloudProfileRequest request = new CloudProfileRequest();
        request.setCloudCode(appProperties.getCloudCode());
        request.setProfileId(profileInfo.getProfileId());
        request.setProfileName(profileInfo.getProfileName());
        request.setDescription(profileInfo.getDescription());
        request.setExtProjectId(profileInfo.getProjectId());
        request.setEnabled(profileInfo.getEnabled());
        request.getCustomProfileParameters().putAll(profileInfo.toCustomPropertiesMap());

        request.setImageJson(imageConfigJsonAsYaml(profileInfo));

        if (profileInfo.getvCenterAccount() == null) {
            throw new IllegalStateException("VCenter account ID was not defined: " + profileInfo.getvCenterAccount());
        }

        request.setvCenterAccount(profileInfo.getvCenterAccount());

        return request;
    }

    public void createInitList(String file) {
        final DefaultSerializerProvider.Impl sp = new DefaultSerializerProvider.Impl();
        sp.setNullValueSerializer(new NullSerializer());

        final ObjectMapper mapper = new ObjectMapper(YAMLFactory.builder().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).build());
        mapper.setSerializerProvider(sp);

        service.getTCProfilesToGitProfiles().map(profiles -> {
            final GitProfiles gitProfiles = new GitProfiles();
            gitProfiles.setProfiles(profiles);
            try {
                mapper.writer().writeValue(new File(file), gitProfiles);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return profiles;
        }).then().block();
    }

    private boolean containsUpdateChange(GitProfileInfo gitProfile, GitProfileInfo tcProfile) {
        final boolean enabledEquals = Objects.equals(gitProfile.getEnabled(), tcProfile.getEnabled());
        if (!enabledEquals) {
            logger.info("GitProfile update detected - `enabled` is different P1 {} vs P2 {} - enabled1 = [{}] and enabled2 = [{}]",
                    gitProfile.getIdString(), tcProfile.getIdString(), gitProfile.getEnabled(), tcProfile.getEnabled());
            return true;
        }
        final boolean descriptionEquals = Objects.equals(Optional.ofNullable(gitProfile.getDescription()).orElse(""), Optional.ofNullable(tcProfile.getDescription()).orElse(""));
        if (!descriptionEquals) {
            logger.info("GitProfile update detected - `description` is different P1 {} vs P2 {} - desc1 = [{}] and desc2 = [{}]",
                    gitProfile.getIdString(), tcProfile.getIdString(), gitProfile.getDescription(), tcProfile.getDescription());
            return true;
        }
        final boolean accountEquals = Objects.equals(gitProfile.getvCenterAccount(), tcProfile.getvCenterAccount());
        if (!accountEquals) {
            logger.info("GitProfile update detected - `vCenterAccount` is different P1 {} vs P2 {} - account1 = [{}] and account2 = [{}]",
                    gitProfile.getIdString(), tcProfile.getIdString(), gitProfile.getvCenterAccount(), tcProfile.getvCenterAccount());
            return true;
        }

        final boolean imageConfigJson = Objects.equals(gitProfile.getImageConfigJson(), tcProfile.getImageConfigJson());
        if (!imageConfigJson) {
            logger.info("GitProfile update detected - `imageConfigJson` is different P1 {} vs P2 {} - imageConfigJson = [{}] and imageConfigJson = [{}]",
                    gitProfile.getIdString(), tcProfile.getIdString(), imageConfigJsonAsYaml(gitProfile), imageConfigJsonAsYaml(tcProfile));
            return true;
        }

        final boolean terminateConditionsEquals = Objects.equals(gitProfile.getTerminateConditions(), tcProfile.getTerminateConditions());
        if (!terminateConditionsEquals) {
            logger.info("GitProfile update detected - `terminateConditions` is different P1 {} vs P2 {} - terminateConditions1 = [{}] and terminateConditions2 = [{}]",
                    gitProfile.getIdString(), tcProfile.getIdString(), gitProfile.getTerminateConditions(), tcProfile.getTerminateConditions());
            return true;
        }

        return false;
    }

    private String imageConfigJsonAsYaml(GitProfileInfo tcProfile)  {
        try {
            return yamlMapper.writeValueAsString(tcProfile.getImageConfigJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
