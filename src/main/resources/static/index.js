//组件导入
const login = httpVueLoader('./components/login.vue');
const work = httpVueLoader('./components/work.vue');
const register = httpVueLoader('./components/register.vue')

//路由定义
const routes = [
    {path: '/', component: work},
    {path: '/work', component: work},
    {path: '/login', component: login},
    {path: '/register', component: register}
]

const router = new VueRouter({
    routes // (缩写) 相当于 routes: routes
})

//增加登录校验
router.beforeEach((to, from, next) => {
    // to将要访问的页面地址 from从哪个路径跳转而来 next()代表放行, next('/a')强制跳转到指定页面
    if (to.path === '/login' || to.path === '/register') {
        return next()
    }

    const token = window.sessionStorage.getItem('token');
    if (!token) {
        return next('/login')
    }
    next()
})