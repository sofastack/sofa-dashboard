import React from "react";
import { connect } from "dva";
import PropsMonitor from '../component/PropsMonitor';
import MetricsGraph from '../component/MetricsGraph';
import PropertyDetail from "../component/PropertyDetail";

/**
 * 应用信息展示控制台
 */
@connect(({ monitor }) => ({
  monitor: monitor,
}))
class OverView extends React.Component {

  componentDidMount() {
    this.load('fetchHealth');
    this.load('fetchMemory');
    this.load('fetchThread');

    const intervalId = setInterval(() => {
      this.load('fetchMemory');
      this.load('fetchThread');

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
        <div style={{ display: 'flex' }}>
          <PropsMonitor
            title="Health"
            data={this.props.monitor.health} />
          <MetricsGraph
            title="Thread"
            data={this.getThreadSummary()} />
        </div>
        <div style={{ display: 'flex' }}>
          <MetricsGraph
            title="Memory:Heap"
            data={this.getMemoryHeapData()} />
          <MetricsGraph
            title="Memory:NonHeap"
            data={this.getMemoryNonHeapData()} />
        </div>
        <PropertyDetail
          title="Detail"
          data={this.props.monitor.health.detail === undefined ? [] : this.props.monitor.health.detail} />
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

  getThreadSummary() {
    if (!this.props.monitor.thread) {
      return []
    }

    const result = this.props.monitor.thread.map(item => {
      return [{
        name: "peak",
        timestamp: item.timestamp.substr(11),
        value: item.value.peak
      }, {
        name: "daemon",
        timestamp: item.timestamp.substr(11),
        value: item.value.daemon
      }, {
        name: "live",
        timestamp: item.timestamp.substr(11),
        value: item.value.live
      },]
    }).reduce((a, b) => a.concat(b), []);

    return result;
  }

  getMemoryHeapData() {
    if (!this.props.monitor.memory) {
      return []
    }

    const result = this.props.monitor.memory.map(item => {
      return [{
        name: "total",
        timestamp: item.timestamp.substr(11),
        value: item.value.heap.size
      }, {
        name: "used",
        timestamp: item.timestamp.substr(11),
        value: item.value.heap.used
      }]
    }).reduce((a, b) => a.concat(b), []);

    return result
  }

  getMemoryNonHeapData() {
    if (!this.props.monitor.memory) {
      return []
    }

    const result = this.props.monitor.memory.map(item => {
      const nonheap = item.value.nonHeap;
      return [{
        name: "total",
        timestamp: item.timestamp.substr(11),
        value: nonheap.size
      }, {
        name: "used",
        timestamp: item.timestamp.substr(11),
        value: nonheap.used
      }, {
        name: "metaspace",
        timestamp: item.timestamp.substr(11),
        value: nonheap.metaspace
      },]
    }).reduce((a, b) => a.concat(b), []);

    return result
  }
}


export default OverView;
