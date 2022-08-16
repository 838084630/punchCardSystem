import axios from "axios";
import Cookies from "js-cookie";
const instance = axios.create({
    // baseURL: 'http://120.24.64.5:8088/mall-admin',
    // baseURL: 'http://localhost:8080',
    baseURL: '/api',
    timeout:15000
});

//拦截器
instance.interceptors.request.use(config =>{
    let token = Cookies.get('token')
    if(token){
        //如果config.headers不为空就等于自己，不然赋值为空对象
        config.headers = config.headers || {}
        config.headers.token = token
    }
    return config;
}, err => {
    return Promise.reject(err);
})

instance.interceptors.response.use(result =>{
    return result.data;
}, err => {
    return Promise.reject(err);
})

export default instance;