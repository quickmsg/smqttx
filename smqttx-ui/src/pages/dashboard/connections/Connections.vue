<template>
  <div style="margin-top: 20px">
    <a-form
        layout="inline"
        class="antAdvancedSearchForm"
    >
      <a-form-item label="设备id" style="size: 20px">
        <a-input v-model="params.clientId" style="width: 150px" placeholder='请输入客户端id'/>
      </a-form-item>
      <a-form-item label="客户端ip" style="size: 20px">
        <a-input v-model="params.clientIp" style="width: 150px" placeholder='请输入客户端ip'/>
      </a-form-item>
      <a-form-item label="节点ip" style="size: 20px">
        <a-input v-model="params.nodeIp" style="width: 150px" placeholder='请输入节点ip'/>
      </a-form-item>
      <a-form-item>
        <a-button style="width: 80px;margin-left: 20px" @click="getConnections">查询</a-button>
        <a-button style="width: 80px;margin-left: 20px" @click="reset">重置</a-button>
        <a-button style="width: 80px;margin-left: 20px" @click="deleteConnectionLink">剔除</a-button>
      </a-form-item>
    </a-form>
    <a-table
        :pagination="pagination"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        :columns="columns"
        :dataSource="dataSource"
        @change="handleTableChange"
    >

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
        <a-tag v-else v-for="(item,index) in record.topics" :key="index" style="margin-top: 1px">{{ item }}</a-tag>
      </template>

      <template slot="action" slot-scope="text, record">
        <a-popconfirm
            v-if="dataSource.length"
            title="你确定要踢除这台设备连接吗?"
            @confirm="() => onDelete(record.clientId)"
        >
          <a href="javascript:;" style="color: cornflowerblue">下线</a>
        </a-popconfirm>
      </template>

    </a-table>
  </div>
</template>

<script>
import {connections, deleteConnections} from '@/services/smqtt'

const columns = [
  {
    title: 'ID',
    customRender: (text, record, index) => index + 1
  },
  {
    title: '设备IP',
    dataIndex: 'clientAddress'
  },
  {
    title: '节点IP',
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
    customRender: (text, record) => record.will ? record.will.willTopic : "无"

    // scopedSlots: {customRender: 'will'}
  },
  {
    title: '心跳时间',
    dataIndex: 'keepalive',
  },
  {
    title: '操作',
    dataIndex: 'action',
    scopedSlots: {customRender: 'action'},
  }

]
export default {
  name: "Connections",
  data() {
    return {
      params: {
        pageNumber: 0,
        pageSize: 10,
        clientId: null,
        nodeIp: null,
        clientIp: null
      },

      pagination: {
        total: 0,
        pageSize: 10, // 默认每页显示数量
        showSizeChanger: true, // 显示可改变每页数量
        pageSizeOptions: ['10', '20', '30', '40'], // 每页数量选项
        showTotal: total => `Total ${total} items`, // 显示总数
        onShowSizeChange: (page, pageSize) => {
          this.pagination.pageSize = pageSize
        }
      },
      selectedRowKeys: [],
      columns: columns,
      dataSource: []
    }
  },
  mounted() {
    this.getConnections()
  },
  methods: {
    reset() {
      this.params.pageNumber = 0
      this.params.pageSize = 10
      this.params.clientId = null
      this.params.nodeIp = null
      this.params.clientIp = null
      this.selectedRowKeys=[]
    },
    getConnections() {
      console.log(this.params)
      connections(this.params).then(res => {

        this.pagination.total = res.data.totalSize
        this.dataSource = res.data.content
      })
    },
    onSelectChange(selectedRowKeys) {
      console.log('selectedRowKeys changed: ', selectedRowKeys);
      this.selectedRowKeys = selectedRowKeys;

    },
    handleTableChange(val) {
      const pager = {...this.pagination};
      this.params.pageNumber = val.current - 1;  // 查看文档可知current 是改变页码数必要字段
      this.params.pageSize = val.pageSize;  // 查看文档可知pageSize是改变动态条数必要字段
      this.selectedRowKeys = [];
      this.pagination = pager;
      this.getConnections()
    },
    async deleteSingleConnectionLink(ids) {
      await deleteConnections(ids).then(res => {
        this.$message.info("deleted successfully!" + res.data);
      })
      await this.getConnections()
    },
    async onDelete(clientId) {

      var arrayObj = new Array();
      arrayObj.push(clientId)
      await deleteConnections({ids: arrayObj}).then(res => {
        this.$message.info("deleted successfully!" + res.data);
      })
      await this.getConnections()
    },
    async deleteConnectionLink() {
      if (this.selectedRowKeys.length == 0) {
        this.$message.error("请选择要剔除的连接！");
        return
      }
      let arrayObj = new Array();
      for (const key in this.selectedRowKeys) {
        let loc = Number(this.selectedRowKeys[key])
        let data = this.dataSource[loc].clientId

        arrayObj.push(data)

      }
      await this.deleteSingleConnectionLink({ids: arrayObj})
    }
  }

}
</script>