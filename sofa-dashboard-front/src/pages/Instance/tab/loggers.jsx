import React from "react";
import {connect} from "dva";
import PropsMonitor from '../component/PropsMonitor';
import PropertyDetail from "../component/PropertyDetail";

/**
 * 应用信息展示控制台
 */
@connect(({monitor}) => ({
  monitor: monitor,
}))
class Loggers extends React.Component {

  componentDidMount() {
    this.load('fetchLoggers');

    const intervalId = setInterval(() => {
      this.load('fetchLoggers');

    }, 5000);
    this.setState({intervalId: intervalId})
  }

  componentWillUnmount() {
    // use intervalId from the state to clear the interval
    clearInterval(this.state.intervalId);
  }

  render() {
    return (
      <div>
        <PropertyDetail
          title="Loggers"
          data={this.props.monitor.loggers.detail === undefined ? [] : this.props.monitor.loggers.detail}/>
      </div>
    )
  };

  load = (type) => {
    const {dispatch} = this.props;
    dispatch({
      type: 'monitor/' + type,
      payload: {
        instanceId: this.props.id
      }
    })
  };
}


export default Loggers;
