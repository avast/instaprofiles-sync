package com.avast.server.instaprofiles.service;

import com.avast.server.instaprofiles.config.properties.AppProperties;
import com.avast.server.instaprofiles.model.*;
import com.avast.server.instaprofiles.utils.AESUtil;
import com.avast.server.instaprofiles.utils.RSAUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * @author Vitasek L.
 */
@Service
public class TCInstantProfilesService {

    private static final Logger logger = LoggerFactory.getLogger(TCInstantProfilesService.class);
    private final AppProperties appProperties;
    private final WebClient tcWebClient;

    public TCInstantProfilesService(AppProperties appProperties, WebClient tcWebClient) {
        this.appProperties = appProperties;
        this.tcWebClient = tcWebClient;
    }

    public Mono<List<GitProfileInfo>> getTCProfilesToGitProfiles() {
        return getTcProfilesList().
                map(this::convert).collectList();
    }

    @NotNull
    private Flux<ProfileInfo> getTcProfilesList() {
        return tcWebClient.get().uri("/app/{vmic}/list?cloudCode={listCloudCode}", appProperties.getWebCloudCode(), appProperties.getListCloudCode()).
                accept(MediaType.APPLICATION_JSON).
                retrieve().
                bodyToFlux(ProfileInfo.class).
                doOnError(t -> t instanceof WebClientResponseException, t -> logger.error("Failed to list profiles {}", ((WebClientResponseException) t).getResponseBodyAsString()));
    }

    public Mono<CloudProfile> createProfile(CloudProfileRequest cloudProfileRequest) {
        return tcWebClient.post().uri("/app/{vmic}/create", appProperties.getWebCloudCode()).
                bodyValue(cloudProfileRequest).
                accept(MediaType.APPLICATION_JSON).
                retrieve().
                bodyToMono(CloudProfile.class).
                doOnError(t -> t instanceof WebClientResponseException, t -> logger.error("Failed to create profile {}", ((WebClientResponseException) t).getResponseBodyAsString()));
    }

    public Mono<CloudProfile> updateProfile(CloudProfileRequest cloudProfileRequest) {
        return tcWebClient.post().uri("/app/{vmic}/update?clean={}", appProperties.getWebCloudCode(), appProperties.isCleanImageParametersOnUpdate()).
                bodyValue(cloudProfileRequest).
                accept(MediaType.APPLICATION_JSON).
                retrieve().
                bodyToMono(CloudProfile.class).
                doOnError(t -> t instanceof WebClientResponseException, t -> logger.error("Failed to update profile {}", ((WebClientResponseException) t).getResponseBodyAsString()));
    }

    public Mono<String> removeProfile(RemoveCloudProfileRequest request) {
        return tcWebClient.post().uri("/app/{vmic}/remove", appProperties.getWebCloudCode()).
                bodyValue(request).
                accept(MediaType.APPLICATION_JSON).
                retrieve().
                bodyToMono(String.class).
                doOnError(t -> t instanceof WebClientResponseException, t -> logger.error("Failed to remove profile {}", ((WebClientResponseException) t).getResponseBodyAsString()));
    }

    public Mono<String> updateAccounts(AccountsUpdateRequest accountsUpdateRequest) {
        return tcWebClient.post().uri("/app/{vmic}/accounts/update", appProperties.getWebCloudCode()).
                bodyValue(accountsUpdateRequest).
                accept(MediaType.APPLICATION_JSON).
                retrieve().
                bodyToMono(String.class).
                doOnError(t -> t instanceof WebClientResponseException, t -> logger.error("Failed to update accounts {}", ((WebClientResponseException) t).getResponseBodyAsString()));
    }


    public Mono<String> updateAccounts(Path file, Path publicKeyPath) {
        return Mono.fromCallable(() -> {
            final PublicKey pk = RSAUtil.INSTANCE.getPublicKeyPem(Files.readAllBytes(publicKeyPath));
            final Key aesKey = AESUtil.INSTANCE.generateAESKey();

            final String aes256enc = encodeBase64String(RSAUtil.INSTANCE.encrypt(aesKey.getEncoded(), pk));
            final String s = encodeBase64String(AESUtil.INSTANCE.encrypt(Files.readAllBytes(file), aesKey));

            return new AccountsUpdateRequest(s, aes256enc);

        }).flatMap(this::updateAccounts);
    }

    private GitProfileInfo convert(ProfileInfo profileInfo) {
        final GitProfileInfo gitProfileInfo = new GitProfileInfo();
        gitProfileInfo.setProfileName(profileInfo.getProfileName());
        gitProfileInfo.setProfileId(profileInfo.getProfileId());
        gitProfileInfo.setDescription(profileInfo.getProfileDescription());
        gitProfileInfo.setProjectId(profileInfo.getProjectExtId());
        gitProfileInfo.setEnabled(profileInfo.isProfileEnabled());

        gitProfileInfo.setvCenterAccount(profileInfo.getVcenterAccount());

        gitProfileInfo.setImageConfigJson(profileInfo.getImageConfigJson());

        Optional.ofNullable(profileInfo.getProfileParameters().get("total-work-time")).filter(StringUtils::isNotEmpty).
                ifPresent(val -> {
            gitProfileInfo.getTerminateConditions().setTotalWorkTime(Integer.valueOf(val));
        });
        Optional.ofNullable(profileInfo.getProfileParameters().get("next-hour")).filter(StringUtils::isNotEmpty).ifPresent(val -> {
            gitProfileInfo.getTerminateConditions().setNextHour(Integer.valueOf(val));
        });
        Optional.ofNullable(profileInfo.getProfileParameters().get("terminate-after-build")).ifPresent(val -> {
            gitProfileInfo.getTerminateConditions().setTerminateAfterBuild(Boolean.valueOf(val));
        });


        return gitProfileInfo;
    }


}
