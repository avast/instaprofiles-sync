package com.avast.server.instaprofiles.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Vitasek L.
 */
public class SynchronizeResult {
    private List<GitProfileInfo> creates = new ArrayList<>();
    private List<GitProfileInfo> updates = new ArrayList<>();
    private List<GitProfileInfo> removals = new ArrayList<>();

    public SynchronizeResult() {
    }

    public SynchronizeResult(List<GitProfileInfo> creates, List<GitProfileInfo> updates, List<GitProfileInfo> removals) {
        this.creates = creates;
        this.updates = updates;
        this.removals = removals;
    }

    public List<GitProfileInfo> getCreates() {
        return creates;
    }

    public void setCreates(List<GitProfileInfo> creates) {
        this.creates = creates;
    }

    public List<GitProfileInfo> getUpdates() {
        return updates;
    }

    public void setUpdates(List<GitProfileInfo> updates) {
        this.updates = updates;
    }

    public List<GitProfileInfo> getRemovals() {
        return removals;
    }

    public void setRemovals(List<GitProfileInfo> removals) {
        this.removals = removals;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SynchronizeResult.class.getSimpleName() + "[", "]")
                .add("creates=" + creates)
                .add("updates=" + updates)
                .add("removals=" + removals)
                .toString();
    }

    public String toYamlString(boolean isDryRun) {
        final ObjectMapper mapper = new ObjectMapper(YAMLFactory.builder().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).build());
        final StringJoiner joiner = new StringJoiner("\n", "", "");
        if (isDryRun) {
            joiner.add("Result of DRY RUN only!");
        }
        try {
            joiner.add("PROFILE CREATES:").add(addCollection(mapper, creates)).add("----------------");
            joiner.add("PROFILE UPDATES:").add(addCollection(mapper, updates)).add("----------------");;
            joiner.add("PROFILE REMOVALS:").add(addCollection(mapper, removals)).add("---------------");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize synchronize result into YAML", e);
        }
        return joiner.toString();
    }

    private String addCollection(ObjectMapper mapper, List<GitProfileInfo> collection) throws JsonProcessingException {
        return !CollectionUtils.isEmpty(collection) ? mapper.writeValueAsString(collection) : "- NO CHANGES";
    }
}
