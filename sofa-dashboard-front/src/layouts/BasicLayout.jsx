import 'ant-design-pro/dist/ant-design-pro.css';
import React, { useState } from 'react';
import Link from 'umi/link';
import Redirect from 'umi/redirect';
import { connect } from 'dva';
import ProLayout from '@ant-design/pro-layout';
import { Typography } from 'antd';

const menuItemRender = (menuItemProps, defaultDom) => (
  <Link to={menuItemProps.path}>{defaultDom}</Link>
);

const rightContentRender = () => {
  const style = {
    float: 'right',
    margin: '0 24px',
  };
  return (
    <div style={style}>
      <span>Ma Yun</span>
    </div>
  );
};

const footerRender = () => {
  const style = {
    margin: '2em 0',
    textAlign: 'center',
  };
  return (
    <Typography.Text
      style={style}
      type="secondary">
      SOFA Stack
    </Typography.Text>
  );
};

const IndexLayout = props => {
  const { children, location, user } = props;
  const [collapsed, setCollapsed] = useState(false);

  if (location.pathname === '/login') {
    return children;
  }

  if (!user.userid) {
    return <Redirect to="/login" />;
  }

  return (
    <ProLayout
      title="SOFA Dashboard"
      logo="https://www.sofastack.tech/img/icons/sofadashboard.png"
      theme="light"
      menu={{ locale: false }}
      collapsed={collapsed}
      onCollapse={status => setCollapsed(status)}
      menuItemRender={menuItemRender}
      rightContentRender={rightContentRender}
      footerRender={footerRender}
      {...props}>
      {children}
    </ProLayout>
  );
};

export default connect(({ user }) => ({
  user,
}))(IndexLayout);
