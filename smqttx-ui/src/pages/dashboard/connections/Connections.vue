<template>
       <div style="margin-top: 20px">
           <a-form
               layout="inline"
               class="antAdvancedSearchForm"
           >
             <a-form-item label="客户端ID" style="size: 20px">
               <a-input v-model="params.clientId" style="width: 100px" placeholder='请输入客户端ID'/>
             </a-form-item>
             <a-form-item label="客户端IP" style="size: 20px">
               <a-input v-model="params.clientIp" style="width: 100px" placeholder='请输入客户端IP'/>
             </a-form-item>
              <a-form-item label="节点IP" style="size: 20px">
                <a-input v-model="params.nodeIp" style="width: 100px" placeholder='请输入节点IP'/>
              </a-form-item>
             <a-form-item label="" style="size: 20px">
                 <a-button style="width: 80px;margin-left: 20px" @click="getConnections">查询</a-button>
                 <a-button style="width: 80px;margin-left: 20px" @click="reset">重置</a-button>
                 <a-button style="width: 80px;margin-left: 20px" @click="kickConnections">踢出</a-button>
             </a-form-item>

           </a-form>
         </div>

    <standard-table
            :columns="columns"
            :pagination="pagination"
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
import {connections} from '@/services/smqtt'
import StandardTable from '@/components/table/StandardTable'

const columns = [
        {
            title: 'ID',
            customRender: (text, record, index) => index + 1
        },
        {   title: '设备IP',
            dataIndex: 'clientAddress'
        },
       {   title: '节点IP',
                dataIndex: 'nodeIp'
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
            title: '保持会话',
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
            customRender: (text, record) => record.will ?   record.will.willTopic: "无"

          // scopedSlots: {customRender: 'will'}
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
                  params: {
                    clientId: null,
                    pageNumber: 0,
                    pageSize: 20,
                    clientIp: null,
                    nodeIp: null
                  },
                 pagination: {
                     pageSize: 20, // 默认每页显示数量
                     showSizeChanger: true, // 显示可改变每页数量
                     pageSizeOptions: ['10', '20', '30', '40'], // 每页数量选项
                     showTotal: total => `总共 ${total} 条`, // 显示总数
                     onShowSizeChange: (page, pageSize) => {
                       this.pagination.pageSize = pageSize,
                       this.params.pageNumber=page,
                       this.params.pageSize=pageSize
                     }
                   },
                 selectedRowKeys: [],
                 columns: columns,
                 dataSource: null,
                 visible: false,
                 confirmLoading: false,
                 form: {
                     clientId: null,
                     clientIp: null,
                     nodeIp: null

                 }
            }
        },
        mounted() {
            this.getConnections()
        },
        methods: {
            async getConnections() {
                connections(this.params).then(res => {
                    this.dataSource = res.data.content,
                    this.pagination.total = res.data.totalSize
                })
            },
            async kickConnections() {
                  for (const key in this.selectedRowKeys) {
                     let loc = Number((this.params.current - 1) * this.params.pageSize) + Number(this.selectedRowKeys[key])
                     let data = this.dataSource[loc]
                     this.$message.info("deleted:" + res.data);
                      await this.queryActionData()
                      this.selectedRowKeys = []
                  }
            },
            reset() {
                  this.params.action = "ALL"
                  this.params.current = 1
                  this.params.pageSize = 10
                  this.params.subject = null
                  this.params.source = null
                  this.params.aclType = null
                  this.getConnections()
            }
        }

    }
</script>