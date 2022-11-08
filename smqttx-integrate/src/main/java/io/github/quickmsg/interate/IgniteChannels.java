package io.github.quickmsg.interate;

import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.SqlBuilder;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.integrate.IgniteCacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.cache.ConnectCache;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.job.CloseJob;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.sql.ConnectionQueryModel;
import io.github.quickmsg.common.sql.PageRequest;
import io.github.quickmsg.common.sql.PageResult;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.netty.handler.codec.mqtt.MqttVersion;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author luxurong
 */
public class IgniteChannels implements IntegrateChannels {


    private final ConcurrentHashMap<String, MqttChannel> localChannelCache;

    private final IntegrateCache<Integer, ConnectCache> shareChannelCache;

    private final IgniteIntegrate integrate;

    private final CloseJob closeConnectJob = new CloseJob();

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }

    public IgniteChannels(IgniteIntegrate integrate, ConcurrentHashMap<String, MqttChannel> channelMap) {
        this.integrate = integrate;
        this.localChannelCache = channelMap;
        this.shareChannelCache = this.integrate.getCache(IgniteCacheRegion.CHANNEL);
    }


    @Override
    public void add(String clientIdentifier, MqttChannel mqttChannel) {
        Collection<Boolean> closeJobs = integrate.getJobExecutor().callBroadcast(this.closeConnectJob, clientIdentifier);
        localChannelCache.put(clientIdentifier, mqttChannel);
        this.shareChannelCache.put(mqttChannel.getId(), mqttChannel.getConnectCache());
    }

    @Override
    public boolean exists(String clientIdentifier) {
        return localChannelCache.containsKey(clientIdentifier);
    }

    @Override
    public MqttChannel get(String clientIdentifier) {
        return localChannelCache.get(clientIdentifier);
    }

    @Override
    public Integer counts() {
        return localChannelCache.size();
    }

    @Override
    public Collection<MqttChannel> getChannels() {
        return localChannelCache.values();
    }

    @Override
    public void remove(MqttChannel mqttChannel) {
        localChannelCache.remove(mqttChannel.getClientId(), mqttChannel);
        shareChannelCache.remove(mqttChannel.getId());
    }

    @Override
    public PageResult<ConnectCache> queryConnectionSql(ConnectionQueryModel model) {
        List<Condition> conditionList = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        List<Object> countParams = new ArrayList<>();

        if (model.getClientId() != null) {
            Condition condition = new Condition();
            condition.setField("clientId");
            condition.setOperator("=");
            condition.setValue("clientId");
            params.add(model.getClientId());
            countParams.add(model.getClientId());
            conditionList.add(condition);
        }
        if (model.getClientIp() != null) {
            Condition condition = new Condition();
            condition.setField("clientAddress");
            condition.setOperator("=");
            condition.setValue("clientAddress");
            params.add(model.getClientIp());
            countParams.add(model.getClientIp());

            conditionList.add(condition);
        }

        if (model.getNodeIp() != null) {
            Condition condition = new Condition();
            condition.setField("nodeIp");
            condition.setOperator("=");
            condition.setValue("nodeIp");
            params.add(model.getNodeIp());
            countParams.add(model.getNodeIp());
            conditionList.add(condition);
        }
        params.add(model.getPageSize());
        params.add(model.getPageNumber() * model.getPageSize());
        Condition[] cons = conditionList.toArray(new Condition[0]);
        Object[] objects = params.toArray(new Object[0]);
        String sql = SqlBuilder.create().select().from("ConnectCache").where(cons).append(" limit ? offset ?").build();
        String countSql = SqlBuilder.create().select("COUNT(*)").from("ConnectCache").where(cons).build();

        SqlFieldsQuery query = new SqlFieldsQuery(sql).setArgs(objects);

        SqlFieldsQuery countQuery = new SqlFieldsQuery(countSql).setArgs(countParams.toArray(new Object[0]));

        PageResult<ConnectCache> pageResult = new PageResult<>();
        pageResult.setPageNumber(model.getPageNumber());
        pageResult.setPageSize(model.getPageSize());
        try (QueryCursor<List<?>> cursor = shareChannelCache.getOriginCache().query(query)) {
            List<ConnectCache> connectCaches = new ArrayList<>();
            for (List<?> row : cursor) {
                ConnectCache cache = new ConnectCache();
                cache.setClientId(String.valueOf(row.get(0)));
                cache.setClientAddress(String.valueOf(row.get(1)));
                cache.setNodeIp(String.valueOf(row.get(2)));
                cache.setVersion(MqttVersion.valueOf(String.valueOf(row.get(3))));
                cache.setKeepalive((Integer) row.get(4));
                cache.setCleanSession(Boolean.parseBoolean(String.valueOf(row.get(5))));
                cache.setConnectTime(String.valueOf(row.get(6)));
                cache.setAuth((MqttChannel.Auth) row.get(7));
                cache.setWill((MqttChannel.Will) row.get(8));
                connectCaches.add(cache);
            }
            int total = 0 ;
            try (QueryCursor<List<?>> countCursor = shareChannelCache.getOriginCache().query(countQuery)){
                for (List<?> row : countCursor) {
                    total = ((Long)row.get(0)).intValue();
                    break;
                }
            }
            pageResult.setContent(connectCaches);
            pageResult.setTotalSize(total);
            int pages = total % model.getPageSize() == 0 ? total / model.getPageSize() : total / model.getPageSize() + 1;
            pageResult.setTotalPages(pages);
        } catch (Exception e) {
            ContextHolder.getReceiveContext().getLogManager()
                        .printError(null, LogEvent.SYSTEM, e.getMessage());
        }
        return pageResult;
    }
}
