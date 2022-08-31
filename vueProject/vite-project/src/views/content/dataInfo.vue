<template>
  <div class="queryMonthDiv">
    <div class="child">
      <el-date-picker v-model="selectedMonth" type="month" placeholder="Pick a month" value-format="YYYY-MM" />
    </div>
    <div class="child">
      <el-button type="danger" plain @click="queryMonthDate">検索</el-button>
    </div>
    <div class="child">
      <el-button type="warning" plain>出力</el-button>
    </div>
  </div>
  <div class="recordTbl">
    <el-table :data="tableData" table-layout="auto" row-key="date" stripe height="100%">

      <el-table-column prop="date" label="年月日" sortable style="display: flex; align-items: center" />
      <el-table-column prop="punchInTime" label="出勤時間" style="display: flex; align-items: center" />
      <el-table-column prop="punchOutTime" label="退勤時間" style="display: flex; align-items: center" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="editEnable && scope.row.date<today"
            type="warning" size="small" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>


  <el-dialog custom-class="reply" v-model="dialogFormVisible" title="出勤時間変更画面" width="500px" @close="displayMessage">
    <el-form :model="form">

      <el-form-item label="出勤" :label-width="formLabelWidth">
        <el-time-select
    v-model="form.punchin"
    start="00:00"
    step="00:01"
    end="24:00"
    placeholder="Select time"
  />
        <!-- <el-input v-model="form.punchin" autocomplete="off"/> -->
        <p class="errMessage" v-if="messageFlgIn">未入力</p>
      </el-form-item>


      <el-form-item label="退勤" :label-width="formLabelWidth">
        <el-time-select
    v-model="form.punchout"
    start="00:00"
    step="00:01"
    end="24:00"
    placeholder="Select time"
  />
        <!-- <el-input v-model="form.punchout" autocomplete="off"/> -->
        <p class="errMessage" v-if="messageFlgOut">未入力</p>
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
import { onMounted, reactive, ref } from 'vue'
import Cookie from 'js-cookie'
import { getRecordByMonth } from '../../request/api'

let username = Cookie.get('username') || ''
let messageFlgIn = ref(false);
let messageFlgOut = ref(false);
let dialogFormVisible = ref(false)
const selectedMonth: any = ref('')
const formLabelWidth = '50px'
const form = reactive({
  punchin: '',
  punchout: ''
})
interface User {
  date: string
  punchInTime: string
  punchOutTime: string
}
const tableData: User[] = reactive([])
const editEnable = ref(false)
let now = ref(new Date())
let year = now.value.getFullYear()
let mmWithoutZero = new Date().getMonth() + 1
let month = mmWithoutZero < 10 ? '0' + mmWithoutZero : mmWithoutZero;
let dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate();
selectedMonth.value = year + '-' + month
let today = year + '-' + month + '-' + dd
onMounted(() => {
  queryMonthDate()
});

const displayMessage = () => {
  form.punchin = '';
  form.punchout = '';
  messageFlgIn.value = false;
  messageFlgOut.value = false;
}

const queryMonthDate = () => {
  tableData.length = 0
  editEnable.value=false
  getRecordByMonth(username, selectedMonth.value).then(res => {
    if (res.code.value === 200) {
      if (res.data !== null) {
        let recordData: any = res.data;
        //判断一致する場合、編集できる
        editEnableCheck();
        for (let item of recordData) {
          tableData.push(item)
        }
      }
    }
  })
}
const handleEdit = (index: number, row: User) => {
  console.log(index, row)
  dialogFormVisible.value= true;
  // form.punchin = row.punchInTime!==null?row.date+' '+row.punchInTime:'';
  // form.punchout = row.punchOutTime!==null?row.date+' '+row.punchOutTime:'';
  form.punchin = row.punchInTime!==null?row.punchInTime:'';
  form.punchout = row.punchOutTime!==null?row.punchOutTime:'';

}
//本日はどの月に所属するかを判断する
//例えば、
//today:08-25 -> 8月　true
//today:08-26 -> 9月　true
//today:09-25 -> 9月  true
//today:09-26 -> 10月 true
function editEnableCheck() {
  let inputYear = selectedMonth.value.slice(0, 4);
  let inputMonth = selectedMonth.value.slice(5, 7);
  let monthCycleWithoutZero;
  if (inputYear === String(year)) {
    if (dd < 25 || dd === 25) {
      if (inputMonth === month) {
        editEnable.value = true
      }
    } else if (dd > 25) {   
      monthCycleWithoutZero = mmWithoutZero + 1
      console.log(monthCycleWithoutZero);
      let monthCycle = monthCycleWithoutZero < 10 ? '0' + monthCycleWithoutZero : monthCycleWithoutZero;
      console.log(monthCycle);
      if (inputMonth === monthCycle) {
        editEnable.value = true
      }
    }

  }

}

</script>

<style lang="less" scoped>

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

.reply .el-dialog__body {
  padding-top: 0px;
  padding-bottom: 0px;
}


.queryMonthDiv {
  display: flex;
  margin-top: 20px;
  margin-bottom: 20px;
  margin-right: 10px;
  justify-content: flex-end;

  .child {
    margin-left: 5px;
  }
}

.recordTbl {
  height: 100%;
}
</style>
