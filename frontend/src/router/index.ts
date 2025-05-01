import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

// 定义路由组件
const Home = () => import('../views/Home.vue');
const Guide = () => import('../views/Guide.vue');
const About = () => import('../views/About.vue');
const AboutAlgorithm = () => import('../views/AboutAlgorithm.vue');
const Login = () => import('../views/Login.vue');
const Register = () => import('../views/Register.vue');
const EditList = () => import('../views/EditList.vue');
const BaseCode = () => import('../views/CodeEditor.vue');
const ProblemList = () => import('../views/ProblemList.vue');
const NormalOJ = () => import('../views/NormalOJ.vue');
const MyProfile = () => import('../views/MyProfile.vue');
const SubmissionDetail = () => import('../views/SubmissionDetail.vue');
const ClassDetail = () => import('../views/ClassDetail.vue');
const BlogHome = () => import('../views/BlogHome.vue');
const BlogDetail = () => import('../views/BlogDetail.vue');
const QADetail = () => import('../views/QADetail.vue');
const MyBlog = () => import('../views/MyBlog.vue');
const Editor = () => import('../views/Editor.vue');

// 定义路由
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/guide',
    name: 'Guide',
    component: Guide
  },
  {
    path: '/about',
    name: 'About',
    component: About
  },
  {
    path: '/aboutalgorithm',
    name: 'AboutAlgorithm',
    component: AboutAlgorithm
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/basecode',
    name: 'BaseCode',
    component: BaseCode,
    meta: { requiresAuth: true } 
  },
  {
    path: '/editlist',
    name: 'EditList',
    component: EditList,
    meta: { requiresAuth: true } 
  },
  {
    path: '/problemlist',
    name: 'ProblemList',
    component: ProblemList,
    meta: { requiresAuth: true } 
  },
  {
    path: '/normalOJ',
    name: 'NormalOJ',
    component: NormalOJ,
    meta: { requiresAuth: true } 
  },
  {
    path: '/profile',
    name: 'MyProfile',
    component: MyProfile,
    meta: { requiresAuth: true } 
  },
  {
    path: '/submissiondetail',
    name: 'SubmissionDetail',
    component: SubmissionDetail,
    meta: { requiresAuth: true } 
  },
  {
    path: '/classdetail',
    name: 'ClassDetail',
    component: ClassDetail,
    meta: { requiresAuth: true } 
  }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
});

//全局路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要登录
  if (to.meta.requiresAuth) {
    const isLoggedIn = localStorage.getItem('token'); // 检查本地存储中的 token
    if (!isLoggedIn) {
      // 如果没有登录，跳转到登录页面
      next('/login');
    } else {
      // 如果已登录，继续导航
      next();
    }
  } else {
    // 如果不需要登录，直接继续导航
    next();
  }
});

export default router;
