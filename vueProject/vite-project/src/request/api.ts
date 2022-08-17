import request from './request'

//用类型别名简化Promise
type PromiseRes<T> = Promise<ManageResult<T>>

interface AdminLoginData {
    username: string,
    password: string
}
interface NameAndtoken {
    username: string,
    token: string
}
//登录返回token
interface ManageResult<T> {
    code: {
      value:number,
      describe:string  
    },
    data: T
    // message: string
}
//登陆的返回接口
interface AdminLoginRes{
    token: string;
    // tokenHead: string
}
//当前的用户信息
interface AdminInfoRes{
    menus: []
}
interface OnlyCode{
    code: {
        value:number,
        describe:string  
      }
}

//用户当前的打卡记录
interface Record{
    id:number;
    username:string;
    punchInTime:string;
    punchOutTime:string;
}

interface ThisYearAndMonth{
    record:number[];
}
export const adminLoginApi = (data: AdminLoginData): PromiseRes<AdminLoginRes> => request.post('/user/login', data)
export const userCheckApi = (data: NameAndtoken): PromiseRes<AdminLoginRes> => request.post('/user/userCheck', data)
export const getAdminInfoApi = (): PromiseRes<AdminInfoRes> => request.get('/user/info')
export const getRecordApi = (data:string,data2:string):PromiseRes<ManageResult<Record>> => request.get('/user/record?username='+data+'&date='+data2)
export const punchCardApi = (data: Record): PromiseRes<OnlyCode> => request.post('/user/punchCard', data)
export const getAbsenceApi = (data: string,data2:string,date3:number):PromiseRes<ManageResult<ThisYearAndMonth>> =>request.get('/user/absence?username='+data+'&time='+data2+'&days='+date3)
