import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Table, Card, Form, Divider, Row, Col, Select, message } from 'antd';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import styles from './Apppluginmng.less';

@connect(({ arkapp }) => ({
    list: arkapp.list,
    defaultVersion: arkapp.defaultVersion,
    versionList: arkapp.versionList,
}))
@Form.create()
class Apppluginmng extends PureComponent {
    state = {
        ipList: [],
        pluginVersion: '',
    };

    componentDidMount() {
        this.getData();
    }

    getData = () => {
        const { dispatch, location } = this.props;
        const { pluginName: pluginNames } = location.query; // 从url中获取参数
        const { pluginVersion } = this.state;
        dispatch({
            type: 'arkapp/fetchArkApps',
            payload: {
                pluginName: pluginNames,
                version: pluginVersion,
            },
        });
    };

    expandedRowRender = record => {
        const columns = [
            { title: 'IP', dataIndex: 'ip', key: 'ip' },
            { title: '状态', dataIndex: 'statue', key: 'statue' },
            {
                title: 'Action',
                // dataIndex: 'operation',
                key: 'operation',
                render: item => (
                    <span className="table-operation">
                        {item.statue === '' ? (
                            <div>
                                <a
                                    onClick={e => {
                                        e.preventDefault();
                                        this.handleSingle(item, record.appName, 'install');
                                    }}
                                >
                                    安装
        </a>
                                <Divider type="vertical" />
                                <a style={{ "opacity": 0.2, "pointerEvents": 'none' }} > 激活 </a>
                                <Divider type="vertical" />
                                <a style={{ "opacity": 0.2, "pointerEvents": 'none' }} > 卸载 </a>
                            </div>
                        ) : (
                                ''
                            )}
                        {item.statue === 'DEACTIVATED' ? (
                            <div>
                                <a style={{ "opacity": 0.2, "pointerEvents": 'none' }} > 安装 </a>
                                <Divider type="vertical" />
                                <a
                                    onClick={e => {
                                        e.preventDefault();
                                        this.handleSingle(item, record.appName, 'switch');
                                    }}
                                >
                                    激活
            </a>
                                <Divider type="vertical" />
                                <a style={{ "opacity": 0.2, "pointerEvents": 'none' }} > 卸载 </a>
                            </div>
                        ) : (
                                ''
                            )}
                        {item.statue === 'ACTIVATED' ? (
                            <div>
                                <a style={{ "opacity": 0.2, "pointerEvents": 'none' }} > 安装 </a>
                                <Divider type="vertical" />
                                <a style={{ "opacity": 0.2, "pointerEvents": 'none' }} > 激活 </a>
                                <Divider type="vertical" />
                                <a
                                    onClick={e => {
                                        e.preventDefault();
                                        this.handleSingle(item, record.appName, 'unstall');
                                    }
                                    }
                                >
                                    卸载
            </a>
                            </div>
                        ) : (
                                ''
                            )}
                        {item.statue === 'RESOLVED' ? (
                            <div>
                                解析中...
        </div>
                        ) : (
                                ''
                            )}
                    </span>
                ),
            },
        ];

        const rowSelection = {
            onChange: selectedRowKeys => {
                this.state.ipList = selectedRowKeys;
            },
        };

        // 分组推送命令
        const footer = () => (
            <div>
                <span className="table-operation">
                    <a
                        onClick={e => {
                            e.preventDefault();
                            this.handleBatchGroup(record.appName, 'install');
                        }}
                    >
                        安装
        </a>
                    <Divider type="vertical" />
                    <a
                        onClick={e => {
                            e.preventDefault();
                            this.handleBatchGroup(record.appName, 'switch');
                        }}
                    >
                        激活
        </a>
                    <Divider type="vertical" />
                    <a
                        onClick={e => {
                            e.preventDefault();
                            this.handleBatchGroup(record.appName, 'unstall');
                        }}
                    >
                        卸载
        </a>
                    <Divider type="vertical" />
                </span>
            </div>
        );

        return (
            <Table
                size="middle"
                // rowSelection={rowSelection} 关闭分组推送
                // bordered
                //// footer={footer}
                columns={columns}
                dataSource={record.ipUnitList}
                rowKey={item => item.ip}
                pagination={false}
            />
        );
    };

    // select 框选项变更触发事件回调函数
    handleSecChange = item => {
        const { dispatch, location } = this.props;
        const queryParams = location.query;
        const { pluginName: pluginNames } = queryParams;
        console.log("pluginName", pluginNames)
        console.log("version", item)
        dispatch({
            type: 'arkapp/fetchArkApps',
            payload: {
                pluginName: pluginNames,
                version: item,
            },
        });
    };

    // 基于应用维度全量推送处理
    handleGlobal = (record, commandType) => {
        const { dispatch } = this.props;
        dispatch({
            type: 'arkapp/command',
            payload: {
                appName: record.appName,
                pluginName: record.pluginName,
                pluginVersion: record.pluginVersion,
                command: commandType,
                dimension: 'app',
            },
        });

        // 1s 刷新一次状态
        window.setInte = setInterval(() => {
            this.getData();
        }, 1000);
    };

    // 处理分组推送的方法
    handleBatchGroup = (appNames, commandType) => {
        const { dispatch, location } = this.props;
        const { defaultVersion: defaultVersions } = this.props;
        const { pluginName: pluginNames } = location.query.pluginName;
        const { ipList: ipLists } = this.state;
        if (ipLists.length === 0) {
            message.info('please select instance item');
        } else {
            // todo 如果ipList为空的哈，弹框提醒一下
            dispatch({
                type: 'arkapp/command',
                payload: {
                    appName: appNames,
                    pluginName: pluginNames,
                    pluginVersion: defaultVersions,
                    command: commandType,
                    dimension: 'ip',
                    targetHosts: ipLists,
                },
            });

            // 1s 刷新一次状态
            window.setInte = setInterval(() => {
                this.getData();
            }, 1000);
        }
    };

    // 基于单ip维度推送处理
    handleSingle = (item, appNames, commandType) => {
        const { dispatch, location } = this.props;
        const { pluginName: pluginNames } = location.query;
        const { defaultVersion: defaultVersions } = this.props;
        dispatch({
            type: 'arkapp/command',
            payload: {
                appName: appNames,
                pluginName: pluginNames,
                pluginVersion: defaultVersions,
                command: commandType,
                dimension: 'ip',
                targetHosts: [item.ip],
            },
        });
        // 1s 刷新一次状态
        window.setInte = setInterval(() => {
            this.getData();
        }, 1000);
    };

    // 搜索框组件
    renderSearchSelectOptionComponent = () => {
        const { defaultVersion, location } = this.props;
        const { pluginName } = location.query;
        if (!defaultVersion) {
            return null;
        }
        const { versionList } = this.props;
        return (
            <Form layout="inline">
                <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
                    <Col md={8} sm={24}>
                        <Form.Item label="模块名称">
                            <h4> {pluginName} </h4>
                        </Form.Item>
                    </Col>
                    <Col md={8} sm={24}>
                        <Form.Item label="版本选择">
                            <Select
                                style={{ width: 200 }}
                                onChange={this.handleSecChange}
                                placeholder="请选择版本"
                                defaultValue={defaultVersion}
                            >
                                {versionList.length &&
                                    versionList.map(item => (
                                        <Select.Option key={item} value={item}>
                                            {item}
                                        </Select.Option>
                                    ))}
                            </Select>
                        </Form.Item>
                    </Col>
                </Row>
            </Form>
        );
    };

    render() {
        // 表格列定义
        const appListCols = [
            // { title: '应用名ID', dataIndex: 'appId', key: 'appId' },
            { title: '应用名', dataIndex: 'appName', key: 'appName' },
            { title: '插件名', dataIndex: 'pluginName', key: 'pluginName' },
            {
                title: 'Action',
                key: 'operation',
                render: (text, record) => (
                    <span className="table-operation">
                        <a
                            onClick={e => {
                                e.preventDefault();
                                this.handleGlobal(record, 'install');
                            }}
                        >
                            安装
        </a>
                        <Divider type="vertical" />
                        <a
                            onClick={e => {
                                e.preventDefault();
                                this.handleGlobal(record, 'switch');
                            }}
                        >
                            激活
        </a>
                        <Divider type="vertical" />
                        <a
                            onClick={e => {
                                e.preventDefault();
                                this.handleGlobal(record, 'unstall');
                            }}
                        >
                            卸载
        </a>
                        <Divider type="vertical" />
                    </span>
                ),
            },
        ];
        const { total, loading = false } = this.state;
        const { list } = this.props;
        return (
            <PageHeaderWrapper>
                <Card bordered={false}>
                    <div className={styles.tableList}>
                        <div className={styles.tableListForm}>{this.renderSearchSelectOptionComponent()}</div>
                        <Table
                            columns={appListCols}
                            dataSource={list}
                            expandedRowRender={record => this.expandedRowRender(record)}
                            rowKey={record => record.appName}
                            loading={loading}
                            expandable={false}
                            defaultExpandAllRows={false}
                            pagination={{
                                defaultCurrent: 1,
                                total,
                                onChange: this.pageChange,
                            }}
                        />
                    </div>
                </Card>
            </PageHeaderWrapper>
        );
    }
}
export default Apppluginmng;
