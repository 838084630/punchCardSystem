import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';
// import Vue from 'vue';
// Vue.use(VueRouter)
import { App } from 'vue'

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'start',
        component: () => import('../views/login/login.vue')
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/login/login.vue')
    },
    {
        path: '/homepage',
        name: 'homepage',
        component: () => import('../views/homepage/homepage.vue'),
        redirect:'/calander',
        children: [
            {
                path: '/calander',
                name: 'calander',
                component: () => import('../views/content/calander.vue'),
            },
            {
                path: '/dataInfo',
                name: 'dataInfo',
                component: () => import('../views/content/dataInfo.vue'),
            },
            {
                path: '/userInfo',
                name: 'userInfo',
                component: () => import('../views/content/userInfo.vue'),
            },
            {
                path: '/export',
                name: 'export',
                component: () => import('../views/content/export.vue'),
            }
        ]
    },
    // {
    //     path:'/map',
    //     name:'map',
    //     component: () => import('../components/map.vue')
    // },
    // {
    //     path:'/homepage',
    //     name:'homepage',
    //     component: () => import('../views/homepage/homepage.vue')
    // }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes //路由配置
})

export const initRouter = (app: App<Element>) => {
    app.use(router);
}
// export default router