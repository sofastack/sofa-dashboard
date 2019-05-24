import React, { Component } from 'react';
import { connect } from 'dva';
import { Card, Table } from 'antd';
import DescriptionList from '@/components/DescriptionList';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import styles from './ServiceDetails.less';

const { Description } = DescriptionList;

const operationTabList = [
  {
    key: 'providerTab',
    tab: '服务提供者',
  },
  {
    key: 'consumerTab',
    tab: '服务消费者',
  },
];
// 服务提供方列表表头
const columns = [
  {
    title: 'IP',
    dataIndex: 'address',
    key: 'address',
  },
  {
    title: '端口',
    dataIndex: 'port',
    key: 'port',
  },
  {
    title: '权重',
    dataIndex: 'weight',
    key: 'weight',
  },
  {
    title: '应用',
    dataIndex: 'appName',
    key: 'appName',
  },
];

// 服务消费者列表表头
const columnsConsumor = [
  {
    title: 'IP',
    dataIndex: 'address',
    key: 'address',
  },
  {
    title: '端口',
    dataIndex: 'port',
    key: 'port',
  },
  {
    title: '应用',
    dataIndex: 'appName',
    key: 'appName',
  },
];

@connect(({ servicemng }) => ({
  servicemng,
}))
class ServiceDetails extends Component {
  state = {
    operationkey: 'providerTab',
    dataid: '',
  };

  componentDidMount() {
    const { dispatch, location } = this.props;
    const queryParams = location.query;
    const hash = location.hash;
    this.setState({
      dataid: queryParams.dataid+hash,
    });
    dispatch({
      type: 'servicemng/fetchProviderDetails',
      payload: {
        dataid: encodeURIComponent(queryParams.dataid+hash),
      }
    });
    dispatch({
      type: 'servicemng/fetchConsumerDetails',
      payload: {
          dataid: encodeURIComponent(queryParams.dataid+hash),
      }
    });
  }

  onOperationTabChange = key => {
    this.setState({ operationkey: key });
  };

  render() {
    const { operationkey, dataid } = this.state;
    const { servicemng, loading } = this.props;
    const { providerInfos, consumerInfos } = servicemng;
    const contentList = {
      providerTab: (
        <Table pagination={false} loading={loading} dataSource={providerInfos} columns={columns} />
      ),
      consumerTab: (
        <Table
          pagination={false}
          loading={loading}
          dataSource={consumerInfos}
          columns={columnsConsumor}
        />
      ),
    };

    return (
      <PageHeaderWrapper>
        <Card title="服务信息" style={{ marginBottom: 24 }} bordered={false}>
          <DescriptionList style={{ marginBottom: 24 }}>
            <Description term="服务ID">{dataid}</Description>
          </DescriptionList>
        </Card>
        <Card
          className={styles.tabsCard}
          bordered={false}
          tabList={operationTabList}
          onTabChange={this.onOperationTabChange}
        >
          {contentList[operationkey]}
        </Card>
      </PageHeaderWrapper>
    );
  }
}

export default ServiceDetails;
