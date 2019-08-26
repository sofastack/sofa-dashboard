import React from 'react';
import { Input, Modal, Form } from 'antd';

class RelatedAppForm extends React.Component {
    render() {
        const { visible, onCancel, onCreate, form, confirmLoading } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                visible={visible}
                title="关联应用"
                okText="确定"
                onCancel={onCancel}
                confirmLoading={confirmLoading}
                onOk={onCreate}>
                <Form layout="vertical">
                    <Form.Item label="Ark Biz Master Name">
                        {getFieldDecorator('appName', {
                            initialValue: '',
                            rules: [{ required: true, message: 'Please input the app name!' }],
                        })(<Input />)}
                    </Form.Item>
                </Form>
            </Modal>
        );
    }
}

export default Form.create({ name: 'form_in_modal_related_app' })(RelatedAppForm);
