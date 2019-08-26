import React from "react";
import { connect } from "dva";
import { Card, Modal } from 'antd';
import styles from './monitor.less';
import DataSet from "@antv/data-set";
import { Axis, Chart, Coord, Geom, Label, Legend, Tooltip, View, } from "bizcharts";

/**
 * 应用信息展示控制台
 */
@connect(({ monitor }) => ({
  monitor: monitor,
}))
class InstanceMonitor extends React.Component {

  componentDidMount () {
    this.load('fetchEnv');
    this.load('fetchMappings');
    this.load('fetchLoggers');
    this.load('fetchInfo');
    this.load('fetchHealth');
    this.load('fetchMemory');
    this.load('fetchThread');

    const intervalId = setInterval(() => {
      this.load('fetchHealth');
      this.load('fetchMemory');
      this.load('fetchThread');

    }, 5000);
    this.setState({ intervalId: intervalId })
  }

  componentWillUnmount () {
    // use intervalId from the state to clear the interval
    clearInterval(this.state.intervalId);
  }

  render () {
    return (
      <div>
        <div style={ { display: 'flex' } }>
          <PropsMonitor
            title="Environment"
            data={ this.props.monitor.env }
            graphAnchor="#env/graph"
            detailAnchor="#env/detail"/>
          <MetricsGraph
            title="Thread"
            data={ this.getThreadSummary() }/>
        </div>
        <div style={ { display: 'flex' } }>
          <MetricsGraph
            title="Memory:Heap"
            data={ this.getMemoryHeapData() }/>
          <MetricsGraph
            title="Memory:NonHeap"
            data={ this.getMemoryNonHeapData() }/>
        </div>
        <div style={ { display: 'flex' } }>
          <PropsMonitor
            title="Health"
            data={ this.props.monitor.health }
            graphAnchor="#env/health"
            detailAnchor="#env/health"/>

          <PropsMonitor
            title="Loggers"
            data={ this.props.monitor.loggers }
            graphAnchor="#loggers/graph"
            detailAnchor="#loggers/detail"/>
        </div>
        <div style={ { display: 'flex' } }>
          <PropsMonitor
            title="Mappings"
            data={ this.props.monitor.mappings }
            graphAnchor="#mappings/graph"
            detailAnchor="#mappings/detail"/>
          <PropsMonitor
            title="Information"
            data={ this.props.monitor.info }
            graphAnchor="#info/graph"
            detailAnchor="#info/detail"/>
        </div>
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

  getThreadSummary () {
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
    console.log(result);
    return result;
  }

  getMemoryHeapData () {
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
    console.log(result);
    return result
  }

  getMemoryNonHeapData () {
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
    console.log(result);
    return result
  }
}

class MetricsGraph extends React.Component {

  render () {
    const { DataView } = DataSet;
    const dv = new DataView().source(this.props.data);
    dv.transform({
      type: "percent",
      field: "value",
      dimension: "timestamp",
      groupBy: ["name"],
      as: "percent"
    });
    return (
      <Card title={ this.props.title } bordered={ false }
            style={ { width: "44%", paddingLeft: "2%", paddingRight: "2%" } }>
        <Chart data={ this.props.data } height="300" scale={ {
          value: {
            min: 0
          },
          timestamp: {
            range: [0, 1]
          }
        } }>
          <Axis name="date"/>
          <Axis name="value"/>
          <Legend/>
          <Tooltip crosshairs={ { type: "y" } }/>
          <Geom type="line" position="timestamp*value" color="name"/>
          <Geom type="line" position="timestamp*value" size={ 2 } color="name"/>
        </Chart>
      </Card>
    );
  }
}

/**
 * 应用属性变量相关展示组件
 */
class PropsMonitor extends React.Component {

  constructor (props) {
    super(props);
    this.state = {
      graph: window.location.hash === props.graphAnchor,
      detail: window.location.hash === props.detailAnchor,
    }; // 使用锚点控制详情页面展示
  }

  switchGraph (visible) {
    this.setState({ graph: visible });
    window.location.hash = visible ? this.props.graphAnchor : "#";
  }

  switchDetail (visible) {
    this.setState({ detail: visible });
    window.location.hash = visible ? this.props.detailAnchor : "#";
  }

  render () {
    const hasData = this.props.data['overview'] !== undefined
      && this.props.data['overview'].length > 0;
    return (
      <Card title={ this.props.title } bordered={ false }
            extra={ <a href="javascript:;" onClick={ () => this.switchGraph(true) }>Graph</a> }
            style={ { width: "44%", paddingLeft: "2%", paddingRight: "2%" } }>
        <div>
          <div>
            {
              //展示由接口生成的 overview 信息
              hasData ? this.props.data['overview'].map(item => {
                return (
                  <div key={ item.key } className={ styles["item-info"] }>
                    <span className={ styles["item-info-span-title"] }>{ item.key }</span>
                    <span
                      className={ [
                        styles["item-info-span-content"],
                        styles["text-right"]
                      ].join(' ') }>{ item.value }</span>
                  </div>
                )
              }) : (<p>No data</p>)
            }
            {
              hasData ? (
                <div key={ '...' } className={ styles["item-info"] }>
                  <span className={ styles["item-info-span-title"] }>
                    <a href="javascript:;"
                       className={ styles["item-info-more"] }
                       onClick={ () => this.switchDetail(true) }>
                       more ...
                    </a>
                  </span>
                </div>
              ) : []
            }
          </div>
          <div>
            {
              //Graph 模态框
              this.props.data.detail === undefined ? (<div></div>) : (
                <PropertyTreeGraph
                  title={ this.props.title || "Properties" }
                  visible={ this.state.graph }
                  dismiss={ () => this.switchGraph(false) }
                  data={ this.props.data.detail }/>)
            }
          </div>
          <div>
            {
              //Graph 模态框
              this.props.data.detail === undefined ? (<div></div>) : (
                <PropertyDetail
                  title={ this.props.title || "Properties" }
                  visible={ this.state.detail }
                  dismiss={ () => this.switchDetail(false) }
                  data={ this.props.data.detail }/>)
            }
          </div>
        </div>
      </Card>
    )
  }
}

/**
 * 用于展示属性图的弹窗
 */
class PropertyTreeGraph extends React.Component {

  render () {
    const dv = new DataSet.View().source(this.props.data, {
      type: "hierarchy",
      pureData: true
    });
    dv.transform({
      type: "hierarchy.indented",
      // this layout algorithm needs to use pure data
      direction: "LR",
    });
    return (
      <Modal title={ this.props.title }
             width="85%"
             visible={ this.props.visible }
             onCancel={ this.props.dismiss }
             onOk={ this.props.dismiss }>
        <Chart
          width={ window.innerWidth * .8 }>
          <Coord reflect="y"/>
          <Tooltip/>
          <View
            data={ dv.getAllLinks().map(link => ({
              x: [link.source.x, link.target.x],
              y: [link.source.y, link.target.y],
              source: link.source.id,
              target: link.target.id
            })) }>
            <Geom
              type="edge"
              position="x*y"
              shape="smooth"
              color="grey"
              opacity={ .5 }
            />
          </View>
          <View
            data={ dv.getAllNodes().map(node => ({
              hasChildren: !!(node.children && node.children.length),
              name: node.data.name,
              value: node.data.value,
              side: node.side || "root",
              x: node.x,
              y: node.y
            })) }>
            <Geom type="point" position="x*y" color="hasChildren" tooltip="value">
              <Label
                content="name"
                textStyle={ (text, item) => {
                  return {
                    fill: "grey",
                    fontSize: 11,
                    textBaseline: "middle",
                  };
                } }
                offset={ 5 }/>
            </Geom>
          </View>
        </Chart>
      </Modal>
    );
  }
}

/**
 * 用于展示属性详情的弹窗
 */
class PropertyDetail extends React.Component {

  convertToList (data, ref, prefix) {
    if (ref === undefined || data == undefined) {
      ref = []
    }
    const thisPrefix = prefix == undefined ? "" : prefix;
    if (data.children === undefined) {
      ref.push({
        "name": data.name,
        "value": data.value,
        prefix: thisPrefix
      })
    } else {
      for (const child of data.children) {
        const nextPrefix = thisPrefix.length === 0 ? data.name : thisPrefix + ' > ' + data.name;
        this.convertToList(child, ref, nextPrefix)
      }
    }
    return ref
  }

  render () {
    const list = this.convertToList(this.props.data)
    return (
      <Modal
        title={ this.props.title }
        visible={ this.props.visible }
        width="85%"
        onCancel={ this.props.dismiss }
        onOk={ this.props.dismiss }>

        {
          list.map(item => {
            return (
              <div key={ item.prefix + item.name } className={ styles["item-info"] }>
                <span className={ styles["item-info-span-title"] }>{ item.name }</span><br/>
                <span className={ styles["item-info-span-content"] }>
                  { item.value === "" ? "<none>" : item.value }
                </span>
                <span className={ styles["tooltips"] }>{ item.prefix }</span>
              </div>
            );
          })
        }

      </Modal>
    );
  }

}

export default InstanceMonitor;
