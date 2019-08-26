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
class Mappings extends React.Component {

  componentDidMount() {
    this.load('fetchMappings');

    const intervalId = setInterval(() => {
      this.load('fetchMappings');

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
        <PropertyDetail
          title="Mappings"
          data={this.props.monitor.mappings === undefined ? [] : this.props.monitor.mappings} />
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


export default Mappings;
