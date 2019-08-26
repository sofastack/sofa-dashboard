import React from 'react';
import { Card, Table, Input, Select, Drawer, List ,Tabs} from 'antd';
import { connect } from 'dva';
const { Option } = Select;
const { Search } = Input;
const { TabPane } = Tabs;
@connect(({ governance }) => ({
  providerDetail: governance.providerDetail,
  consumerDetail: governance.consumerDetail
}))
class ServiceDetails extends React.Component {

  componentDidMount() {
    const { dispatch, location } = this.props;
    const queryParams = location.query;
    const serviceId = queryParams.serviceId + location.hash
    dispatch({
      type: 'governance/fetchProviderDetails',
      payload: {
        "dataid":serviceId,
      }
    });
  }
  
  render() {
    const {dispatch, location } = this.props;
    const queryParams = location.query;
    const serviceId = queryParams.serviceId + location.hash
    function callback(key) {
      // 1: 请求服务提供方数据
      if (key == 1) {
        dispatch({
          type: 'governance/fetchProviderDetails',
          payload: {
            "dataid":serviceId,
          }
        });
      }
      // 2: 请求服务消费方数据
      if (key == 2) {
        dispatch({
          type: 'governance/fetchConsumerDetails',
          payload: {
            "dataid":serviceId,
          }
        });
      }
    }

  // 服务提供方列表表头
  const columnsProvider = [
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
  const columnsConsumer = [
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

  return (
      <div>
          <Card bordered={false} hoverable={false} style={{ marginTop: -15, marginLeft: -15 }}>
              服务ID : {serviceId}
          </Card>
          <Card bordered={false} hoverable={false} style={{ marginTop: 5, marginLeft: -15 }}>
            <Tabs defaultActiveKey="1" onChange={callback}>
              <TabPane tab="服务提供方" key="1">
                <Table 
                    columns={columnsProvider} 
                    dataSource={this.props.providerDetail} 
                    rowKey={record => record.address}
                    />
              </TabPane>
              <TabPane tab="服务消费方" key="2">
                <Table 
                    columns={columnsConsumer} 
                    dataSource={this.props.consumerDetail} 
                    rowKey={record => record.address}
                    />
              </TabPane>
            </Tabs>
          </Card>
        </div>
    );
  }
};

export default ServiceDetails;