import React from 'react';
import { Card, Table, Divider, Input, Tag, Modal, Button, Select, Form, Radio, Tooltip } from 'antd';
// eslint-disable-next-line
class RegisterPluginFrom extends React.Component {
    render() {
        const { visible, onCancel, onCreate, form, confirmLoading, arkModalData } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                visible={visible}
                title="添加插件"
                okText={arkModalData.pluginName ? '更新' : '创建'}
                onCancel={onCancel}
                confirmLoading={confirmLoading}
                onOk={onCreate}
            >
                <Form layout="vertical">
                    {/* 隐藏域，用于更新时导出插件id */}
                    <Form.Item style={{ height: 0, margin: 0, padding: 0 }}>
                        {getFieldDecorator('id', {
                            initialValue: arkModalData.id || '',
                        })(<Input type="hidden" />)}
                    </Form.Item>
                    <Form.Item label="Plugin Name">
                        {getFieldDecorator('pluginName', {
                            initialValue: arkModalData.pluginName || '',
                            rules: [{ required: true, message: 'Please input the plugin name!' }],
                        })(<Input />)}
                    </Form.Item>
                    <Form.Item label="Plugin Description">
                        {getFieldDecorator('description', {
                            initialValue: arkModalData.description || '',
                            rules: [{ required: false, message: 'Please input the plugin name!' }],
                        })(<Input type="textarea" />)}
                    </Form.Item>
                </Form>
            </Modal>
        );
    }
}
export default Form.create({ name: 'form_in_modal_related_app' })(RegisterPluginFrom);