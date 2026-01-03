import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import BlogList from '../views/BlogList.vue'
import BlogDetail from '../views/BlogDetail.vue'
import Archive from '../views/Archive.vue'
import Types from '../views/Types.vue'
import Tags from '../views/Tags.vue'
import About from '../views/About.vue'
import AdminLogin from '../views/admin/Login.vue'
import AdminRegister from '../views/admin/Register.vue'
import AdminDashboard from '../views/admin/Dashboard.vue'
import AdminBlogList from '../views/admin/BlogList.vue'
import AdminBlogEdit from '../views/admin/BlogEdit.vue'
import AdminTypeList from '../views/admin/TypeList.vue'
import AdminTagList from '../views/admin/TagList.vue'
import AdminProfile from '../views/admin/Profile.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/blog/:id',
    name: 'BlogDetail',
    component: BlogDetail
  },
  {
    path: '/archive',
    name: 'Archive',
    component: Archive
  },
  {
    path: '/types',
    name: 'Types',
    component: Types
  },
  {
    path: '/tags',
    name: 'Tags',
    component: Tags
  },
  {
    path: '/about',
    name: 'About',
    component: About
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  {
    path: '/admin/register',
    name: 'AdminRegister',
    component: AdminRegister
  },
  {
    path: '/admin',
    component: AdminDashboard,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/admin/blogs'
      },
      {
        path: 'blogs',
        name: 'AdminBlogList',
        component: AdminBlogList
      },
      {
        path: 'blog/edit/:id?',
        name: 'AdminBlogEdit',
        component: AdminBlogEdit
      },
      {
        path: 'types',
        name: 'AdminTypeList',
        component: AdminTypeList
      },
      {
        path: 'tags',
        name: 'AdminTagList',
        component: AdminTagList
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: AdminProfile
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const userId = sessionStorage.getItem('userId')
    if (!userId) {
      next('/admin/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router

