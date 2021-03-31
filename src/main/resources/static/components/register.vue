<template>
  <div class="register-container">
    <div class="chart-header">
      <img src="../favicon.ico" alt="">
    </div>

    <div class="register-box">
      <div class="register-form">
        <div class="register-title">
          <h1>创建账户</h1>
          <div>已有账户?<router-link class="register-router" to="/login">登录</router-link></div>
        </div>

        <el-form :model="user" :rules="rules">
          <el-form-item prop="nickname">
            <el-input v-model="user.nickname" placeholder="昵称"></el-input>
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="user.phone" placeholder="手机号"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" v-model="user.password" placeholder="密码"></el-input>
          </el-form-item>
          <el-form-item class="btns">
            <el-button type="primary" @click="onSubmit">创建</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
module.exports = {
  name: "register",
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
    const validateNickname = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入昵称'));
      }
      callback();
    }
    return {
      rules: {
        nickname: [
          { validator: validateNickname, trigger: 'blur' }
        ],
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
      const res = await this.$http.post('/user/register', JSON.stringify(this.user), {
        headers: {
          'content-type': 'application/json'
        }
      })

      if (res.data.code !== 0) {
        return ELEMENT.Message.error("创建失败: " + res.data.msg);
      }

      this.$router.push("/login")
      return ELEMENT.Message.success("创建成功");
    }
  }
}
</script>

<style scoped>
.register-container {
  height: 100%;
}

.register-box {
  width: 800px;
  height: 600px;
  box-shadow: 0 0 10px #ddd;
  position: absolute;
  left: 50%;
  top: 20%;
  transform: translate(-50%);
}

.register-form {
  width: 450px;
  height: 300px;
  border-radius: 3px;
  position: absolute;
  top: 50%;
  left:50%;
  transform: translate(-50%, -50%);
}

.register-form .register-title {
  position: absolute;
  left: 50%;
  transform: translate(-50%, -100%);
  text-align: center;
  line-height: 15px;
}

.register-form .el-form {
  margin-top: 15px;
}

.register-router {
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
</style>