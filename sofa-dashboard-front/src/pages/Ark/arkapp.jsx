import React from 'react';
import { Card, Select, Table, Button, Spin, Drawer, Row, Col, List, message } from 'antd';
import { connect } from 'dva';

const { Option } = Select;

const pStyle = {
  fontSize: 18,
  color: 'rgb(126,15,125)',
  lineHeight: '24px',
  display: 'block',
  fontWeight: 500,
  marginBottom: 16,
};

const DescriptionItem = ({ title, content }) => (
  <div
    style={{
      fontSize: 14,
      lineHeight: '22px',
      marginBottom: 7,
      color: 'rgba(0,0,0,0.65)',
    }}
  >
    <p
      style={{
        marginRight: 8,
        display: 'inline-block',
        color: 'rgb(126,15,125)',
        fontWeight: 'bold'
      }}
    >
      {title}:
    </p>
    <span style={{
      color: 'rgb(50,171,57)',
    }}
    >
      {content}
    </span>
  </div>
);

@connect(({ arkapp }) => ({
  ipUnitList: arkapp.ipUnitList,
  defaultVersion: arkapp.defaultVersion,
  versionList: arkapp.versionList,
  bizState: arkapp.bizState
}))
class ArkApp extends React.Component {

  state = {
    version: "",
    visible: false,
    selectedRowKeys: [], // Check here to configure the default column
    loading: false,
  };

  componentDidMount() {
    this.getData("");
  }

  syncBizState = (version) => {
    var intervalID = setInterval(() => this.getData(version), 500);
    return intervalID;
  }

  getData = (version) => {
    const { dispatch, location } = this.props;
    const { pluginName, appName } = location.query;
    dispatch({
      type: 'arkapp/fetchArkApps',
      payload: {
        "version": version,
        "pluginName": pluginName,
        "appName": appName
      }
    });
  };

  showDrawer = (record) => {
    this.setState({
      visible: true,
    });
    const { dispatch, location } = this.props;
    const { appName } = location.query;

    dispatch({
      type: 'arkapp/fetchBizState',
      payload: {
        "ip": record.ip,
        "appName": appName
      }
    });
  };

  onClose = () => {
    this.setState({
      visible: false,
    });
  };

  // 处理全局相关事件
  handlerGlobal = (eventType) => {
    // this.setState({ loading: true });
    // // ajax request after empty completing
    // setTimeout(() => {
    //   this.setState({
    //     selectedRowKeys: [],
    //     loading: false,
    //   });
    // }, 1000);
    message.warn("暂不支持按应用维度推送指令")
  };

  // 处理分组相关事件
  handlerGroup = (eventType) => {
    // this.setState({ loading: true });
    // // ajax request after empty completing
    // setTimeout(() => {
    //   this.setState({
    //     selectedRowKeys: [],
    //     loading: false,
    //   });
    // }, 1000);
    message.warn("暂不支持按分组维度推送指令")
  };

  // 处理单实例相关事件
  handlerSingle = (eventType, record) => {
    this.setState({ loading: true });
    const { dispatch, location, defaultVersion } = this.props;
    const { appName, pluginName } = location.query;

    dispatch({
      type: 'arkapp/command',
      payload: {
        "appName": appName,
        "pluginName": pluginName,
        "pluginVersion": defaultVersion,
        "command": eventType,
        "dimension": 'ip',
        "targetHosts": [record.ip],
      }
    }).then(data => {
      if (data) {
        var intervalID = this.syncBizState(defaultVersion);
        // 延迟两秒
        setTimeout(() => {
          clearInterval(intervalID);
          dispatch({
            type: 'arkapp/getBizState',
            payload: {
              "appName": appName,
              "pluginName": pluginName,
              "version": defaultVersion,
              "ip": record.ip,
            }
          }).then(result => {
            const isOk = (result.data === 'ACTIVATED' && eventType === 'install')
              || (result.data === '' && eventType === 'uninstall')
              || (result.data === 'DEACTIVATED' && eventType === 'switch')
              || (result.data === 'ACTIVATED' && eventType === 'switch')
              || (result.data === 'DEACTIVATED' && eventType === 'install');
            if (result.success && isOk) {
              // 关闭加载动画
              this.setState({
                selectedRowKeys: [],
                loading: false,
              });
              message.info(eventType + '成功');
            } else {
              message.info(eventType + '成功,同步模块状态超时，请尝试手动刷新');
            }
          });
        }, 3000);
      } else {
        message.info(eventType + '失败，请联系管理员');
      }
    });
  };

  onSelectChange = selectedRowKeys => {
    this.setState({ selectedRowKeys });
  };

  handleChange = (value) => {
    this.getData(value);
  };
  render() {
    const { selectedRowKeys } = this.state;
    const { versionList, ipUnitList, location } = this.props;
    const { pluginName, appName } = location.query;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const hasSelected = selectedRowKeys.length > 0;

    const columns = [
      {
        title: 'IP',
        dataIndex: 'ip',
      },
      {
        title: '端口',
        dataIndex: 'port',
      },
      {
        title: '状态',
        dataIndex: 'status',
      },
      {
        title: 'Action',
        dataIndex: 'operation',
        key: 'operation',
        render: (text, record) => (
          <span className="table-operation">
            <Button type="primary"
              onClick={() => this.handlerSingle("install", record)}
              style={{ marginRight: 10 }}
              disabled={record.status != ''}>
              安装
            </Button>

            <Button
              type="primary"
              onClick={() => this.handlerSingle("uninstall", record)}
              style={{ marginRight: 10 }}
              disabled={record.status != 'ACTIVATED'}>
              卸载
            </Button>

            <Button type="primary"
              onClick={() => this.handlerSingle("switch", record)}
              style={{ marginRight: 10 }}
              disabled={record.status != 'DEACTIVATED'}>
              激活
            </Button>

            <Button type="primary"
              onClick={() => this.showDrawer(record)}
              style={{ marginRight: 10 }}
            >
              状态详情
            </Button>
          </span>
        ),
      },
    ];
    const header = () => {
      return (
        <div>
          <Button type="primary" onClick={() => this.handlerGlobal("install")} style={{ marginRight: 10 }} >
            全部安装
            </Button>

          <Button type="primary" onClick={() => this.handlerGlobal("uninstall")} style={{ marginRight: 10 }}>
            全部卸载
            </Button>

          <Button type="primary" onClick={() => this.handlerGlobal("switch")} style={{ marginRight: 10 }}>
            全部激活
            </Button>
        </div>
      );
    }

    const footer = () => {
      return (
        <div>
          <Button type="primary" onClick={() => this.handlerGroup("install")} disabled={!hasSelected} style={{ marginRight: 10 }}>
            分组安装
            </Button>

          <Button type="primary" onClick={() => this.handlerGroup("uninstall")} disabled={!hasSelected} style={{ marginRight: 10 }}>
            分组卸载
            </Button>

          <Button type="primary" onClick={() => this.handlerGroup("switch")} disabled={!hasSelected} style={{ marginRight: 10 }}>
            分组激活
            </Button>

          <span style={{ marginLeft: 8 }}>
            {hasSelected ? `Selected ${selectedRowKeys.length} items` : ''}
          </span>
        </div>
      );
    }

    return (
      <div>
        <Card bordered={false} hoverable={false} style={{ marginTop: -15, marginLeft: -15 }}>
          <span style={{ marginRight: 40 }}>
            插件名：{pluginName}
          </span>

          <span style={{ marginRight: 40 }}>
            应用名：{appName}
          </span>

          <span>
            版本选择：
            {
              this.props.defaultVersion && <Select defaultValue={this.props.defaultVersion} style={{ minWidth: 200 }} onChange={this.handleChange}>
                {versionList.length &&
                  versionList.map(item => (
                    <Select.Option key={item} value={item}>
                      {item}
                    </Select.Option>
                  ))}
              </Select>
            }
          </span>
        </Card>

        <Card bordered={false} hoverable={false} style={{ marginTop: 5, marginLeft: -15 }}>
          <Spin spinning={this.state.loading}>
            <Table
              rowSelection={rowSelection}
              columns={columns}
              dataSource={ipUnitList}
              bordered
              title={header}
              footer={footer}
              rowKey={record => record.ip}
            />,
          </Spin>
        </Card>

        <Drawer
          title="BizState 详情"
          placement="right"
          closable={false}
          width={700}
          onClose={this.onClose}
          visible={this.state.visible}
        >
          {this.props.bizState && this.props.bizState.message && this.props.bizState.code && (
            <div>
              <Row>
                <Col>
                  <DescriptionItem title="message" content={this.props.bizState.message} />
                </Col>
              </Row>
              <Row style={{ borderBottom: "1px solid #ccc", marginBottom: 15 }}>
                <Col>
                  <DescriptionItem title="code" content={this.props.bizState.code} />
                </Col>
              </Row>
            </div>
          )}
          <p style={pStyle}> bizInfos </p>
          {this.props.bizState && this.props.bizState['bizInfos'] && this.props.bizState["bizInfos"].map((item, index) => {
            return (
              <div key={index}>
                <Row>
                  <Col span={12}>
                    <DescriptionItem title="bizName" content={item.bizName} />
                  </Col>

                  <Col span={12}>
                    <DescriptionItem title="bizVersion" content={item.bizVersion} />
                  </Col>
                </Row>
                <Row>
                  <Col>
                    <DescriptionItem title="mainClass" content={item.mainClass} />
                  </Col>
                </Row>
                <Row>
                  <Col span={12}>
                    <DescriptionItem title="webContextPath" content={item.webContextPath} />
                  </Col>
                  <Col span={12}>
                    <DescriptionItem title="priority" content={item.priority} />
                  </Col>
                </Row>
                <Row>
                  <Col span={12}>
                    <DescriptionItem title="identity" content={item.identity} />
                  </Col>
                  <Col span={12}>
                    <DescriptionItem title="bizState" content={item.bizState} />
                  </Col>
                </Row>
                {/* 有数据时才展示 */}
                {item.denyImportPackages.length == 0 ? (
                  <p style={{ fontSize: 16, color: 'rgb(126,15,125)' }}> denyImportPackages : []</p>
                ) : (
                    <div>
                      <List
                        header={<div style={{ fontSize: 16, fontWeight: "bold", color: 'rgb(126,15,125)' }}>denyImportPackages</div>}
                        dataSource={item.denyImportPackages}
                        renderItem={item => <List.Item key={item}>{item}</List.Item>}
                      />
                    </div>
                  )}

                {item.denyImportPackageNodes.length == 0 ? (
                  <p style={{ fontSize: 16, color: 'rgb(126,15,125)' }}> denyImportPackageNodes : []</p>
                ) : (
                    <div>
                      <List
                        header={<div style={{ fontSize: 16, fontWeight: "bold", color: 'rgb(126,15,125)' }}>denyImportPackageNodes</div>}
                        dataSource={item.denyImportPackageNodes}
                        renderItem={item => <List.Item key={item}>{item}</List.Item>}
                      />
                    </div>
                  )}

                {item.denyImportPackageStems.length == 0 ? (
                  <p style={{ fontSize: 16, color: 'rgb(126,15,125)' }}> denyImportPackageStems : []</p>
                ) : (
                    <div>
                      <List
                        header={<div style={{ fontSize: 16, fontWeight: "bold", color: 'rgb(126,15,125)' }}>denyImportPackageStems</div>}
                        dataSource={item.denyImportPackageStems}
                        renderItem={item => <List.Item key={item}>{item}</List.Item>}
                      />
                    </div>
                  )}


                {item.denyImportClasses.length == 0 ? (
                  <p style={{ fontSize: 16, color: 'rgb(126,15,125)' }}> denyImportClasses : []</p>
                ) : (
                    <div>
                      <List
                        header={<div style={{ fontSize: 16, fontWeight: "bold", color: 'rgb(126,15,125)' }}>denyImportClasses</div>}
                        dataSource={item.denyImportClasses}
                        renderItem={item => <List.Item key={item}>{item}</List.Item>}
                      />
                    </div>
                  )}

                {item.denyImportResources.length == 0 ? (
                  <p style={{ fontSize: 16, color: 'rgb(126,15,125)' }}> denyImportResources : []</p>
                ) : (
                    <div>
                      <List
                        header={<div style={{ fontSize: 16, fontWeight: "bold", color: 'rgb(126,15,125)' }}>denyImportResources</div>}
                        dataSource={item.denyImportResources}
                        renderItem={item => <List.Item key={item}>{item}</List.Item>}
                      />
                    </div>
                  )}

                {item.classPath && item.classPath.length && (
                  <List
                    header={<div style={{ fontSize: 16, fontWeight: "bold", color: 'rgb(126,15,125)' }}>classPath</div>}
                    dataSource={item.classPath}
                    renderItem={item => <List.Item key={item} style={{ color: 'rgb(50,171,57)' }}>{item}</List.Item>}
                  />
                )}

                {/* 如果 item["bizClassLoader"] 存在则渲染组件，否则不展示 */}
                {item["bizClassLoader"] && item["bizClassLoader"].urls.length && (
                  <div>
                    <p style={pStyle}> bizClassLoader </p>
                    <Row>
                      <Col span={12}>
                        <DescriptionItem title="parent" content={item["bizClassLoader"].parent} />
                      </Col>
                      <Col span={12}>
                        <DescriptionItem title="bizIdentity" content={item["bizClassLoader"].bizIdentity} />
                      </Col>
                    </Row>
                    <List
                      header={<div style={{ fontSize: 16, fontWeight: "bold", color: 'rgb(126,15,125)' }}>urls</div>}
                      dataSource={item["bizClassLoader"].urls}
                      renderItem={item => <List.Item key={item}>{item}</List.Item>}
                    />
                  </div>
                )}
              </div>
            )
          })}
        </Drawer>
      </div>
    );
  }
};
export default ArkApp;