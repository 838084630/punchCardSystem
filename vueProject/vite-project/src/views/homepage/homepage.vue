<template>
    <div class="layout">
        <div class="homepage_menu" v-if="screenFlg">
            <el-menu active-text-color="#ffd04b" background-color="#545c64" text-color="#fff" router
                default-active="/calander" class="el-menu-vertical-demo" :collapse="isCollapse" @open="handleOpen"
                @close="handleClose">
                <el-menu-item index="/calander">
                    <el-icon>
                        <Calendar />
                    </el-icon>
                    <template #title>出勤カレンダー</template>
                </el-menu-item>

                <el-menu-item index="/dataInfo">
                    <el-icon>
                        <TrendCharts />
                    </el-icon><template #title>勤務状況分析</template>
                </el-menu-item>
                <el-menu-item index="/userInfo">
                    <el-icon>
                        <Tools />
                    </el-icon><template #title>個人情報設定</template>
                </el-menu-item>
                <el-menu-item index="/export">
                    <el-icon>
                        <Document />
                    </el-icon><template #title>データ入出力</template>
                </el-menu-item>

            </el-menu>
        </div>
        <div class="homepage_menu2" v-if="!screenFlg">
            <el-drawer v-model="drawer" :with-header="false" direction="ltr" size="60%" ref="navDrawer">
                <el-menu @select="menuActive" active-text-color="#ffd04b" background-color="#545c64" text-color="#fff"
                    router default-active="/calander" class="el-menu-vertical-demo" :collapse="isCollapse"
                    @open="handleOpen" @close="handleClose">
                    <el-menu-item index="/calander">
                        <el-icon>
                            <Calendar />
                        </el-icon>
                        <template #title>出勤カレンダー</template>
                    </el-menu-item>

                    <el-menu-item index="/dataInfo">
                        <el-icon>
                            <TrendCharts />
                        </el-icon><template #title>勤務状況分析</template>
                    </el-menu-item>
                    <el-menu-item index="/userInfo">
                        <el-icon>
                            <Tools />
                        </el-icon><template #title>個人情報設定</template>
                    </el-menu-item>
                    <el-menu-item index="/export">
                        <el-icon>
                            <Document />
                        </el-icon><template #title>データ入出力</template>
                    </el-menu-item>

                </el-menu>
            </el-drawer>
        </div>
        <div class="right" :class="{ isActive: isCollapse }">
            <div class="homepage_header">
                <el-icon v-if='!iconShow' style="margin-left: 16px;margin-top:16px" @click="drawer = true">
                    <Menu />
                </el-icon>
                <el-icon v-if='!isCollapse && iconShow' @click="changeMenu" class="iconfont">
                    <Fold />
                </el-icon>
                <el-icon v-if='isCollapse && iconShow' @click="changeMenu" class="iconfont">
                    <Expand />
                </el-icon>
                <span class="topInfo">ようこそ、<b>{{ username }}</b>様<el-button type="warning" class="logout"
                        @click="centerDialogVisible = true">
                        ログアウト</el-button></span>
            </div>
            <div class="homepage_content">
                <router-view></router-view>
                <div class="dialog_out">
                    <el-dialog custom-class="reply" v-model="centerDialogVisible" title="ログアウトしますか？" width="30%" center>
                        <template #footer>
                            <span class="dialog-footer">
                                <el-button @click="centerDialogVisible = false">キャンセル</el-button>
                                <el-button type="primary" @click="confirm">確定</el-button>
                            </span>
                        </template>
                    </el-dialog>
                </div>
            </div>
        </div>

    </div>

</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import Cookie from 'js-cookie'
import { useRouter } from 'vue-router';
import {
    Document,
    Menu,
    Calendar,
    Tools,
    TrendCharts,
    Fold,
    Expand,
} from '@element-plus/icons-vue'
const drawer = ref(false)
let iconShow = ref(true)
let screenWidth = document.body.clientWidth
let screenFlg = ref(true)
if (screenWidth < 600) {
    screenFlg.value = false
    iconShow.value = false;
}

const menuActive = () => {
    drawer.value = false;
}

const isCollapse = ref(false)
const handleOpen = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
let router = useRouter();
let centerDialogVisible = ref(false);
let username = Cookie.get('username')
let opends: Array<string> = ['1'];

const changeMenu = () => {
    isCollapse.value = !isCollapse.value
}

const confirm = () => {
    Cookie.remove('username')
    Cookie.remove('token')
    centerDialogVisible.value = false;
    router.push('/login')
}

</script>

<style lang="less" scoped>
@media screen and (max-width: 20000px) {
    :deep(.reply) {
        width: 25% !important;
        min-width: 100px !important;
    }

    .right {
        margin-left: 200px;
    }

    .isActive {
        margin-left: 64px;
    }

}

@media screen and (max-width: 600px) {
    :deep(.reply) {
        width: 60% !important;
        min-width: 100px !important;
    }

    .right {
        margin-left: 0;
    }

    .isActive {
        margin-left: 0;
    }

}

:deep(.el-drawer__body) {
    background-color: #545c64;
}

.layout {
    .homepage_menu {
        background: #545c64;
        position: fixed;
        top: 0;
        bottom: 0;
    }

    .right {

        // margin-left: 200px;
        .homepage_header {
            color: white;
            background-color: #1e78bf;
            height: 50px;
        }
    }

    // .isActive{
    //     margin-left: 64px;
    // }
}


.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
}

.dialog-footer button:first-child {
    margin-right: 10px;
}

.iconfont {
    font-size: 20px;

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
</style>