import React from "react";
import { connect } from "dva";
import PropsMonitor from '../component/PropsMonitor';
import PropertyDetail from "../component/PropertyDetail";

/**
 * 应用信息展示控制台
 */
@connect(({ monitor }) => ({
  monitor: monitor,
}))
class Environment extends React.Component {

  componentDidMount() {
    this.load('fetchEnv');

    const intervalId = setInterval(() => {
      this.load('fetchEnv');

    }, 5000);
    this.setState({ intervalId: intervalId })
  }

  componentWillUnmount() {
    // use intervalId from the state to clear the interval
    clearInterval(this.state.intervalId);
  }

  render() {
    return (
      <div>
        {/* <PropsMonitor
          width="88%"
          title="Environment"
          data={this.props.monitor.env}
          graphAnchor="#env/graph"
          detailAnchor="#env/detail" /> */}
        <PropertyDetail
          title="Environment"
          data={this.props.monitor.env.detail === undefined ? [] : this.props.monitor.env.detail} />
      </div>
    )
  };

  load = (type) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'monitor/' + type,
      payload: {
        instanceId: this.props.id
      }
    })
  };
}


export default Environment;
