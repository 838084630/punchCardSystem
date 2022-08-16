import { createApp, VueElement } from 'vue'
import './style.css'
import App from './App.vue'
// import less from 'less'
import { initRouter } from './router';
// import { initStore } from './store';
import ElementPlus  from 'element-plus';
import 'element-plus/dist/index.css';


const app =createApp(App).use(ElementPlus);
// app.use(router);
//路由封装到index.ts文件的方法里,防止别的地方调用
initRouter(app);
// initStore(app);
app.mount('#app');
