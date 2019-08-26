export default [
  {
    path: '/',
    component: '../layouts/BasicLayout',
    routes: [
      { path: '/', redirect: '/dashboard/overview' },
      {
        path: '/dashboard',
        name: '应用面板',
        icon: 'appstore',
        routes: [
          {
            path: 'overview',
            name: '应用列表',
            component: 'Overview',
          },
          {
            path: 'instance',
            name: '实例列表',
            component: 'Instance',
          },
          {
            path: 'instance/*',
            component: './Instance/actuator',
            hideInMenu: true
          },
        ]
      },
      {
        path: '/governance',
        name: '服务治理',
        icon: 'appstore',
        routes: [
          {
            path: 'governance',
            name: '服务列表',
            component: 'Governance',
          },
          {
            path: 'details',
            component: './Governance/details',
            hideInMenu: true
          },
        ]
      },
      {
        path: '/ark',
        name: '动态模块',
        icon: 'appstore',
        routes: [
          {
            path: 'ark',
            name: '模块列表',
            component: './Ark/index',
          },
          {
            path: 'app',
            hideInMenu: true,
            component: './Ark/arkapp',
          },
        ]
      },
      // {
      //   path: '/demo-page',
      //   name: 'DemoPage',
      //   icon: 'build',
      //   component: 'DemoPage',
      //   routes: [
      //     {
      //       path: 'p1',
      //       name: 'p1',
      //       component: 'DemoPage',
      //       routes: [
      //         {
      //           path: 'p2',
      //           name: 'p2',
      //           component: 'DemoPage',
      //         },
      //       ]
      //     },
      //     {
      //       path: 'p3',
      //       name: 'p3',
      //       component: 'DemoPage',
      //     }
      //   ]
      // },

      // 登录注册页面，不展示在菜单侧边栏
      {
        path: '/login',
        exact: true,
        component: 'Login',
      },

    ],
  }
];
