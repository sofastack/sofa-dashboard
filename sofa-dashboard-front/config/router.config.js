export default [
  // app
  {
    path: '/',
    component: '../layouts/BasicLayout',
    Routes: ['src/pages/Authorized'],
    authority: ['admin', 'user'],
    routes: [
      { path: '/', redirect: '/dashboard/workplace' },
      {
        path: '/dashboard',
        name: 'dashboard',
        icon: 'dashboard',
        routes: [
          {
            path: '/dashboard/workplace',
            name: 'workplace', //工作台，这里因为有国际化原因，保留这里的name值，如果要换，则在国际化配置文件中配置相应的值
            component: './List/BasicList',
          },
          {
            path: '/dashboard/details',
            hideInMenu:true,
            component: './Dashboard/Environment',
          },
          {
              path: '/dashboard/metrics',
              hideInMenu:true,
              component: './Dashboard/Metrics',
          },
          {
              path: '/dashboard/traces',
              hideInMenu:true,
              component: './Dashboard/Traces',
          },
        ],
      },
     // servicemng
      {
        path: '/servicemng',
        name: 'servicemng',
        icon: 'dashboard',
        routes: [
          {
            path: '/servicemng/service-list',
            name: 'list', //工作台，这里因为有国际化原因，保留这里的name值，如果要换，则在国际化配置文件中配置相应的值
            component: './Servicemng/Servicemng',
          },

          {
            path: '/servicemng/service-details',
            hideInMenu:true,
            component: './Servicemng/ServiceDetails',
          },
        ],
      },
       // sofaarkmng
       {
        path: '/sofaarkmng',
        name: 'sofaarkmng',
        icon: 'dashboard',
        routes: [
          {
            path: '/sofaarkmng/ark-modules',
            name: 'modules', //工作台，这里因为有国际化原因，保留这里的name值，如果要换，则在国际化配置文件中配置相应的值
            component: './Sofaarkmng/Sofaarkmng',
          },
          {
            path: '/sofaarkmng/ark-apps',
            name: 'modules', //工作台，这里因为有国际化原因，保留这里的name值，如果要换，则在国际化配置文件中配置相应的值
            hideInMenu:true,
            component: './Sofaarkmng/Apppluginmng',
          },
        ],
      },
      {
        component: '404',
      },
    ],
  },
];
