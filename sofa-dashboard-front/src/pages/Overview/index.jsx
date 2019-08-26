import React from 'react';
import { Card, Table, Divider, Input } from 'antd';
import { connect } from 'dva';
const { Search } = Input;
@connect(({ application }) => ({
  list: application.list || [],
}))
class Overview extends React.Component {

  getData = (keyWord) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'application/fetch',
      payload: {
        "applicationName": keyWord
      }
    });
  }

  componentDidMount() {
    this.getData("");
  }

  handleSearch = (value) => {
    this.getData(value);
  }
  render() {
    const columns = [
      {
        title: '应用名',
        dataIndex: 'applicationName',
        key: 'applicationName',
      },
      {
        title: '应用实例数',
        dataIndex: 'applicationCount',
        key: 'applicationCount',
      },
      {
        title: '操作',
        key: 'action',
        render: (text, record) => (
          <span>
            <a href={`/dashboard/instance?applicationName=${record.applicationName}`}>实例列表</a>
            <Divider type="vertical" />
            <a href="javascript:;">配置</a>
          </span>
        ),
      },
    ];

    return (
      <Card bordered={false} hoverable={false} style={{ marginTop: -15, marginLeft: -15 }}>
        <Search placeholder="input application name" onSearch={value => this.handleSearch(value)} enterButton style={{ width: 500, marginBottom: 20 }} />

        {this.props.list && (
          <Table
            dataSource={this.props.list}
            columns={columns}
            rowKey={record => record.applicationName}
          />
        )}
      </Card>
    );
  }
};

export default Overview;