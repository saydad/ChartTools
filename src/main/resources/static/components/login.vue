<template>
  <div class="login-container">
    <div class="chart-header">
      <img src="../favicon.ico" alt="">
    </div>

    <div class="login-box">
      <div class="login-form">
        <div class="login-title">
          <h1>登录</h1>
          <div>新用户?<router-link class="login-router" to="/register">注册账号</router-link></div>
        </div>

        <el-form :model="user" :rules="rules">
          <el-form-item prop="phone">
            <el-input v-model="user.phone" placeholder="手机号"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" v-model="user.password" placeholder="密码"></el-input>
          </el-form-item>
          <el-form-item class="btns">
            <el-button type="primary" @click="onSubmit">登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="login-footer">
      聊~聊 出品
    </div>
  </div>
</template>

<script>
module.exports = {
  name: 'login',
  data() {
    const validatePhone = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入昵称'));
      }
      if(!(/^1[3456789]\d{9}$/.test(value))){
        callback(new Error('无效的手机号'));
      }
      callback();
    }
    const validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      }
      callback();
    }
    return {
      rules: {
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, trigger: 'blur' }
        ]
      },
      user: {}
    }
  },
  methods: {
    async onSubmit() {
      const res = await this.$http.post('/user/login', JSON.stringify(this.user), {
        headers: {
          'content-type': 'application/json'
        }
      });

      if (res.data.code !== 0) {
        return ELEMENT.Message.error("登录失败: " + res.data.msg);
      }

      window.sessionStorage.setItem("token", res.data.result.token)
      window.sessionStorage.setItem("loginNickName", res.data.result.nickname)
      ELEMENT.Message.success("登录成功");
      this.$router.push("/work")
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100%;
}

.login-box {
  width: 800px;
  height: 600px;
  box-shadow: 0 0 10px #ddd;
  position: absolute;
  left: 50%;
  top: 20%;
  transform: translate(-50%);
}
.login-form {
  width: 450px;
  height: 300px;
  border-radius: 3px;
  position: absolute;
  top: 50%;
  left:50%;
  transform: translate(-50%, -50%);
}
.login-form .login-title {
  position: absolute;
  left: 50%;
  transform: translate(-50%, -100%);
  text-align: center;
  line-height: 15px;
}
.login-form .el-form {
  margin-top: 15px;
}
.login-router {
  margin-left: 5px;
  color: #8074d6;
  text-decoration: none;
}
.btns {
  margin-top: 100px;
  display: flex;
  justify-content: center;
}
.btns .el-button {
  width: 250px;
  border-radius: 100px;
}
.login-footer {
  height: 40px;
  width: 100%;
  position: absolute;
  bottom: 0;
  text-align: center;
}
</style>
