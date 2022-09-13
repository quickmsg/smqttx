<template>
    <standard-table
            :columns="columns"
            :dataSource="dataSource"
            :row-key="(r,i)=>{i.toString()}">
        <template slot="connection" slot-scope="{text,record}">
            <a-tag v-if="record.connection.size===0"> 空</a-tag>
            <a-tag v-else v-for="(v,k) in record.connection" :key="k" style="margin-top: 1px">{{ k }}:{{ v }}</a-tag>
        </template>
        <template slot="will" slot-scope="{text,record}">
            <a-tag v-if="record.will.size===0"> 空</a-tag>
            <a-tag v-else v-for="(v,k) in record.will" :key="k" style="margin-top: 1px">{{ k }}:{{ v }}</a-tag>
        </template>
        <template slot="topics" slot-scope="{text,record}">
            <span v-if="record.topics.size===0"> 空 </span>
            <a-tag v-else v-for="(item,index) in record.topics" :key="index" style="margin-top: 1px">{{item}}</a-tag>
    </template>
    </standard-table>
</template>

<script>
import moment from "moment";
import {connections} from '@/services/smqtt'
import StandardTable from '@/components/table/StandardTable'

const columns = [
        {
            title: 'ID',
            width: '100px',
            customRender: (text, record, index) => index + 1
        },
        {   title: '设备IP',
            dataIndex: 'clientAddress',
            width: "200px"
        },
       {   title: '节点IP',
                dataIndex: 'nodeIp',
                width: "200px"
            },
        {
            title: '设备id',
            dataIndex: 'clientId'
        },
        {
            title: '连接时间',
            dataIndex: 'connectTime'

        },
        {
            title: '是否持久化',
            dataIndex: 'cleanSession',
            customRender: (text, record) => record.sessionPersistent ? "是" : "否"
        },
        {
            title: '用戶名',
            dataIndex: 'auth.username'
        },
        {
            title: '遗嘱消息',
            dataIndex: 'will',
            scopedSlots: {customRender: 'will'},
            width: "100px"
        },
        {
            title: '心跳时间',
            dataIndex: 'keepalive',
        }
    ]
    export default {
        name: "Connections",
        components: {StandardTable},
        data() {
            return {
                columns: columns,
                dataSource: []
            }
        },
        mounted() {
            this.getConnections()
        },
        methods: {
            getConnections() {
                connections().then(res => {
                    this.dataSource = res.data
                })
            }
        }

    }
</script>