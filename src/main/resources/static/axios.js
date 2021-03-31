//axios配置
//axios.defaults.baseURL = "http://localhost:9999/api"
axios.defaults.baseURL = "http://localhost:9988/api"

//axios请求拦截器,增加header
axios.interceptors.request.use(config => {
    config.headers.Authorization = window.sessionStorage.getItem("token")
    return config
})

//axios响应拦截器,统一处理token验证问题
axios.interceptors.response.use(
    response => {
        if (response.status === 200) {
            return Promise.resolve(response)
        } else {
            return Promise.reject(response)
        }
    },
    error => {
        if (error.response.status) {
            switch (error.response.status) {
                // 401: 未登录
                // 未登录则跳转登录页面，并携带当前页面的路径
                // 在登录成功后返回当前页面，这一步需要在登录页操作。
                case 401:
                    router.replace({
                        path: '/login',
                        query: {
                            redirect: router.currentRoute.fullPath
                        }
                    });
                    break;

                // 403 token过期
                // 登录过期对用户进行提示
                // 清除本地token和清空vuex中token对象
                // 跳转登录页面
                case 403:
                    ELEMENT.Message.error('登录过期，请重新登录');
                    // 清除token
                    window.sessionStorage.removeItem('token');
                    // 跳转登录页面，并将要浏览的页面fullPath传过去，登录成功后跳转需要访问的页面
                    setTimeout(() => {
                        router.replace({
                            path: '/login',
                            query: {
                                redirect: router.currentRoute.fullPath
                            }
                        });
                    }, 1000);
                    break;

                // 404请求不存在
                case 404:
                    ELEMENT.Message.error('网络请求不存在');
                    break;
                // 其他错误，直接抛出错误提示
                default:
                    ELEMENT.Message.success(error.response.data.msg);
            }
        }
        return Promise.reject(error.response);
    }
)

Vue.prototype.$http = axios