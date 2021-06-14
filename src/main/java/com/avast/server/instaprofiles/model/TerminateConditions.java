package com.avast.server.instaprofiles.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

/**
 * @author Vitasek L.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TerminateConditions {
    // After certain work time X (minutes)
    private Integer totalWorkTime = null;
    //  If idle, stop X minutes before the full hour
    private Integer nextHour = null;
    // After the first build
    private Boolean terminateAfterBuild;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TerminateConditions)) return false;
        TerminateConditions that = (TerminateConditions) o;
        return Objects.equals(totalWorkTime, that.totalWorkTime) && Objects.equals(nextHour, that.nextHour) && Objects.equals(terminateAfterBuild, that.terminateAfterBuild);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalWorkTime, nextHour, terminateAfterBuild);
    }

    public Integer getTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(Integer totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public Integer getNextHour() {
        return nextHour;
    }

    public void setNextHour(Integer nextHour) {
        this.nextHour = nextHour;
    }

    public Boolean getTerminateAfterBuild() {
        return terminateAfterBuild;
    }

    public void setTerminateAfterBuild(Boolean terminateAfterBuild) {
        this.terminateAfterBuild = terminateAfterBuild;
    }

    public Map<String, String> toCustomPropertiesMap() {
        final Map<String, String> map = new HashMap<>();
        Optional.ofNullable(totalWorkTime).ifPresent(val -> map.put("total-work-time", String.valueOf(val)));
        Optional.ofNullable(nextHour).ifPresent(val -> map.put("next-hour", String.valueOf(val)));
        Optional.ofNullable(terminateAfterBuild).ifPresent(val -> map.put("terminate-after-build", String.valueOf(val)));

        return map;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "", "")
                .add("totalWorkTime=" + totalWorkTime)
                .add("nextHour=" + nextHour)
                .add("terminateAfterBuild=" + terminateAfterBuild)
                .toString();
    }
}
