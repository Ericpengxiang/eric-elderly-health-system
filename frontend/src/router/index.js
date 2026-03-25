import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { public: true },
  },
  {
    path: '/',
    component: () => import('@/views/layout/LayoutView.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
      },
      // 体征管理
      {
        path: 'vital-signs',
        name: 'vital-signs',
        component: () => import('@/views/vitalsign/VitalSignView.vue'),
      },
      // 健康档案
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/profile/HealthProfileView.vue'),
      },
      // 风险评估
      {
        path: 'assessment',
        name: 'assessment',
        component: () => import('@/views/assessment/AssessmentView.vue'),
      },
      // 预警管理
      {
        path: 'alerts',
        name: 'alerts',
        component: () => import('@/views/alert/AlertView.vue'),
      },
      // 预约挂号
      {
        path: 'appointments',
        name: 'appointments',
        component: () => import('@/views/appointment/AppointmentView.vue'),
      },
      // 数据统计（管理员）
      {
        path: 'statistics',
        name: 'statistics',
        component: () => import('@/views/statistics/StatisticsView.vue'),
        meta: { roles: ['ADMIN'] },
      },
      // 用户管理（管理员）
      {
        path: 'users',
        name: 'users',
        component: () => import('@/views/users/UserManageView.vue'),
        meta: { roles: ['ADMIN'] },
      },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const user = useUserStore()
  if (to.meta.public) {
    next()
    return
  }
  if (to.meta.requiresAuth && !user.accessToken) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  // 角色权限检查
  if (to.meta.roles && user.user && !to.meta.roles.includes(user.user.role)) {
    next({ name: 'dashboard' })
    return
  }
  next()
})

export default router
