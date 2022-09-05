import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';
// import Vue from 'vue';
// Vue.use(VueRouter)
import { App } from 'vue'
import Cookies from "js-cookie";

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
        meta: {
            loginRequest: true
        },
        children: [
            {
                path: '/calander',
                name: 'calander',
                component: () => import('../views/content/calander.vue'),
                meta: {
                    loginRequest: true
                }
            },
            {
                path: '/dataInfo',
                name: 'dataInfo',
                component: () => import('../views/content/dataInfo.vue'),
                meta: {
                    loginRequest: true
                }
            },
            {
                path: '/userInfo',
                name: 'userInfo',
                component: () => import('../views/content/userInfo.vue'),
                meta: {
                    loginRequest: true
                }
            },
            {
                path: '/export',
                name: 'export',
                component: () => import('../views/content/export.vue'),
                meta: {
                    loginRequest: true
                }
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
});

router.beforeEach((to,from,next) => {
    if(to.meta.loginRequest){
        if(Cookies.get('token')){
            next()
        }else{
            next({
                path:'/login',
                query: {
                    redirect: to.fullPath
                }
            })
        }
    }else{
        next()
    }
})

export const initRouter = (app: App<Element>) => {
    app.use(router);
}
// export default router