<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
             label-width="150px">
      <el-form-item label="监控类型" prop="monitorType">
        <el-select v-model="dataForm.monitorType" value-key="monitorType" filterable clearable placeholder="请选择">
          <el-option
            v-for="item in monitorTypes"
            :key="item.monitorType"
            :label="item.monitorTypeDesc"
            :value="item.monitorType">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="监控频率" prop="frequency">
        <el-select v-model="dataForm.frequency" value-key="frequency" filterable clearable placeholder="请选择">
          <el-option
            v-for="item in frequencys"
            :key="item.frequency"
            :label="item.frequencyDesc"
            :value="item.frequency">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="描述" prop="remark">
        <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
      </el-form-item>
      <el-form-item label="ip地址" prop="host">
        <el-input v-model="dataForm.hostName" placeholder="ip地址"></el-input>
      </el-form-item>
      <el-form-item label="端口" prop="port">
        <el-input v-model="dataForm.port" placeholder="端口"></el-input>
      </el-form-item>
      <el-form-item label="用户" prop="username">
        <el-input v-model="dataForm.username" placeholder="root"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="pwd">
        <el-input v-model="dataForm.pwd" placeholder="123456"></el-input>
      </el-form-item>
      <el-form-item label="密钥文件地址" prop="pem">
        <el-input v-model="dataForm.pem" placeholder="/root/.ssh/id_rsa"></el-input>
      </el-form-item>
      <!-- LOG monitor start -->
      <div v-if="dataForm.monitorType === 'LOG'">
        <el-form-item label="日志文件" prop="filePath">
          <el-input v-model="dataForm.filePath" placeholder="/root/lite-monitor-server/logs/m.log"></el-input>
        </el-form-item>
        <el-form-item label="统计范围" prop="statSecond">
          <el-input v-model="dataForm.statSecond" placeholder="统计近多少秒之内的日志"></el-input>
        </el-form-item>
        <el-form-item label="阈值" prop="threshold">
          <el-input v-model="dataForm.threshold" placeholder="阈值"></el-input>
        </el-form-item>
        <el-form-item label="展示条数" prop="showCount">
          <el-input v-model="dataForm.showCount" placeholder="默认展示10"></el-input>
        </el-form-item>
      </div>
      <!-- LOG monitor end -->
      <el-form-item v-if="dataForm.monitorType === 'LOG'" label="命令" prop="shellCmd">
        <el-input v-model="dataForm.shellCmd" placeholder="awk -F, '{print $1}'"></el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.monitorType === 'PROCESS'" label="关键字" prop="shellCmd">
        <el-input v-model="dataForm.shellCmd" placeholder="lite-monitor-service"></el-input>
      </el-form-item>
      <el-form-item label="钉钉标题" prop="dingTitle">
        <el-input v-model="dataForm.dingTitle" placeholder="钉钉提醒消息标题"></el-input>
      </el-form-item>
      <el-form-item label="钉钉机器人token" prop="dingToken">
        <el-input v-model="dataForm.dingToken" placeholder="钉钉机器人token"></el-input>
      </el-form-item>
      <el-form-item label="钉钉@人员" prop="dingAt">
        <el-input v-model="dataForm.dingAt" placeholder="多个手机号用英文逗号分割"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  module.exports = {
    data () {
      return {
        visible: false,
        dataForm: {
          id: undefined,
          monitorType: undefined,
          frequency: undefined,
          hostName: undefined,
          port: 22,
          remark: undefined,
          username: undefined,
          pwd: undefined,
          pem: undefined,
          filePath: undefined,
          threshold: undefined,
          shellCmd: undefined,
          dingTitle: undefined,
          dingToken: undefined,
          statSecond: undefined,
          showCount: 10,
          dingAt: undefined
        },
        monitorTypes: [{
          monitorType: 'LOG',
          monitorTypeDesc: '日志'
        }, {
          monitorType: 'PROCESS',
          monitorTypeDesc: '进程'
        }],
        frequencys: [],
        dataRule: {
          remark: [
            {required: true, message: '描述不能为空', trigger: 'blur'}
          ],
          hostName: [
            {required: true, message: 'ip地址不能为空', trigger: 'blur'}
          ],
          port: [
            {required: true, message: '端口不能为空', trigger: 'blur'}
          ],
          monitorType: [
            {required: true, message: '监控类型不能为空', trigger: 'blur'}
          ],
          frequency: [
            {required: true, message: '监控频率不能为空', trigger: 'blur'}
          ],
          username: [
            {required: true, message: '用户名不能为空', trigger: 'blur'}
          ],
          dingToken: [
            {required: true, message: '钉钉机器人token不能为空', trigger: 'blur'}
          ]
        }
      }
    },
    mounted () {
      this.initFrequency()
    },
    methods: {
      init (id) {
        this.dataForm.id = id || undefined
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            let url = '/liteMonitor/info?id=' + id
            axios.get(getUrl(url)).then((res) => {
              let data = res.data
              if (data && data.code === 0) {
                this.dataForm.id = data.entity.id
                this.dataForm.monitorType = data.entity.monitorType
                this.dataForm.frequency = data.entity.frequency
                this.dataForm.hostName = data.entity.hostName
                this.dataForm.port = data.entity.port
                this.dataForm.remark = data.entity.remark
                this.dataForm.username = data.entity.username
                this.dataForm.pwd = data.entity.pwd
                this.dataForm.pem = data.entity.pem
                this.dataForm.filePath = data.entity.filePath
                this.dataForm.threshold = data.entity.threshold
                this.dataForm.shellCmd = data.entity.shellCmd
                this.dataForm.dingTitle = data.entity.dingTitle
                this.dataForm.dingToken = data.entity.dingToken
                this.dataForm.statSecond = data.entity.statSecond
                this.dataForm.showCount = data.entity.showCount
                this.dataForm.dingAt = data.entity.dingAt
              } else {
                this.$message.error(data.msg)
              }
            })
          } else {
            this.dataForm.monitorType = 'LOG'
          }
        })
      },
      initFrequency () {
        axios.get(getUrl('/liteMonitor/frequency')).then((res) => {
          let data = res.data
          if (data && data.code === 0) {
            this.frequencys = data.frequencyList
          } else {
            this.$message.error(data.msg)
          }
        })
      },
      copy (id) {
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          let url = '/liteMonitor/info?id=' + id
          axios.get(getUrl(url)).then((res) => {
            let data = res.data
            if (data && data.code === 0) {
              this.dataForm.id = undefined
              this.dataForm.monitorType = data.entity.monitorType
              this.dataForm.frequency = data.entity.frequency
              this.dataForm.hostName = data.entity.hostName
              this.dataForm.port = data.entity.port
              this.dataForm.remark = data.entity.remark
              this.dataForm.username = data.entity.username
              this.dataForm.pwd = data.entity.pwd
              this.dataForm.pem = data.entity.pem
              this.dataForm.filePath = data.entity.filePath
              this.dataForm.threshold = data.entity.threshold
              this.dataForm.shellCmd = data.entity.shellCmd
              this.dataForm.dingTitle = data.entity.dingTitle
              this.dataForm.dingToken = data.entity.dingToken
              this.dataForm.statSecond = data.entity.statSecond
              this.dataForm.showCount = data.entity.showCount
              this.dataForm.dingAt = data.entity.dingAt
            } else {
              this.$message.error(data.msg)
            }
          })
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            let url = '/liteMonitor/save'
            axios.post(getUrl(url), this.dataForm).then((res) => {
              let data = res.data
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refresh')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
