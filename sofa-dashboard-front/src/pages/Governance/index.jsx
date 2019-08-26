import React from 'react';
import { Card, Table, Input, Select, Drawer, List } from 'antd';
import { connect } from 'dva';

const { Option } = Select;
const { Search } = Input;

@connect(({ governance }) => ({
  list: governance.list,
  providerListData: governance.providerListData || [],
  consumerListData: governance.consumerListData || []
}))
class Governance extends React.Component {
  state = {
    columns: [
      {
        title: '服务ID',
        dataIndex: 'serviceId',
        key: 'serviceId',
        render: serviceId => <a href={`/governance/details?serviceId=${serviceId}`}>{serviceId}</a>,
      },
      {
        title: '提供此服务的应用',
        dataIndex: 'serviceProviderAppName',
        key: 'serviceProviderAppName',
      },
      {
        title: '服务提供者数目',
        dataIndex: 'serviceProviderAppNum',
        key: 'serviceProviderAppNum',
      },
      {
        title: '服务消费者数目',
        dataIndex: 'serviceConsumerAppNum',
        key: 'serviceConsumerAppNum',
      },
    ],
    queryType: "service",
    visible: false
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'governance/fetch',
      payload: {
        "queryType": this.state.queryType,
        "queryKey": ""
      }
    });
  }

  handleSearch = (value) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'governance/fetch',
      payload: {
        "queryKey": value,
        "queryType": this.state.queryType
      }
    });
  }

  showDrawer = (appName) => {
    this.setState({
      visible: true,
    });

    const { dispatch } = this.props;
    dispatch({
      type: 'governance/fetchServiceByAppName',
      payload: {
        "appName": appName
      }
    });
  };

  onClose = () => {
    this.setState({
      visible: false,
    });
  };

  render() {
    const appColumns = [
      {
        title: '应用名',
        dataIndex: 'appName',
        key: 'appName',
        render: appName => <a onClick={() => this.showDrawer(appName)}>{appName}</a>,
      },
    ];

    const serviceColumns = [
      {
        title: '服务ID',
        dataIndex: 'serviceId',
        key: 'serviceId',
        render: serviceId => <a href={`/governance/details?serviceId=${serviceId}`}>{serviceId}</a>,
      },
      {
        title: '提供此服务的应用',
        dataIndex: 'serviceProviderAppName',
        key: 'serviceProviderAppName',
      },
      {
        title: '服务提供者数目',
        dataIndex: 'serviceProviderAppNum',
        key: 'serviceProviderAppNum',
      },
      {
        title: '服务消费者数目',
        dataIndex: 'serviceConsumerAppNum',
        key: 'serviceConsumerAppNum',
      },
    ];

    const handleChange = (value) => {
      const { dispatch } = this.props;
      if (value === 'app') {
        this.setState({
          columns: appColumns,
          queryType: "app"
        });
        dispatch({
          type: 'governance/fetch',
          payload: {
            "queryType": "app",
            "queryKey": ""
          }
        });

      } else {
        this.setState({
          columns: serviceColumns,
          queryType: "service"
        });
        dispatch({
          type: 'governance/fetch',
          payload: {
            "queryType": "service",
            "queryKey": ""
          }
        });
      }
    }

    return (
      < div >
        <Card bordered={false} hoverable={false} style={{ marginTop: -15, marginLeft: -15 }}>
          <Select defaultValue="service" style={{ width: 120, marginRight: 10 }} onChange={handleChange}>
            <Option value="service">服务维度</Option>
            <Option value="app">应用维度</Option>
          </Select>
          <Search placeholder="please input query keyword" onSearch={value => this.handleSearch(value)} enterButton style={{ width: 500, marginBottom: 20 }} />
          <Table
            dataSource={this.props.list}
            columns={this.state.columns}
            rowKey={record => (record.appName != undefined) ? record.appName : record.serviceId}
          />
        </Card>

        <Drawer
          title="服务详情"
          placement="right"
          closable={false}
          onClose={this.onClose}
          visible={this.state.visible}
          width={640}
        >
          <Card title="服务发布列表">
            <List
              size="small"
              dataSource={this.props.providerListData}
              style={{ minHeight: 30 }}
              renderItem={item => <List.Item>{item}</List.Item>}
            />
          </Card>

          <Card title="服务消费列表" style={{ marginTop: 10 }}>
            <List
              size="small"
              dataSource={this.props.consumerListData}
              style={{ minHeight: 30 }}
              renderItem={item => <List.Item>{item}</List.Item>}
            />
          </Card>
        </Drawer>
      </div >
    );
  }
};

export default Governance;

// 服务治理页，按照应用维度查看，当点击应用名时，右侧抽屉面板展示，问题是，目前从后端获取到的数据没法展示。