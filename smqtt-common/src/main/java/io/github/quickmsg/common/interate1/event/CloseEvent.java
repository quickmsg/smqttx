package io.github.quickmsg.common.interate1.event;

import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.events.Event;
import org.apache.ignite.lang.IgniteUuid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author luxurong
 */
public class CloseEvent implements Event {
    @Override
    public IgniteUuid id() {
        return null;
    }

    @Override
    public long localOrder() {
        return 0;
    }

    @Override
    public ClusterNode node() {
        return null;
    }

    @Override
    public @Nullable String message() {
        return null;
    }

    @Override
    public int type() {
        return 0;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public long timestamp() {
        return 0;
    }

    @Override
    public String shortDisplay() {
        return null;
    }

    @Override
    public int compareTo(@NotNull Event o) {
        return 0;
    }
}
