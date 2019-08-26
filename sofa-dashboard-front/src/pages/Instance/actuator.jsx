import React from 'react';
import {Card, Tabs} from 'antd';
import {connect} from 'dva';
import OverView from './tab/overview'
import Environment from "./tab/environment";
import Loggers from "./tab/loggers";
import Mappings from './tab/mappings';

const {TabPane} = Tabs;

@connect(({instance}) => ({
  list: instance.list,
}))
class Actuator extends React.Component {
  state = {
    activeKey: '1',
  };

  componentDidMount() {
  }

  changeTab = (key) => {
    this.setState({activeKey: key});
  };

  render() {
    const {activeKey} = this.state;

    return (
      <Card bordered={false} hoverable={false} style={{marginTop: -15, marginLeft: -15}}>
        <Tabs activeKey={this.state.activeKey} onChange={this.changeTab}>
          <TabPane tab="基础信息" key="1">
            {
              activeKey === '1'
              &&
              <OverView dispatch={this.props.dispatch} id={this.props.match.params[0]}/>
            }
          </TabPane>
          <TabPane tab="环境变量" key="2">
            {
              activeKey === '2'
              &&
              <Environment dispatch={this.props.dispatch} id={this.props.match.params[0]}/>
            }
          </TabPane>
          <TabPane tab="日志信息" key="3">
            {
              activeKey === '3'
              &&
              <Loggers dispatch={this.props.dispatch} id={this.props.match.params[0]}/>
            }
          </TabPane>
          <TabPane tab="路径映射" key="4">
            {
              activeKey === '4'
              &&
              <Mappings dispatch={this.props.dispatch} id={this.props.match.params[0]}/>
            }
          </TabPane>
        </Tabs>
      </Card>
    );
  }
};
export default Actuator;
