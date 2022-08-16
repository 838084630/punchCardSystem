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
          {{ item.date }}<div class="date-desc">欠</div>
        </div>
        <div v-if="compareToNow(item) === 1" class="future">{{ item.date }}</div>
      </div>
    </div>
  </div>

  <!-- punch in dialog -->
  <!-- <el-dialog v-model="dialogFormVisible" title="勤怠入力画面" width="500px" @close="displayMessage"> -->
  <el-dialog v-model="dialogFormVisible" title="勤怠入力画面" width="500px">
    <el-form :model="form">
      <el-form-item label="出勤" :label-width="formLabelWidth">
        <el-input v-model="form.punchin" autocomplete="off" :readonly="editStatusIn"
          :class="{ inputbgc: editStatusIn === true }" @click="getpunchInTime" />
        <!-- <p class="errMessage" v-if="messageFlgIn">未入力</p> -->
      </el-form-item>
      <el-form-item label="退勤" :label-width="formLabelWidth">
        <el-input v-model="form.punchout" autocomplete="off" :readonly="editStatusOut"
          :class="{ inputbgc: editStatusOut === true }" @click="getpunchOutTime" />
        <!-- <p class="errMessage" v-if="messageFlgOut">未入力</p> -->
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">キャンセル</el-button>
        <el-button type="primary" @click="submit">確定</el-button>
      </span>
    </template>
  </el-dialog>





</template>


<script lang="ts" setup>
import { ref, onMounted, reactive } from 'vue'
import { getRecordApi, punchCardApi } from '../../request/api'
import Cookie from 'js-cookie'

const dialogFormVisible = ref(false)
const formLabelWidth = '100px'
const form = reactive({
  punchin: '',
  punchout: ''
})

let editStatusIn = ref(false);
let editStatusOut = ref(false);
// let messageFlgIn = ref(false);
// let messageFlgOut = ref(false);

function editStatusCheck(date: string) {
  let today = ref(new Date());
  let now = String(today.value.getFullYear()) + '-' + String(today.value.getMonth() + 1) + '-' + String(today.value.getDate());

  if (new Date(date) < new Date(now)) {
    editStatusOut.value = true
    editStatusIn.value = true

  } else {
    if (form.punchin !== '') {
      editStatusIn.value = true
    } else {
      editStatusIn.value = false
    }

    if (form.punchout !== '') {
      editStatusOut.value = true
      editStatusIn.value = true
    } else {
      editStatusOut.value = false
    }

  }


}

// let today = ref(new Date())
let now = ref(new Date())
let weeks = ref(["日", "一", "二", "三", "四", "五", "六"])
let year = ref("")
let month = ref("")
let date = ref("")
let firstDay = ref("")
let data: any = ref([])
let resubmit: number[] = reactive([])
let recordIndex: any;

onMounted(() => {
  //todo拿date请求后台，返回需要补卡的日期
  resubmit.push(1, 2, 3)
  getNow();
})
const getNow = () => {
  year.value = String(now.value.getFullYear());
  month.value = String(now.value.getMonth() + 1);
  date.value = String(now.value.getDate());
  now.value.setDate(1);
  firstDay.value = String(now.value.getDay());
  initData();
}
function getMonthDay(month: number): any {
  if ([1, 3, 5, 7, 8, 10, 12].includes(month)) {
    return 31
  } else if ([4, 6, 9, 11].includes(month)) {
    return 30
  } else if (month === 2) {
    //  判断当年是否为闰年
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
  // //todo拿date请求后台，返回需要补卡的日期
  // resubmit.push(1, 2, 3)
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
      return 1
    } else if (date1.getTime() === now.getTime()) {
      return 0
    } else if (date1.getTime() < now.getTime()) {
      // console.log(resubmit);
      // console.log(item.month + "-" +item.date);
      if (resubmit.includes(item.date)) {
        return -2
        //需要补卡
      } else {
        //不需要补卡
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

      }
      editStatusCheck(date)
    } else {

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
  let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds();
  return yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss;
}

const getpunchInTime = () => {
  if (!editStatusIn.value) {
    form.punchin = getLocalTime();
  }
}
const getpunchOutTime = () => {
  if (!editStatusOut.value) {
    form.punchout = getLocalTime();
  }
}


const submit = () => {
  // if (form.punchin.trim() === '') {
  // messageFlgIn.value = true;
  // }
  // if (form.punchin.trim() === '' && form.punchout.trim() === '') {
  // messageFlgIn.value = true;
  // messageFlgOut.value = true;
  // }

  // let punchInTime = !editStatusIn.value?form.punchin:null;
  // let punchOutTime = !editStatusOut.value?form.punchout:null;
  let punchInTime = form.punchin;
  let punchOutTime = form.punchout;
  let record: any = {
    id: recordIndex,
    username: Cookie.get('username'),
    punchInTime: punchInTime,
    punchOutTime: punchOutTime
  }

  punchCardApi(record).then(res => {
    if (res.code.value === 200) {
      console.log(res);
    }
    dialogFormVisible.value = false
  })

}

// const displayMessage = () => {
//   messageFlgIn.value = false;
//   messageFlgOut.value = false;
// }

</script>


<style scoped lang="less">
.el-button--text {
  margin-right: 15px;
}

.el-select {
  width: 300px;
}

.el-input {
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
  // grid-gap:1rem;
  background-color: #FFFFFF;
  line-height: 40px;

  div {
    text-align: center;
  }
}

// .today {
//   background: url("../assets/circle_success.png") no-repeat center center;
//   background-size: 95% 95%;
//   position: relative;
// }

.otherDay {
  // background: url("../assets/circle_error.png") no-repeat center center;
  // background-size: 2% 2%;
  position: relative;
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

.calenderDiv {
  width: 50%;
  position: relative;
  top: 10%;
  left: 20%;
}

.center {
  text-align: center;
}

.future {
  background-color: gainsboro;
}

.inputbgc {
  background-color: blue;
}

// .errMessage {
//   color: red;
// }
</style>
