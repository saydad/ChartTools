<template>
  <div>
    <el-container>
<!--   菜单   -->
      <el-aside class="menu" width="50px">
        <span>{{nickname}}</span>
        <div class="chart-header">
          <img src="../favicon.ico" alt="">
        </div>
      </el-aside>

<!--   回话列表   -->
      <el-aside class="session-list" width="200px">
        <div style="display: flex">
          <el-autocomplete
              prefix-icon="el-icon-search"
              class="inline-input"
              v-model="searchContent"
              :fetch-suggestions="querySearch"
              placeholder="查询联系人"
              :trigger-on-focus="false"
              @select="handleSelect"></el-autocomplete>
          <i class="el-icon-circle-plus-outline" style="font-size: 40px; color: #dddddd" @click="showChartDialog = true"></i>
        </div>
        <el-row v-for="sessionInfo in sessionInfoList" :key="sessionInfo.id">
          <el-col :span="24">
          <!--  新消息提醒  -->
            <el-badge value="news" :hidden="!(allSessionHiddenNewMsg[sessionInfo.id] === false)">
              <el-card class="box-card" @click.native="selectSession(sessionInfo)">
                <div class="session-title">
                  <span>{{sessionInfo.name}}</span>
                  <span class="secondary-msg">{{formatDate(sessionInfo.timestamp)}}</span>
                </div>
                <span class="secondary-msg">{{sessionInfo.lastMsg}}</span>
              </el-card>
            </el-badge>
          </el-col>
        </el-row>
      </el-aside>

<!--   聊天区域   -->
      <el-aside class="window" width="650px">
        <div v-if="curSessionId === ''" class="empty-window secondary-msg">
          欢迎使用聊~聊
        </div>
        <div v-if="curSessionId !== ''" class="session-window">
          <el-header style="text-align: center; font-size: 20px">
            <span>{{ curSessionName }}</span>
          </el-header>
          <el-main @scroll.native="scrollFetch()" ref="msgWindow">
            <!--    消息区域      -->
            <div class="window-msg">
              <div class="mgs-fetch-tips" v-if="showMsgFetchTips" @click="directFetchPreMsg()">更多消息</div>
              <el-row type="flex" :justify="msgItem.curUser ? 'space-around' : ''" v-for="msgItem in curSessionMsgList" :key="msgItem.id">
              <!--     消息行    -->
                <el-col :span="11" :offset="msgItem.curUser ? 12 : 1" class="grid-content">
                  <div v-bind:class="msgClass(msgItem)" style="font-size: 10px">{{formatTime(msgItem.timestamp)}}</div>
                  <div class="bg-purple" v-bind:class="msgClass(msgItem)">{{formatMsg(msgItem)}}</div>
                </el-col>
              </el-row>
            </div>
          </el-main>
          <el-footer>
            <!--    输入框      -->
            <el-input class="window-input" type="textarea" :rows="5" v-model="msgArea"
                      @keydown.native="handleKeyCode($event)"
                      placeholder="换行(command + entry)/发送(entry)"
            ></el-input>
          </el-footer>
        </div>
      </el-aside>
    </el-container>

<!--  创建群聊dialog  -->
    <el-dialog
        title="创建群聊"
        :visible.sync="showChartDialog"
        width="30%"
        center>
      <div>
        <el-input style="width: 193px" v-model="createGroupChart.chartName" placeholder="群名称"></el-input>
        <el-autocomplete
            prefix-icon="el-icon-search"
            class="inline-input"
            v-model="createGroupChartUser"
            :fetch-suggestions="groupChartUserSearch"
            placeholder="查询联系人"
            :trigger-on-focus="false"
            @select="groupChartAddUser"></el-autocomplete>
      </div>
      <el-table :data="createGroupChart.userInfos">
        <el-table-column property="id" label="id"></el-table-column>
        <el-table-column property="value" label="姓名"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showChartDialog = false">取 消</el-button>
        <el-button type="primary" @click="createGroupChartAction()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
module.exports = {
  name: 'work',
  data() {
    return {
      //webSocket连接对象
      websocketObj: null,
      wsHeartBeatFlag: false,
      reconnectTime: 0,
      //当前用户昵称userId
      nickname: '',
      //当前用户id
      userId: '',
      //搜索内容
      searchContent: '',
      //当前日期
      curDate: moment(new Date().getTime()).format('YYYY-MM-DD'),
      //当前选中的回话id
      curSessionId: '',
      curSessionName: '',
      //聊天消息区域
      msgArea: '',
      //回话列表
      sessionInfoList: [],
      //所有会话消息
      allSessionMsgMap: {},
      //当前会话消息列表-临时对象
      curSessionMsgList: [],
      //会话是否新消息标记
      allSessionHiddenNewMsg: {},
      //是否展示更多消息按钮
      showMsgFetchTips: true,
      //是否展示创建群聊窗口
      showChartDialog: false,
      //创建群聊信息
      createGroupChart: {},
      //创建群聊时搜索的用户
      createGroupChartUser: ''
    }
  },
  created() {
    //初始化必须参数
    this.nickname = window.sessionStorage.getItem('loginNickName')
    const token = window.sessionStorage.getItem('token')
    //base64 解码
    const rawStr = window.atob(token)
    this.userId = rawStr.split('.')[0]

    this.curUserAllSession()
    this.initWebsocket(token)
  },
  computed: {
    //日期格式化
    formatDate() {
      return function (timestamp) {
          const msgTime = moment(timestamp)
          const msgDate = msgTime.format('YYYY-MM-DD')
          //消息的时间戳小于当前日期则展示日期
          if (moment(msgDate).isBefore(this.curDate)) {
            return msgDate
          }
          //展示时间
          return msgTime.format('HH:mm:ss')
        }
    },
    formatTime() {
      return function (timestamp) {
        const msgTime = moment(timestamp)
        const msgDate = msgTime.format('YYYY-MM-DD')
        //消息的时间戳小于当前日期则展示日期
        if (moment(msgDate).isBefore(this.curDate)) {
          return msgTime.format('YYYY/MM/DD HH:mm:ss')
        }
        //展示时间
        return msgTime.format('HH:mm:ss')
      }
    },
    //消息格式
    formatMsg() {
      return function (msgItem) {
        if (msgItem.curUser) {
          return msgItem.msg + " @mine"
        } else {
          return msgItem.user + "@ "+ msgItem.msg
        }
      }
    },
    //消息对其样式
    msgClass() {
      return function (msgItem) {
        if (msgItem.curUser) {
          return {
            'msg-right': true
          }
        } else {
          return {
            'msg-left': true
          }
        }
      }
    }
  },
  methods: {
    //建立websocket连接
    initWebsocket(token) {
      if(typeof(WebSocket) == "undefined") {
        return ELEMENT.Message.error('当前浏览器不支持webSocket')
      }

      const socketUrl = this.$http.defaults.baseURL.replace('http', 'ws').replace('https', 'ws')
      this.webSocketObj = new WebSocket(
          socketUrl + "/imServer/" + this.userId + "?token=" + token
      );
      this.webSocketObj.onmessage = this.onMessage
      this.webSocketObj.onopen = this.onOpen
      this.webSocketObj.onerror = this.onError
      this.webSocketObj.onclose = this.onClose
      //开启心跳检测
      this.timingHeart()
    },
    onOpen() {
      if (this.webSocketObj.readyState === 1) {
        console.log("连接建立成功!!")
        //- readyState 等于1 的时候建立链接成功
        this.wsHeartBeatFlag = true;
        this.reconnectTime = 0;
      }
    },
    async onMessage(evt) {
      const received_msg = evt && evt.data
      if (!received_msg) {
        return
      }
      console.log("接受到消息 " + received_msg)

      const sessionId = Number.parseInt(received_msg)
      if (sessionId) {
        //某个会话有新消息产生, 更新会话列表
        const sessionInfo = this.sessionIdFilter(sessionId);
        if (sessionInfo) {
          await this.fetchMessage(sessionInfo, true)
          //当前非自身会话窗口时增加新消息提醒
          if (this.curSessionId !== sessionId) {
            this.$set(this.allSessionHiddenNewMsg, sessionId, false)
          } else {
            //当前会话的消息窗口
            const msgWindowRef = this.$refs.msgWindow.$el
            //滑动条到底, 因为新增消息需要增加高度
            msgWindowRef.scrollTop = msgWindowRef.scrollHeight + 42
            //更新当前会话消息列表
            this.curSessionMsgList = this.allSessionMsgMap[this.curSessionId]
          }
        }
      }
    },
    onError() {
      console.log('socket异常')
      this.onClose()
    },
    onClose() {
      console.log('socket关闭')
      this.wsHeartBeatFlag = false;
      this.webSocketObj && this.webSocketObj.close && this.webSocketObj.close();
      //重连
      clearTimeout(this.wsHeart);
      this.wsHeartBeatFlag = false;
      if (this.reconnectTime <= 10) {
        console.log('socket准备重连')
        setTimeout(() => {
          console.log('socket重连')
          this.initWebsocket(window.sessionStorage.getItem('token'))
          this.reconnectTime+=1;
        }, 5000);
      } else {
        ELEMENT.Message.error('抱歉，暂时无法连接到聊天服务器，请稍后再试');
      }
    },
    timingHeart() {
      // 心跳检测  每4.5分钟发送一次
      if (this.wsHeartBeatFlag) {
        this.webSocketObj.send('hi!');
      }
      this.wsHeart = setTimeout(() => {
        this.timingHeart();
      }, 30 * 1000); // 30s心跳
    },
    //加载用户所有的会话信息
    async curUserAllSession() {
      const res = await this.$http.get('/conversation/list/specifyUser', {
        params: {
          userId: this.userId
        }
      })
      if (res.status !== 200 || res.data.code !== 0) {
        return ELEMENT.Message.error('获取当前用户全量会话信息失败')
      }
      this.sessionInfoList = res.data.result
    },
    //搜索, cb返回过滤后的结果
    async querySearch(queryString, cb) {
      const res = await this.$http.get('/user/search', {params:
            {
              userId: this.userId,
              query: queryString
            }
      })
      if (res.status!== 200 && res.data.code !== 0) {
        return ELEMENT.Message.error('获取搜索结果失败')
      }

      cb(res.data.result)
    },
    //搜索选中项
    async handleSelect(item) {
      //拉取该指定会话信息
      if (!item.id || !item.value) {
        return ELEMENT.Message.error('拉取指定会话信息, 参数缺失')
      }

      // 是否在session列表中存在
      const existIndex = this.nickNameFilter(item)
      if (existIndex !== -1) {
        const targetSession = this.sessionInfoList[existIndex]
        this.sessionInfoList.splice(existIndex, 1)
        this.sessionInfoList.unshift(targetSession)
        this.searchContent = ''
        return
      }

      const res = await this.$http.post('/conversation/specifyChart', JSON.stringify({
        type: item.type,
        targetId: item.id,
        curUserId: this.userId
      }), {
        headers: {
          'content-type': 'application/json'
        }
      });

      if (res.status!== 200 || res.data.code !== 0) {
        return ELEMENT.Message.error('拉取指定会话信息失败')
      }

      //添加数据到sessionInfoList
      this.sessionInfoList.unshift(res.data.result)
      this.curSessionId = item.id
      this.curSessionName = item.value
      this.searchContent = ''
    },
    //通过昵称过滤session信息的在数组中的下标
    nickNameFilter(sessionCondition) {
      for (let i = 0; i < this.sessionInfoList.length; i++) {
        if (this.sessionInfoList[i].name === sessionCondition.value) {
          return i
        }
      }
      return -1
    },
    //通过sessionId获取session信息
    sessionIdFilter(sessionId) {
      for (let i = 0; i < this.sessionInfoList.length; i++) {
        if (this.sessionInfoList[i].id === sessionId) {
          return this.sessionInfoList[i]
        }
      }
      return null
    },
    //选中指定回话
    async selectSession(sessionInfo) {
      //拉取该session的聊天信息
      await this.fetchMessage(sessionInfo, true)

      //更新该会话
      this.curSessionId = sessionInfo.id
      this.curSessionName = sessionInfo.name
      //更新消息列表
      this.curSessionMsgList = this.allSessionMsgMap[this.curSessionId]
      //取消新消息提醒标记
      this.$set(this.allSessionHiddenNewMsg, sessionInfo.id, true)
    },
    //拉取消息列表 down-true:代表拉取最新消息
    async fetchMessage(sessionInfo, down) {
      const targetSessionMsgList = this.allSessionMsgMap[sessionInfo.id]
      let thresholdTimestamp = sessionInfo.timestamp
      if (!targetSessionMsgList) {
        //第一次拉取消息,将时间限制推回三小时之前
        thresholdTimestamp = thresholdTimestamp - 3 * 3600 * 1000
        //第一次强制向下拉取消息
        down = true
      } else if (!down) {
        //向上拉取,取最旧的时间戳
        thresholdTimestamp = sessionInfo.oldestTimestamp
      }

      const res = await this.$http.post('/conversation/message/list', JSON.stringify({
        conversationId: sessionInfo.id,
        curUserId: this.userId,
        timestamp: thresholdTimestamp,
        down: down
      }), {
        headers: {
          'content-type': 'application/json'
        }
      })

      if (res.status !== 200 || res.data.code !== 0) {
        return ELEMENT.Message.error('拉取该会话消息列表失败')
      }

      const msgList= res.data.result
      if (msgList.length === 0) {
        ELEMENT.Message.success('当前会话已无消息')
        return
      }

      if (!down) {
        //向上查询时将已有数据尾插
        targetSessionMsgList.forEach(msg => {
          if (!msgList.some(msgItem => {return msgItem.id === msg.id})) {
            msgList.push(msg)
          }
        })

        this.$set(this.allSessionMsgMap, sessionInfo.id, msgList)
        //赋值当前session下最老消息的时间戳
        sessionInfo.oldestTimestamp = msgList[0].timestamp
        return
      }

      //还不存在该会话的消息列表
      if (!targetSessionMsgList) {
        this.$set(this.allSessionMsgMap, sessionInfo.id, msgList)
        //第一次需要赋值当前session下最老消息的时间戳
        sessionInfo.oldestTimestamp = msgList[0].timestamp
      } else {
        //向下查询时尾部插入
        msgList.forEach(msg => {
          if (!targetSessionMsgList.some(msgItem => {return msgItem.id === msg.id})) {
            targetSessionMsgList.push(msg)
          }
        })
      }

      //更新session信息
      const lastMsg = msgList[msgList.length - 1]
      this.$set(sessionInfo, 'timestamp', lastMsg.timestamp)
      this.$set(sessionInfo, 'lastMsg', lastMsg.msg)
    },
    //滑动拉取消息
    scrollFetch() {
      const msgWindowRef = this.$refs.msgWindow.$el
      if (msgWindowRef.scrollHeight !== 0) {
        //存在滑动条则不展示'更多消息'
        this.showMsgFetchTips = false;
      }

      let scrollTopValue = msgWindowRef.scrollTop
      if (scrollTopValue === 0) {
        this.directFetchPreMsg()
      }
    },
    //直接获取历史的消息
    async directFetchPreMsg() {
      const sessionInfo = this.sessionIdFilter(this.curSessionId)
      await this.fetchMessage(sessionInfo, false)
      //更新当前会话的消息列表
      this.curSessionMsgList = this.allSessionMsgMap[this.curSessionId]
    },
    //创建群聊搜索用户, cb返回过滤后的结果
    async groupChartUserSearch(queryString, cb) {
      const res = await this.$http.get('/user/search', {params:
            {
              userId: this.userId,
              query: queryString
            }
      })
      if (res.status!== 200 && res.data.code !== 0) {
        return ELEMENT.Message.error('获取搜索结果失败')
      }

      console.log(res.data.result)
      cb(res.data.result)
    },
    //创建群聊增加用户
    groupChartAddUser(item) {
      console.log(item)
      if (!this.createGroupChart.userInfos) {
        this.$set(this.createGroupChart, 'userInfos', [])
      }
      this.createGroupChart.userInfos.push(item)
      this.createGroupChartUser = ''
    },
    //创建群聊发送请求
    async createGroupChartAction() {
      if (!this.createGroupChart.chartName || this.createGroupChart.chartName === '') {
        ELEMENT.Message.error('群名未填写')
        return
      }
      if (!this.createGroupChart.userInfos || this.createGroupChart.userInfos.length === 0) {
        ELEMENT.Message.error('群成员为空')
        return
      }

      const res = await this.$http.post('/conversation/createChart', JSON.stringify({
        type: 2,
        chartName: this.createGroupChart.chartName,
        createUser: this.userId,
        chartUsers: this.createGroupChart.userInfos.map(userInfo => userInfo.id)
      }), {
        headers: {
          'content-type': 'application/json'
        }
      })

      if (res.status !== 200 || res.data.code !== 0) {
        ELEMENT.Message.error('创建群聊失败')
        return
      }

      this.sessionInfoList.push(res.data.result)
      this.showChartDialog = false
      this.createGroupChart = {}
    },
    // 键盘回车事件
    // 回车发送数据 / command + 回车 换行
    handleKeyCode(event) {
      if (event.keyCode === 13) {
        if (!event.metaKey) {
          event.preventDefault();
          this.sendMsg();
        } else {
          this.msgArea = this.msgArea + '\n';
        }
      }
    },
    sendMsg() {
      //发送消息
      this.webSocketObj.send(JSON.stringify({
        conversationId: this.curSessionId,
        userId: this.userId,
        msg: this.msgArea,
        timestamp: new Date().getTime()
      }));
      this.msgArea = ''
    }
  },
}
</script>

<style scoped>
.el-container {
  width: 900px;
  height: 800px;
  border: 1px solid black;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 10px #ddd;
}
.menu {
  background-color: black;
}
.menu span {
  margin-top: 20px;
  display: block;
  color: white;
  text-align: center;
}
.menu div {
  position: absolute;
  bottom: 0;
  margin-bottom: 5px;
}
.menu div img {
  margin-left: 5px;
}
.session-title {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.session-list {
  border-left: 1px solid black;
  border-right: 1px solid black;
}
.session-list .el-autocomplete {
  width: 190px;
  margin-left: 5px;
  margin-bottom: 3px;
}
.session-list .el-autocomplete {
  margin-top: 2px;
}
.el-card {
  border: 1px solid #dddddd;
  line-height: 20px;
  margin-bottom: 1px;
}
.el-card span {
  display: block;
  white-space: nowrap;
  text-overflow:ellipsis;
  overflow:hidden;
}
.el-card :hover {
  background-color: #dddddd;
}
.secondary-msg {
  font-size: 10px;
  color: #bcb9b9;
}
.empty-window {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(20%, -50%);
  font-size: 30px;
}
.bg-purple {
  border-radius: 4px;
  background: #d3dce6;
  margin-bottom: 5px;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.session-window {
  height: 790px;
}
.session-window .el-header {
  border-bottom: 1px solid #dddddd;
}
.window-msg {
  height: 620px;
}
.window-input {
  width: 640px;
  position: absolute;
  bottom: 0;
  margin-left: 4px;
}
.el-main {
  padding: 0;
}
.el-footer {
  padding: 0;
}
.msg-left {
  float: left;
  clear: both;
}
.msg-right {
  float: right;
  clear: both;
}
.el-badge {
  margin-left: 5px;
  width: 190px;
}
.el-badge sup {
  top: 10px;
  right: 43px;
}
.mgs-fetch-tips {
  background-color: #dddddd;
  color: white;
  border-radius: 10px;
  width: 60px;
  height: 17px;
  text-align: center;
  font-size: 5px;
  margin: auto;
}
</style>