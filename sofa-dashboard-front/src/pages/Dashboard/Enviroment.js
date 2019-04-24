import React, { Component } from 'react';
import { connect } from 'dva';
import { Card, Table, Divider } from 'antd';
import DescriptionList from '@/components/DescriptionList';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import styles from './Enviroment.less';

const { Description } = DescriptionList;
// profile 的数据会被挂在到 this.props
@connect(({ config, loading }) => ({
  config,
  loading: loading.effects['config/fetchBasic'],
}))
class Enviroment extends Component {
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'config/fetchBasic',
    });
  }

  render() {
    const { config, loading } = this.props;
    const { appProps, appSysEnv, appSysProps } = config;
    let goodsData = [];
    let appsData = [];
    let systemPropertiesData = [];
    if (appProps.length) {
      appsData = appSysEnv;
      goodsData = appProps;
      systemPropertiesData = appSysProps;
    }
    const renderContent = (value, row, index) => {
      const obj = {
        children: value,
        props: {},
      };
      if (index === appProps.length) {
        obj.props.colSpan = 0;
      }
      return obj;
    };
    const goodsColumns = [
      {
        title: '配置名',
        dataIndex: 'name',
        key: 'name',
        render: renderContent,
      },
      {
        title: '配置值',
        dataIndex: 'value',
        key: 'value',
        render: renderContent,
      },
    ];
    return (
      <PageHeaderWrapper title="Enviroment">
        <Card bordered={false}>
          <DescriptionList size="large" title="Profiles" style={{ marginBottom: 32 }}>
            <Description term="spring.profile.active">No profiles active</Description>
          </DescriptionList>
          <Divider style={{ marginBottom: 32 }} />
          <DescriptionList size="large" title="server.ports" style={{ marginBottom: 32 }}>
            <Description term="local.server.port">8083</Description>
          </DescriptionList>
          <Divider style={{ marginBottom: 32 }} />

          <div className={styles.title}>applicationConfig: [classpath:/application.properties]</div>
          <Table
            style={{ marginBottom: 24 }}
            pagination={false}
            loading={loading}
            dataSource={goodsData}
            columns={goodsColumns}
            rowKey="id"
          />

          <div className={styles.title}>systemEnvironment</div>
          <Table
            style={{ marginBottom: 24 }}
            pagination={false}
            loading={loading}
            dataSource={appsData}
            columns={goodsColumns}
            rowKey="id"
          />

          <div className={styles.title}>systemProperties</div>
          <Table
            style={{ marginBottom: 24 }}
            pagination={false}
            loading={loading}
            dataSource={systemPropertiesData}
            columns={goodsColumns}
            rowKey="id"
          />
        </Card>
      </PageHeaderWrapper>
    );
  }
}

export default Enviroment;
