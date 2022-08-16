<template>
    <!-- <Child :username="username"></Child> -->
    <div class="homepage_container">
        <div class="homepage_header">
            <img src='../../assets/vueIcon.jpeg'>
            <span class="title">Punch Card System</span>
            <span class="topInfo">ようこそ、<b>{{ username }}</b>様<el-button type="warning" class="logout"
                    @click="centerDialogVisible = true">
                    ログアウト</el-button></span>

        </div>
        <div class="homepage_menu">

            <el-menu active-text-color="#ffd04b" background-color="#545c64" class="el-menu-vertical-demo"
                default-active="1" text-color="#fff" @open="handleOpen" @close="handleClose" :default-openeds="opends">
                <el-sub-menu index="1">
                    <template #title>
                        <el-icon>
                            <Menu />
                        </el-icon>
                        <span>Navigator</span>
                    </template>
                    <div v-for="(item, index) in menuData" :key="index" @click="clickcategory(index)">
                        <el-menu-item :index=item.index :class="{ active: categoryIndex == index }">
                            <router-link :to=item.to>
                                {{ item.title }}
                            </router-link>
                        </el-menu-item>
                    </div>
                    <!-- <el-menu-item index="1-1">
                        <router-link to="/calander">
                            <el-icon>
                                <Calendar />
                            </el-icon>出勤カレンダー
                        </router-link>
                    </el-menu-item> -->
                    <!-- <el-menu-item index="1-2">
                        <router-link to="/dataInfo">
                            <el-icon>
                                <TrendCharts />
                            </el-icon>勤務状況分析
                        </router-link>
                    </el-menu-item>
                    <el-menu-item index="1-3">
                        <router-link to="/userInfo">
                            <el-icon>
                                <Tools />
                            </el-icon>個人情報設定
                        </router-link>

                    </el-menu-item>
                    <el-menu-item index="1-4">
                        <router-link to="/export">
                            <el-icon>
                                <Document />
                            </el-icon>データ入出力
                        </router-link>
                    </el-menu-item> -->
                </el-sub-menu>
            </el-menu>
        </div>
        <div class="homepage_content">
            <router-view></router-view>
        </div>
    </div>

    <el-dialog v-model="centerDialogVisible" title="ログアウトしますか？" width="30%" center>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="centerDialogVisible = false">キャンセル</el-button>
                <el-button type="primary" @click="confirm">確定</el-button>
            </span>
        </template>
    </el-dialog>

</template>

<script lang="ts" setup>
import { reactive, ref, toRefs } from 'vue'
import Cookie from 'js-cookie'
import { useRouter } from 'vue-router';
import {
    Document,
    Menu,
    Calendar,
    Tools,
    TrendCharts
} from '@element-plus/icons-vue'
let router = useRouter();
let centerDialogVisible = ref(false);
let username = Cookie.get('username')
let opends: Array<string> = ['1'];

let categoryIndex = ref(0);


let menuData = reactive([
    {
        index: "1",
        to: "/calander",
        component: "<Calendar/>",
        title: "出勤カレンダー"
    },
    {
        index: "2",
        to: "/dataInfo",
        component: { TrendCharts },
        title: "勤務状況分析"
    },
    {
        index: "3",
        to: "/userInfo",
        component: "userInfo",
        title: "個人情報設定"
    },
    {
        index: "4",
        to: "/export",
        component: "Document",
        title: "データ入出力"
    },

]);


const clickcategory = (index: any) => { // 这里我们传入一个当前值

    categoryIndex.value = index

}

const handleOpen = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}

const confirm = () => {
    Cookie.remove('username')
    Cookie.remove('token')
    centerDialogVisible.value = false;
    router.push('/login')
}

</script>

<style lang="less" scoped>
.dialog-footer button:first-child {
    margin-right: 10px;
}

.title {
    padding-left: 30%;
    padding-bottom: 5%;
    font-size: 50px;
}

img {
    width: 7%;
}

.homepage_container {
    position: relative;
    height: 100%;

    .homepage_header {
        height: 10%;
        background-color: white;
    }

    .homepage_menu {
        position: absolute;
        top: 10%;
        left: 0;
        bottom: 0;
        width: 15%;
        background-color: beige;
    }

    .el-menu {
        height: 100%;

    }

    .homepage_content {
        position: absolute;
        top: 10%;
        right: 0;
        // left: 250px;
        bottom: 0;
        width: 85%;
        background-color: gainsboro
    }
}

.topInfo {
    position: absolute;
    top: 2%;
    right: 2%;
}

.logout {
    margin-left: 20px;
}

b {
    color: skyblue
}

a {
    color: white;
}

.active {

    background: rgb(235, 63, 63)
}
</style>