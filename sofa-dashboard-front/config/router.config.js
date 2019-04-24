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
            path: '/dashboard/env',
            hideInMenu:true,
            component: './Dashboard/Enviroment',
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
