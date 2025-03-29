import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout/Layout.vue'
import Home from '@/pages/Main/Home.vue'
import Cart from '@/pages/Cart.vue'
import NormalCart from '@/pages/Cart.vue'
import SubCart from '@/pages/Cart.vue'
import Detail from '@/pages/Cart.vue'
import ItemList from '@/pages/Cart.vue'
import LogIn from '@/pages/Cart.vue'
import MyPage from '@/pages/Cart.vue'
import NotFound from '@/pages/Cart.vue'
import Payment from '@/pages/Cart.vue'
import SearchList from '@/pages/Cart.vue'
import SignUp from '@/pages/Cart.vue'
import SubPayment from '@/pages/Cart.vue'
import NoteReview from '@/pages/Cart.vue'
import NoteTalk from '@/pages/Cart.vue'
import OrderDetail from '@/pages/Cart.vue'
import NormalOrder from '@/pages/Cart.vue'
import SubscriptionOrder from '@/pages/Cart.vue'
import UserInfo from '@/pages/Cart.vue'
import WishList from '@/pages/Cart.vue'
import SubManage from '@/pages/Cart.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: Layout,
      children: [
        { path: '', component: Home },
        {
          path: 'cart',
          component: Cart,
          children: [
            { path: 'normal', component: NormalCart },
            { path: 'subscription', component: SubCart }
          ]
        },
        { path: 'detail/:id', component: Detail },
        { path: 'list', component: ItemList },
        { path: 'login', component: LogIn },
        {
          path: 'mypage',
          component: MyPage,
          children: [
            { path: 'user-info', component: UserInfo },
            { path: 'order/subscription', component: SubscriptionOrder },
            { path: 'order/normal', component: NormalOrder },
            { path: 'order/:id', component: OrderDetail },
            { path: 'sub-manage', component: SubManage },
            { path: 'wish', component: WishList },
            { path: 'note/review', component: NoteReview },
            { path: 'note/talk', component: NoteTalk }
          ]
        },
        { path: 'search', component: SearchList },
        { path: 'signup', component: SignUp },
        { path: 'pay/normal', component: Payment },
        { path: 'pay/subscription', component: SubPayment },
        { path: ':pathMatch(.*)*', component: NotFound }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  window.scrollTo(0, 0)
  next()
})

export default router