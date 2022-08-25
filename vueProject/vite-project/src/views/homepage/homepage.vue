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
                <el-menu @select="menuActive" 
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
                        </el-icon><template #title>勤務状況一覧</template>
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
                <div class="headerLeft" style="margin-left: 16px">
                    <el-icon v-if='!iconShow' @click="drawer = true">
                        <Menu />
                    </el-icon>
                    <el-icon v-if='!isCollapse && iconShow' @click="changeMenu" class="iconfont">
                        <Fold />
                    </el-icon>
                    <el-icon v-if='isCollapse && iconShow' @click="changeMenu" class="iconfont">
                        <Expand />
                    </el-icon>
                </div>
                <div class="headerRight" style="margin-right: 16px">

                    <el-dropdown>
                        <span class="el-dropdown-link">
                            <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item divided class="nameCenter"><span><b>{{ username }}</b>様</span>
                                </el-dropdown-item>
                                <el-dropdown-item divided>
                                    <el-button type="success" @click="">
                                        個人設定</el-button>
                                </el-dropdown-item>
                                <el-dropdown-item divided>
                                    <el-button type="warning" @click="centerDialogVisible = true">
                                        LOGOUT</el-button>
                                </el-dropdown-item>

                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>

                </div>
                <!-- <span class="topInfo">ようこそ、<b>{{ username }}</b>様<el-button type="warning" class="logout"
                        @click="centerDialogVisible = true">
                        ログアウト</el-button></span> -->
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
    background-color: white;
}

.layout {
    .homepage_menu {
        background: #545c64;
        position: fixed;
        top: 0;
        bottom: 0;
    }

    .right {
        .homepage_header {
            color: white;
            background-color: #1e78bf;
            height: 50px;
            display: flex;

            .headerLeft {
                justify-content: flex-start;
                display: flex;
                align-items: center;
            }

            .headerRight {
                flex: 1;
                justify-content: flex-end;
                display: flex;
                align-items: center;
                
            }
        }
    }

}

:deep(.el-dropdown-menu__item) {
                justify-content: center;
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

b {
    color: skyblue
}

a {
    color: white;
}

.nameCenter {
    text-align: center;
}
</style>