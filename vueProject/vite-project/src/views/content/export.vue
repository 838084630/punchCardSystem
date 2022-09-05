<template>
  <div class="templet">
    <el-button type="danger" @click="showUploadDailog">アップロード</el-button>
    <el-button type="warning">ダウンロード</el-button>
  </div>

  <el-dialog v-model="centerDialogVisible" title="Warning" width="70%" center>
    <el-upload
    class="upload-demo"
    drag
    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
    multiple
  >
    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
    <div class="el-upload__text">
      Drop file here or <em>click to upload</em>
    </div>
    <template #tip>
      <div class="el-upload__tip">
        jpg/png files with a size less than 500kb
      </div>
    </template>
  </el-upload>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="centerDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="centerDialogVisible = false"
          >Confirm</el-button
        >
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'

const centerDialogVisible = ref(false)

const showUploadDailog = () => {
  centerDialogVisible.value = true
}
</script>

<style lang="less" scoped>
  .templet{
    display: flex;
    justify-content: center;
    margin: 20px;
  }
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
