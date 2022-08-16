<template>
  <div class="bg">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">Punch Card System</span>
        </div>
      </template>
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" class="demo-ruleForm">
        <el-form-item prop="username">
          <el-input v-model="ruleForm.username" type="text" autocomplete="off" placeholder="ユーザID"
            :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="ruleForm.password" type="password" autocomplete="off" placeholder="パスワード"
            :prefix-icon="Key" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loginFn()" class="loginBtn">login</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
  <el-button :plain="true" @click="open4">error</el-button>
</template>

<script lang="ts" setup>
import { ElMessage } from 'element-plus';
import { reactive, toRefs, ref } from 'vue';
import { adminLoginApi, userCheckApi } from '../../request/api';
import { User, Key } from '@element-plus/icons-vue';
import Cookie from 'js-cookie';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';


let router = useRouter();
let store = useStore();

const state = reactive({
  ruleForm: {
    username: '',
    password: ''
  }
})

if (Cookie.get("token") != null && Cookie.get("username") != null) {
  userCheckApi({
    username: Cookie.get("username") || '',
    token: Cookie.get("token")|| ''
  }).then(res => {
    if(res.code.value === 200){
    router.push('/homepage')
    }
  })
}else{
  alert('セッションが失効しました。もう一度ログインください')
}

const open4 = () => {
  ElMessage({
    showClose: true,
    message: 'ユーザID・パスワードが一致しません。',
    type: 'error',
  })
}

let { ruleForm } = toRefs(state)
//获取el-from组件对象
let ruleFormRef = ref();

const validateName = (rule: unknown, value: string, cb: (msg?: string) => void) => {
  if (!value) {
    cb('ユーザIDを入力してください。')
  } else {
    cb();
  }
}
const validatePass = (rule: unknown, value: string, cb: (msg?: string) => void) => {
  if (!value) {
    cb('パスワードを入力してください。')
  }
  else {
    // v-model.number配合下面的判断实现必须number check
    // if (!Number.isInteger(value)) {
    //   cb('password was not number')
    // }
    cb();
  }
}
const rules = reactive({
  username: [{ validator: validateName, trigger: 'blur' }],
  password: [{ validator: validatePass, trigger: 'blur' }]
})

const loginFn = () => {
  ruleFormRef.value.validate().then(() => {
    adminLoginApi({
      username: ruleForm.value.username,
      password: ruleForm.value.password
    }).then(res => {
      console.log(res);
      if (res.code.value === 200) {
        //引入第三方库cookie，先存储token,expires:7过期时间
        // Cookie.set('token',res.data.tokenHead + res.data.token,{ expires: 7});
        Cookie.set('token', res.data.token, { expires: 7 });
        Cookie.set('username', ruleForm.value.username, { expires: 7 });
        router.push('/homepage')
      } else {
        open4()
        // location.reload();
      }
    })
  }).catch(() => {
    console.log('error')
  })
}


</script>

<style scoped>
.bg {
  position: absolute;
  display: flex;
  left: 0px;
  top: 0px;
  background-image: url('../../assets/p1.jpeg');
  background-size: 100% 100%;
  width: 100%;
  height: calc(100vh);
  justify-content: center;
  align-items: center
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title,
.loginBtn {
  margin: 0 auto;
}

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.box-card {
  width: 400px;

  filter: alpha(opacity=80);
  opacity: 0.8
}

.el-form {
  width: 200px;
  margin: 0px auto;
}

.el-card__body {
  padding-top: 50px;
}
</style>
