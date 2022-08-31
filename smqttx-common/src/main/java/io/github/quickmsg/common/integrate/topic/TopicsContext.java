package io.github.quickmsg.common.integrate.topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author luxurong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicsContext {
    /**
     * cluster id
     */
    private String clusterUuid;

    /**
     * channel id
     */
    private String channelId;

    /**
     * subscribe timestamp
     */
    private Long timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicsContext that = (TopicsContext) o;
        return Objects.equals(clusterUuid, that.clusterUuid) && Objects.equals(channelId, that.channelId) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterUuid, channelId, timestamp);
    }
}
