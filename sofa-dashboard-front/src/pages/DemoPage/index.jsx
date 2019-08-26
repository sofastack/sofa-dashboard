import React from 'react';
import { Result, Icon } from 'antd';

export default function (props) {
  return (
    <Result
      icon={<Icon type="smile" theme="twoTone" />}
      title={`当前页面路径是 ${props.location.pathname}`}
    />
  );
}
