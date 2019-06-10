import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Row, Col, Card, Form, Input, Button, Modal, Divider, Table, message, Tag, Popover } from 'antd';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import styles from './Sofaarkmng.less';
import Result from '@/components/Result';

const FormItem = Form.Item;

// 注册弹框组件及事件
const CreateForm = Form.create()(props => {
    const { modalVisible, form, handleAdd, handleModalVisible } = props;
    const okHandle = () => {
        form.validateFields((err, fieldsValue) => {
            if (err) return;
            form.resetFields();
            handleAdd(fieldsValue);
        });
    };
    return (
        <Modal
            destroyOnClose
            title="注册插件"
            visible={modalVisible}
            onOk={okHandle}
            onCancel={() => handleModalVisible(false)}
        >
            <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="名称">
                {form.getFieldDecorator('pluginName', {
                    rules: [{ required: true, message: '请输入插件名称！', min: 1 }],
                })(<Input placeholder="testPluginName" />)}
            </FormItem>
            <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="描述">
                {form.getFieldDecorator('description', {
                    rules: [{ required: true, message: '请输入描述信息！', min: 1 }],
                })(<Input placeholder="this is a ark plugin" />)}
            </FormItem>
        </Modal>
    );
});

/* eslint react/no-multi-comp:0 */
@connect(({ arkmng }) => ({
    list: arkmng.list,
}))
@Form.create()
class Sofaarkmng extends PureComponent {
    state = {
        modalVisible: false,
        visible: false,
        done: false,
        appVisible: false,
        appDone: false,
        pluginName: '',
    };
    componentDidMount() {
        const { dispatch } = this.props;
        dispatch({
            type: 'arkmng/fetchPlugins',
        });
    }

    // 展示新增版本的组件
    showNewVersionModal = item => {
        this.setState({
            visible: true,
            current: item,
        });
    };

    // 展示关联应用的组件
    showAppArkModal = item => {
        this.setState({
            appVisible: true,
            current: item,
        });
    };

    // 展示注册插件的弹框
    handleModalVisible = flag => {
        this.setState({
            modalVisible: !!flag,
        });
    };

    // 弹框确认执行的事件函数
    handleAdd = fields => {
        const { dispatch } = this.props;
        dispatch({
            type: 'arkmng/registerPlugins',
            payload: {
                pluginName: fields.pluginName,
                description: fields.description,
            },
        });
        message.success('添加成功');
        this.handleModalVisible();
    };

    // 搜索事件函数
    handleSearch = () => {
        const { dispatch } = this.props;
        const { pluginName } = this.state;
        dispatch({
            type: 'arkmng/search',
            payload: pluginName,
        });
    };

    // 新版版本提交
    handleSubmit = () => {
        const { dispatch, form } = this.props;
        const { current } = this.state;
        const pluginNames = current ? current.pluginName : '';
        form.validateFields((err, fieldsValue) => {
            if (err) return;
            dispatch({
                type: 'arkmng/submitVersion',
                payload: {
                    pluginName: pluginNames,
                    version: fieldsValue.version,
                    address: fieldsValue.address
                },
            }).then(() => {
                this.setState({
                    done: true,
                });
            });
        });
    };

    // 关联应用版本提交
    handleAppSubmit = () => {
        const { dispatch, form } = this.props;
        const { current } = this.state;
        const pluginNames = current ? current.pluginName : '';
        form.validateFields((err, fieldsValue) => {
            if (err) return;
            dispatch({
                type: 'arkmng/relatedApp',
                payload: {
                    pluginName: pluginNames,
                    appName: fieldsValue.appName,
                },
            }).then(() => {
                this.setState({
                    appDone: true,
                });
            });
        });
    };

    // 删除模块
    handleDelete = record => {
        const { dispatch } = this.props;
        const pluginNames = record.pluginName;
        dispatch({
            type: 'arkmng/deletePlugin',
            payload: {
                pluginName: pluginNames,
            },
        });
    };

    // 新增版本
    handleDone = () => {
        this.setState({
            done: false,
            visible: false,
        });
    };

    handleCancel = () => {
        this.setState({
            visible: false,
            showAppVersionModal: false
        });
    };

    // 关联应用
    handleAppDone = () => {
        this.setState({
            appDone: false,
            appVisible: false,
        });
    };

    handleAppCancel = () => {
        this.setState({
            appVisible: false,
            showAppArkModal: false
        });
    };

    handleDetails = record => {
        const url = '/sofaarkmng/ark-apps?pluginName=';
        window.location.href = url + record.pluginName;
    };

    removeApp = (pluginNames, record) => {
        const { dispatch } = this.props;
        dispatch({
            type: 'arkmng/cancelRelatedApp',
            payload: {
                appName: record.appName,
                pluginName: pluginNames,
            },
        });
    };

    // 查询组件
    renderSimpleForm() {
        return (
            <Form layout="inline">
                <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
                    <Col md={8} sm={24}>
                        <FormItem label="模块名称">
                            <Input
                                placeholder="请输入"
                                onChange={e => this.setState({ pluginName: e.target.value })}
                            />
                        </FormItem>
                    </Col>
                    <Col md={8} sm={24}>
                        <span className={styles.submitButtons}>
                            <Button type="primary" onClick={() => this.handleSearch()}>
                                查询
        </Button>
                            <Button
                                icon="plus"
                                type="primary"
                                style={{ marginLeft: 8 }}
                                onClick={() => this.handleModalVisible(true)}
                            >
                                新建
        </Button>
                        </span>
                    </Col>
                </Row>
            </Form>
        );
    }

    renderForm() {
        return this.renderSimpleForm();
    }

    render() {
        const { list: dataSource } = this.props;
        const parentMethods = {
            handleAdd: this.handleAdd,
            handleModalVisible: this.handleModalVisible,
        };

        const {
            form: { getFieldDecorator },
        } = this.props;

        const { visible, modalVisible, done, appVisible, appDone } = this.state;
        // 表格列定义
        const columns = [
            {
                title: '模块名称',
                dataIndex: 'pluginName',
                key: 'pluginName',
            },
            {
                title: '版本',
                key: 'versions',
                dataIndex: 'versions',
                render: versions => (
                    <span>
                        {versions.map(tag => (
                            <Popover content={tag.sourcePath} title={tag.version} key={tag.version}>
                                <Tag color="blue" key={tag.version}>
                                    {tag.version}
                                </Tag>
                            </Popover>
                        ))}
                    </span>
                ),
            },
            {
                title: '操作',
                key: 'action',
                render: (text, record) => (
                    <span>
                        <a
                            onClick={e => {
                                e.preventDefault();
                                this.showAppArkModal(record);
                            }}
                        >
                            关联应用
            </a>
                        <Divider type="vertical" />
                        <a
                            onClick={e => {
                                e.preventDefault();
                                this.showNewVersionModal(record);
                            }}
                        >
                            添加版本
            </a>
                        <Divider type="vertical" />
                        <a
                            onClick={e => {
                                e.preventDefault();
                                this.handleDetails(record);
                            }}
                        >
                            详情
            </a>
                        <Divider type="vertical" />
                        <a
                            onClick={e => {
                                e.preventDefault();
                                this.handleDelete(record);
                            }}
                        >
                            删除
            </a>
                    </span>
                ),
            },
        ];

        const modalFooter = done
            ? { footer: null, onCancel: this.handleDone }
            : { okText: '保存', onOk: this.handleSubmit, onCancel: this.handleCancel };

        const modalAppFooter = appDone
            ? { footer: null, onCancel: this.handleAppDone }
            : { okText: '保存', onOk: this.handleAppSubmit, onCancel: this.handleAppCancel };

        // 增加关联应用弹框组件
        const getAppArkModalContent = () => {
            if (appDone) {
                return (
                    <Result
                        type="success"
                        title="操作成功"
                        description="添加关联应用成功!"
                        actions={
                            <Button type="primary" onClick={this.handleAppDone}>
                                知道了
                </Button>
                        }
                    />
                );
            }
            return (
                <Form onSubmit={this.handleAppSubmit}>
                    <FormItem label="关联应用">
                        {getFieldDecorator('appName', {
                            rules: [{ required: true, message: '应用名' }],
                        })(<Input placeholder="应用名" />)}
                    </FormItem>
                </Form>
            );
        };

        // 新增版本弹框组件
        const getModalContent = () => {
            if (done) {
                return (
                    <Result
                        type="success"
                        title="操作成功"
                        description="新增插件版本成功!"
                        actions={
                            <Button type="primary" onClick={this.handleDone}>
                                知道了
                </Button>
                        }
                    />
                );
            }
            return (
                <Form onSubmit={this.handleSubmit}>
                    <FormItem label="新增版本" >
                        {getFieldDecorator('version', {
                            rules: [{ required: true, message: '请输入版本' }],
                        })(<Input placeholder="请输入版本" />)}
                    </FormItem>

                    <FormItem label="文件地址" >
                        {getFieldDecorator('address', {
                            rules: [{ required: true, message: '请输入文件地址' }],
                        })(<Input placeholder="请输入文件地址" />)}
                    </FormItem>
                </Form>
            );
        };

        // 关联应用列表
        const expandedRowRender = (pluginName, data) => {
            const expandedColumns = [
                { title: '应用名', dataIndex: 'appName', key: 'appName' },
                { title: '实例个数', dataIndex: 'instanceNum', key: 'instanceNum' },
                {
                    title: '操作',
                    key: 'action',
                    render: (text, record) => (
                        <span>
                            <a
                                onClick={e => {
                                    e.preventDefault();
                                    this.removeApp(pluginName, record);
                                }}
                            >
                                取消关联
            </a>
                        </span>
                    ),
                },
            ];
            return (
                <Table
                    columns={expandedColumns}
                    dataSource={data}
                    pagination={false}
                    size="small"
                    rowKey={r => r.appName}
                />
            );
        };

        return (
            <PageHeaderWrapper>
                <Card bordered={false}>
                    <div className={styles.tableList}>
                        <div className={styles.tableListForm}>{this.renderForm()}</div>
                        <Table
                            dataSource={dataSource}
                            expandedRowRender={record => expandedRowRender(record.pluginName, record.appArkList)}
                            columns={columns}
                            rowKey={r => r.id}
                        />
                    </div>
                </Card>
                <CreateForm {...parentMethods} modalVisible={modalVisible} />
                {/* 添加版本的弹框组件 */}
                {
                    this.state.visible && (
                        <Modal destroyOnClose visible={visible} width={320} {...modalFooter}>
                            {getModalContent()}
                        </Modal>
                    )
                }
                {/* 关联应用的弹框组件 */}
                {
                    this.state.appVisible && (
                        <Modal destroyOnClose visible={appVisible} width={320} {...modalAppFooter}>
                            {getAppArkModalContent()}
                        </Modal>
                    )
                }
            </PageHeaderWrapper>
        );
    }
}

export default Sofaarkmng;
