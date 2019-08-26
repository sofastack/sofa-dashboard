import React from 'react';
import Redirect from 'umi/redirect';
import { Form, Icon, Input, Button } from 'antd';
import { connect } from 'dva';
import Styles from './index.less';

class NormalLoginForm extends React.Component {
  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        // 发送用户信息，这里的 values 现在并没有使用
        this.props.dispatch({
          type: 'user/fetchUser',
          payload: values,
        });
      }
    });
  };

  render() {
    const { form, user } = this.props;
    const { getFieldDecorator } = form;

    if (user.userid) {
      return <Redirect to="/" />;
    }

    return (
      <div className={Styles.login}>
        <Form onSubmit={this.handleSubmit} className="login-form">
          <Form.Item>
            {getFieldDecorator('username', {
              rules: [{ required: true, message: 'Please input your username!' }],
            })(
              <Input
                prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />}
                placeholder="Username"
              />,
            )}
          </Form.Item>
          <Form.Item>
            {getFieldDecorator('password', {
              rules: [{ required: true, message: 'Please input your Password!' }],
            })(
              <Input
                prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />}
                type="password"
                placeholder="Password"
              />,
            )}
          </Form.Item>
          <Button type="primary" htmlType="submit" className="login-form-button">
            Log in
          </Button>
        </Form>
      </div>
    );
  }
}

const WrappedNormalLoginForm = Form.create({ name: 'normal_login' })(NormalLoginForm);

export default connect(({ user }) => ({
  user,
}))(WrappedNormalLoginForm);
