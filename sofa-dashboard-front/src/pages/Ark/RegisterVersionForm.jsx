import React from 'react';
import { Input, Modal, Form } from 'antd';

class RegisterVersionForm extends React.Component {
    render() {
        const { visible, onCancel, onCreate, form, confirmLoading } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                visible={visible}
                title="添加版本"
                okText="确定"
                onCancel={onCancel}
                confirmLoading={confirmLoading}
                onOk={onCreate}>
                <Form layout="vertical">
                    <Form.Item label="Module Version">
                        {getFieldDecorator('version', {
                            initialValue: '',
                            rules: [{ required: true, message: 'Please input the module version!' }],
                        })(<Input />)}
                    </Form.Item>
                    <Form.Item label="Module Address">
                        {getFieldDecorator('address', {
                            initialValue: '',
                            rules: [{ required: true, message: 'Please input the module file address!' }],
                        })(<Input />)}
                    </Form.Item>
                </Form>
            </Modal>
        );
    }
}

export default Form.create({ name: 'form_in_modal_register_version' })(RegisterVersionForm);
