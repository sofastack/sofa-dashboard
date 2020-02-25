import React from 'react';
import { Card, Input, Table, Tag } from 'antd';
import { connect } from 'dva';

const { Search } = Input;

@connect(({ instance }) => ({
  list: instance.list,
}))
class Instance extends React.Component {

  componentDidMount () {
    const { location } = this.props;
    const queryParams = location.query;
    this.getData(queryParams.applicationName);
  }

  getData = (keyWord) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'instance/fetch',
      payload: {
        "applicationName": keyWord || ''
      }
    });
  }

  handleSearch = (value) => {
    this.getData(value);
  }

  parseDate = (str) => {
    function addZero (num) {
      if (parseInt(num) < 10) {
        num = '0' + num;
      }
      return num;
    }

    const oDate = new Date(str),
      oYear = oDate.getFullYear(),
      oMonth = oDate.getMonth() + 1,
      oDay = oDate.getDate(),
      oHour = oDate.getHours(),
      oMin = oDate.getMinutes(),
      oSen = oDate.getSeconds(),
      oTime = oYear + '-' + addZero(oMonth) + '-' + addZero(oDay) + ' ' + addZero(oHour) + ':' +
        addZero(oMin) + ':' + addZero(oSen);
    return oTime;
  }

  render () {
    const columns = [
      {
        title: '应用名',
        dataIndex: 'appName',
        key: 'appName',
      },
      {
        title: 'IP',
        dataIndex: 'hostName',
        key: 'hostName',
      },
      {
        title: '内部IP',
        dataIndex: 'internalHost',
        key: 'internalHost',
      },
      {
        title: '端口',
        dataIndex: 'port',
        key: 'port',
      },
      {
        title: '状态',
        dataIndex: 'appState',
        key: 'appState',
        render: appState => (
          <span>
            {
              <Tag color={ appState === 'UP' ? 'green' : 'red' } key={ appState }>
                { appState }
              </Tag>
            }
          </span>
        ),
      },
      {
        title: '启动时间',
        key: 'startTime',
        render: (text, record) => (
          <span>
            { this.parseDate(record.startTime) }
          </span>
        )
      },
      {
        title: '上次恢复',
        key: 'lastRecover',
        render: (text, record) => (
          <span>
            { this.parseDate(record.lastRecover) }
          </span>
        )
      },
      {
        title: '操作',
        key: 'action',
        render: (text, record) => (
          <span>
            <a href={ `/dashboard/instance/${ record.id }` }>状态查看</a>
          </span>
        ),
      },
    ];
    return (
      <Card bordered={ false } hoverable={ false } style={ { marginTop: -15, marginLeft: -15 } }>
        <Search placeholder="input application name" onSearch={ value => this.handleSearch(value) }
                enterButton style={ { width: 500, marginBottom: 20 } }/>
        <Table
          dataSource={ this.props.list }
          columns={ columns }
          rowKey={ record => record.hostName }
        />
      </Card>
    );
  }
};

export default Instance;
