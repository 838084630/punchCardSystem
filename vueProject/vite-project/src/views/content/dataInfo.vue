<template>
  <div class="queryMonthDiv">
    <div class="child">
      <el-date-picker v-model="selectedMonth" type="month" placeholder="Pick a month" value-format="YYYY-MM" />
    </div>
    <div class="child">
      <el-button type="danger" plain @click="queryMonthDate">検索</el-button>
    </div>
    <div class="child">
      <el-button type="warning" plain @click="exportRecord">出力</el-button>
    </div>
  </div>
  <div class="recordTbl">
    <el-table :data="tableData" table-layout="auto" row-key="date" stripe>

      <el-table-column prop="date" label="年月日" sortable style="display: flex; align-items: center" />
      <el-table-column prop="punchInTime" label="出勤時間" style="display: flex; align-items: center" />
      <el-table-column prop="punchOutTime" label="退勤時間" style="display: flex; align-items: center" />
      <el-table-column label="状態">
        <template #default="scope">
          <p v-if="scope.row.comeLate!==null" class="errMessage">遅刻</p>
          <p v-if="scope.row.leaveEarly!==null" class="errMessage">早退</p>
          <p v-if="scope.row.leaveEarly==null && scope.row.comeLate==null" class="succMessage">正常</p>
        </template>
      </el-table-column>
      
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="editEnable && scope.row.date < today" type="warning" size="small"
            @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>


  <el-dialog custom-class="reply" v-model="dialogFormVisible" title="出勤時間変更画面" width="500px" @close="displayMessage">
    <el-form :model="form">

      <el-form-item label="出勤" :label-width="formLabelWidth">
        <el-time-select v-model="form.punchin" start="00:00" step="00:01" end="24:00" placeholder="Select time" />
        <!-- <el-input v-model="form.punchin" autocomplete="off"/> -->
        <p class="errMessage" v-if="messageFlgIn">出勤時間未入力</p>
      </el-form-item>


      <el-form-item label="退勤" :label-width="formLabelWidth">
        <el-time-select v-model="form.punchout" start="00:00" step="00:01" end="24:00" placeholder="Select time" />
        <!-- <el-input v-model="form.punchout" autocomplete="off"/> -->
        <p class="errMessage" v-if="messageFlgOut">退勤時間未入力</p>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">キャンセル</el-button>
        <el-button type="primary" @click="submit()">確定</el-button>
      </span>
    </template>
  </el-dialog>
<!-- 個人情報についての出力データ確認画面 -->
  <el-dialog custom-class="reply" v-model="exportDialog" title="個人情報" width="500px">
    <el-form :model="form">

      <el-form-item label="部署コード" :label-width="formLabelWidth">
        <!-- <el-input v-model="form.punchin" autocomplete="off"/> -->
        <p class="errMessage" >部署コード</p>
      </el-form-item>


      <el-form-item label="退勤" :label-width="formLabelWidth">
        <el-time-select v-model="form.punchout" start="00:00" step="00:01" end="24:00" placeholder="Select time" />
        <!-- <el-input v-model="form.punchout" autocomplete="off"/> -->
        <p class="errMessage" v-if="messageFlgOut">退勤時間未入力</p>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">キャンセル</el-button>
        <el-button type="primary" @click="submit()">確定</el-button>
      </span>
    </template>
  </el-dialog>
  <el-backtop :right="30" :bottom="30" />
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import Cookie from 'js-cookie'
import { getRecordByMonth, punchCardApi } from '../../request/api'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router';

let router = useRouter();
let username = Cookie.get('username') || ''
let messageFlgIn = ref(false);
let messageFlgOut = ref(false);
let dialogFormVisible = ref(false)
let exportDialog = ref(false)
const selectedMonth: any = ref('')
const formLabelWidth = '50px'
const form = reactive({
  id: '',
  date: '',
  punchin: '',
  punchout: '',
  comelate: '',
  leaveearly: ''
})
interface User {
  id: string
  date: string
  punchInTime: string
  punchOutTime: string
  comeLate: number,
  leaveEarly: number
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
  console.log(tableData);
  
});

const displayMessage = () => {
  form.id = '';
  form.date = '';
  form.punchin = '';
  form.punchout = '';
  messageFlgIn.value = false;
  messageFlgOut.value = false;
}

const queryMonthDate = () => {
  tableData.length = 0
  editEnable.value = false
  getRecordByMonth(username, selectedMonth.value).then(res => {
    if (res.code.value === 200) {
      if (res.data !== null) {
        console.log(res.data);
        
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
  // console.log(index, row)
  dialogFormVisible.value = true;
  // form.punchin = row.punchInTime!==null?row.date+' '+row.punchInTime:'';
  // form.punchout = row.punchOutTime!==null?row.date+' '+row.punchOutTime:'';
  form.id = row.id !== null ? row.id : '';
  form.date = row.date !== null ? row.date : '';
  form.punchin = row.punchInTime !== null ? row.punchInTime : '';
  form.punchout = row.punchOutTime !== null ? row.punchOutTime : '';

}
//本日はどの月に所属するかを判断するロジックA
//例えば、
//today:08-25 -> 8月　true
//today:08-26 -> 9月　true
//today:09-25 -> 9月  true
//today:09-26 -> 10月 true
// function editEnableCheck() {
//   let inputYear = selectedMonth.value.slice(0, 4);
//   let inputMonth = selectedMonth.value.slice(5, 7);
//   let monthCycleWithoutZero;
//   if (inputYear === String(year)) {
//     if (dd < 25 || dd === 25) {
//       if (inputMonth === month) {
//         editEnable.value = true
//       }
//     } else if (dd > 25) {
//       monthCycleWithoutZero = mmWithoutZero + 1
//       // console.log(monthCycleWithoutZero);
//       let monthCycle = monthCycleWithoutZero < 10 ? '0' + monthCycleWithoutZero : monthCycleWithoutZero;
//       // console.log(monthCycle);
//       if (inputMonth === monthCycle) {
//         editEnable.value = true
//       }
//     }

//   }

// }
//本日はどの月に所属するかを判断するロジックB
function editEnableCheck(){
  let inputYear = selectedMonth.value.slice(0, 4);
  let inputMonth = selectedMonth.value.slice(5, 7);
  if (inputYear === String(year)) {
    if(inputMonth === month){
      editEnable.value = true
    }
  }
}

const submit = () => {
  messageFlgIn.value = form.punchin === ''
  messageFlgOut.value = form.punchout === ''
  if (!messageFlgIn.value && !messageFlgOut.value) {


    let punchInTime = form.date + ' ' + form.punchin;
    let punchOutTime = form.date + ' ' + form.punchout;
    let record: any = {
      id: form.id,
      username: username,
      punchInTime: punchInTime,
      punchOutTime: punchOutTime
    }
    console.log(record);

    punchCardApi(record).then(res => {
      if (res.code.value === 200) {
        ElMessage({
          message: '保存しました',
          type: 'success',
        })
        queryMonthDate()
        //todo reload

      } else if (res.code.value === 401) {
        router.push('/login')
      }
      dialogFormVisible.value = false
    })
  }
}

 const exportRecord = () =>{
  console.log(tableData);
  exportDialog.value= true;
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

.errMessage {
  color: red;
  font-size: 10px;
  margin: 0px;
}
.succMessage{
  color: green;
  font-size: 10px;
  margin: 0px;
}
</style>
