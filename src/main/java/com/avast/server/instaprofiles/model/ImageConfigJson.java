package com.avast.server.instaprofiles.model;

import com.avast.server.instaprofiles.utils.CompareUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Vitasek L.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageConfigJson {
    private String template;
    private String instanceFolder;
    private String resourcePool;
    private Integer maxInstances;
    // can be number (pool id) or string (pool name)
    private Object agentPool;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> network = List.of();
    private Integer shutdownTimeout;


    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getInstanceFolder() {
        return instanceFolder;
    }

    public void setInstanceFolder(String instanceFolder) {
        this.instanceFolder = instanceFolder;
    }

    public String getResourcePool() {
        return resourcePool;
    }

    public void setResourcePool(String resourcePool) {
        this.resourcePool = resourcePool;
    }

    public Integer getMaxInstances() {
        return maxInstances;
    }

    public void setMaxInstances(Integer maxInstances) {
        this.maxInstances = maxInstances;
    }

    public Object getAgentPool() {
        return agentPool;
    }

    public void setAgentPool(Object agentPool) {
        this.agentPool = agentPool;
    }

    public List<String> getNetwork() {
        return network;
    }

    public void setNetwork(List<String> network) {
        this.network = network;
    }

    public Integer getShutdownTimeout() {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(Integer shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageConfigJson)) return false;
        ImageConfigJson that = (ImageConfigJson) o;
        return Objects.equals(template, that.template) && Objects.equals(instanceFolder, that.instanceFolder) && Objects.equals(resourcePool, that.resourcePool) &&
                Objects.equals(maxInstances, that.maxInstances) && Objects.equals(agentPool, that.agentPool) && CompareUtils.listEqualsIgnoreOrder(network, that.network) &&
                Objects.equals(shutdownTimeout, that.shutdownTimeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, instanceFolder, resourcePool, maxInstances, agentPool, network, shutdownTimeout);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ImageConfigJson.class.getSimpleName() + "[", "]")
                .add("template='" + template + "'")
                .add("instanceFolder='" + instanceFolder + "'")
                .add("resourcePool='" + resourcePool + "'")
                .add("maxInstances=" + maxInstances)
                .add("agentPool=" + agentPool)
                .add("network=" + network)
                .add("shutdownTimeout=" + shutdownTimeout)
                .toString();
    }
}
