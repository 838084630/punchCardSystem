<template>
  <div class="calenderDiv">
    <div class="top-title">
      <div class="center"><span @click="lastMonth" class="link">⋘</span></div>
      <div class="center"><span>{{ year }}年{{ month }}月</span></div>
      <div class="center"><span @click="nextMonth" class="link">⋙</span></div>
    </div>
    <div class="container" style="border-bottom: 1px solid #cccccc">
      <div v-for="(item, index) in weeks" :key="index">{{ item }}</div>
    </div>
    <div class="container" style="padding: 1vh 1vh 3vh 1vh;">
      <div v-for="(item, index) in data" :key="index">
        <div v-if="compareToNow(item) === 0" style="color: #2d8cf0" @click="todo(item)">{{ item.date }}</div>
        <div v-if="compareToNow(item) === -1" @click="todo(item)">{{ item.date }}</div>
        <div v-if="compareToNow(item) === -2" style="color: red" class="otherDay" @click="todo(item)">
          <el-button type="danger" circle class="otherDayBtnSize">{{ item.date }}</el-button>
        </div>
        <div v-if="compareToNow(item) === 1" class="future">{{ item.date }}</div>
      </div>
    </div>
  </div>
  <el-dialog custom-class="reply" class="dialogClass" v-model="dialogFormVisible" title="勤怠入力画面"
    :before-close="handleClose">
    <div class="btnCenter">
      <el-button type="primary" circle class="btnSize" :class="{ btnAction: !btnStatus }" @click="submit"
        :disabled="btnStatus">{{ timeHHmm }}</el-button>
    </div>
    <div class="cont">
      <div slot="label">出勤: <b>{{ form.punchin }}</b></div>
      <div slot="label">退勤: <b>{{ form.punchout }}</b></div>

    </div>
  </el-dialog>

</template>


<script lang="ts" setup>
import { ref, onMounted, reactive, onBeforeUnmount } from 'vue'
import { getRecordApi, punchCardApi, getAbsenceApi } from '../../request/api'
import Cookie from 'js-cookie'
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus'

let router = useRouter();
const dialogFormVisible = ref(false)
const form = reactive({
  punchin: '',
  punchout: ''
})
let btnStatus = ref(true)

let timeHHmm = ref('');
let timer = null;
const getNowTime = (newtime: any) => {
  let hh = new Date().getHours() < 10 ? '0' + new Date().getHours() : new Date().getHours();
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes();
  let mfx = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds();
  return hh + ":" + mf + ":" + mfx;
}
onMounted(() => {
  getNow();
  timer = setInterval(() => {
    timeHHmm.value = getNowTime(new Date());
  }, 1000)
})

// onBeforeUnmount(()=>{
//   if(timer){
//     clearInterval(timer)
//   }
// })


let now = ref(new Date())
let weeks = ref(["日", "一", "二", "三", "四", "五", "六"])
let year = ref("")
let month = ref("")
let date = ref("")
let firstDay = ref("")
let data: any = ref([])
let resubmit: number[] = reactive([])
let recordIndex: any;

//初期化呼び出し
todo({
  year: now.value.getFullYear(),
  month: now.value.getMonth() + 1,
  date: now.value.getDate(),
})
const getNow = () => {
  year.value = String(now.value.getFullYear());
  month.value = String(now.value.getMonth() + 1);
  date.value = String(now.value.getDate());
  now.value.setDate(1);
  firstDay.value = String(now.value.getDay());
  getAbsenceArr()

  initData();
}
function getMonthDay(month: number): any {
  if ([1, 3, 5, 7, 8, 10, 12].includes(month)) {
    return 31
  } else if ([4, 6, 9, 11].includes(month)) {
    return 30
  } else if (month === 2) {
    if (
      (Number(year.value) % 4 === 0 && Number(year.value) % 100 !== 0) ||
      Number(year.value) % 400 === 0
    ) {
      return 29
    } else {
      return 28
    }
  }
}
const initData = () => {
  data.value = [];
  let days = getMonthDay(Number(month.value));

  for (let i = 0; i < Number(firstDay.value); i++) {
    data.value.push({
      year: "",
      month: "",
      date: "",
    });
  }
  for (let i = 0; i < days; i++) {
    data.value.push(
      {
        year: year.value,
        month: month.value,
        date: i + 1,
      }
    );
  }

}
const lastMonth = () => {
  now.value.setMonth(now.value.getMonth() - 1);
  getNow();
}
const nextMonth = () => {
  now.value.setMonth(now.value.getMonth() + 1);
  getNow();
}
function compareToNow(item: any): any {

  if (item.year && item.month && item.date) {
    let date1 = new Date();
    date1.setFullYear(item.year)
    date1.setMonth(item.month - 1)
    date1.setDate(item.date)
    date1.setHours(0)
    date1.setMinutes(0)
    date1.setSeconds(0)
    let now = new Date();
    now.setHours(0)
    now.setMinutes(0)
    now.setSeconds(0)

    if (date1.getTime() > now.getTime()) {
      //将来
      return 1
    } else if (date1.getTime() === now.getTime()) {
      //本日
      return 0
    } else if (date1.getTime() < now.getTime()) {
      //以前
      if (resubmit.includes(item.date)) {
        //以前欠勤の場合
        return -2
      } else {
        //以前正常の場合
        return -1
      }

    }
  }
}
function todo(item: any): any {
  form.punchin = '';
  form.punchout = '';
  dialogFormVisible.value = true
  let username = Cookie.get('username') || ''
  let date = item.year + '-' + item.month + '-' + item.date
  getRecordApi(username, date).then(res => {
    if (res.code.value === 200) {
      if (res.data !== null) {
        form.punchin = res.data.punchInTime !== null ? res.data.punchInTime : '';
        form.punchout = res.data.punchOutTime !== null ? res.data.punchOutTime : '';
        recordIndex = res.data.id !== null ? res.data.id : '';
        /*ボタン非活性判断
          1.過去または将来の場合、ボタン非活性
          2.本日の場合①出勤と退勤記録有：非活性　以外：活性
        */
      }
      if (compareToNow(item) === 0) {
        btnStatus.value = form.punchin === '' || form.punchout === '' ? false : true;
      }
    } else if (res.code.value === 401) {
      router.push('/login')
    }
  })
}

const getLocalTime = () => {
  let yy = new Date().getFullYear();
  let mmWithoutZero = new Date().getMonth() + 1
  let mm = mmWithoutZero < 10 ? '0' + mmWithoutZero : mmWithoutZero;
  let dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate();
  let hh = new Date().getHours() < 10 ? '0' + new Date().getHours() : new Date().getHours();
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes();
  return yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf;
}

const submit = () => {
  console.log(form.punchin);
  console.log(form.punchout);

  let punchInTime = form.punchin;
  let punchOutTime = form.punchout;
  if (punchInTime === '') {
    //本日の記録がない場合
    punchInTime = getLocalTime();
  } else if (punchInTime !== '' && punchOutTime === '') {
    //本日の出勤記録があり、退勤記録がない場合
    punchOutTime = getLocalTime();
  }

  let record: any = {
    id: recordIndex,
    username: Cookie.get('username'),
    punchInTime: punchInTime,
    punchOutTime: punchOutTime
  }

  punchCardApi(record).then(res => {
    if (res.code.value === 200) {
      ElMessage({
        message: '保存しました',
        type: 'success',
      })
    } else if (res.code.value === 401) {
      router.push('/login')
    }
    dialogFormVisible.value = false
  })

}

function getAbsenceArr() {
  let days = getMonthDay(Number(month.value));
  resubmit.length = 0

  getAbsenceApi(Cookie.get('username') || '', year.value + '-' + month.value, days).then(res => {
    if (res.code.value == 200) {
      let thisMonthErrDate: any = res.data;
      for (let i of thisMonthErrDate) {
        resubmit.push(i)
      }
    }
  })

}

const handleClose = (done: () => void) => {
  btnStatus.value = true;
  done()

}

</script>


<style scoped lang="less">
.el-form-item__label {
  width: 50px;

}

@media screen and (max-width: 200000px) {
  .calenderDiv {
    width: 40%;
    position: relative;
    left: 20%;
    margin-top: 3%;

    .otherDay {
      background-size: 40%;
    }
  }

  .inputOut {
    width: 300px;
  }

}

@media screen and (max-width: 500px) {

  .calenderDiv {
    width: 90%;
    position: relative;
    left: 5%;
    margin-top: 3%;

    .otherDay {
      background-size: 70%;
    }
  }

  .inputOut {
    width: 150px;
  }
}

.el-button--text {
  margin-right: 15px;
}

.el-select {
  width: 300px;
}

.dialog-footer button:first-child {
  margin-right: 10px;
}


.top-title {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-auto-rows: 40px;
  grid-gap: 1rem;
  background-color: #FFFFFF;
  border-bottom: 1px solid #cccccc;
  line-height: 40px;
}

.container {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-auto-rows: 40px;
  background-color: #FFFFFF;
  line-height: 40px;

  div {
    text-align: center;
  }
}

.link {
  font-size: 14px;
  color: #2d8cf0;
  text-align: center;
}

.date-desc {
  display: block;
  position: absolute;
  width: 2vw;
  top: 0;
  left: 3vw;
  font-size: 0.8vw;
  color: green;
}

.center {
  text-align: center;
}

.future {
  background-color: gainsboro;
}

.reply .el-dialog__body {
  padding-top: 0px;
  padding-bottom: 0px;
}

.btnSize {
  width: 120px;
  height: 120px;
}

.btnAction {
  animation: heartbeat 2s linear infinite;
}

@keyframes heartbeat {
  50% {
    transform: scale(1.1);
  }
}

.otherDayBtnSize {
  width: 30px;
  height: 30px;
}

.btnCenter {
  text-align: center;
  margin: 10px;
}

.otherDay {
  text-align: center;
}

.el-input input {
  border: none;
}
</style>
