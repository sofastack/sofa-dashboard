import React, { Component } from 'react';
import { connect } from 'dva';
import {Card, Table, Tabs,Collapse} from 'antd';

import {
    Chart,
    Geom,
    Axis,
    Tooltip,
    Legend,
} from "bizcharts";

import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import styles from './Environment.less';
const Panel = Collapse.Panel;
const TabPane = Tabs.TabPane;
// profile 的数据会被挂在到 this.props
@connect(({ actuator }) => ({
    actuator,
    info: actuator.info,
    health: actuator.health,
    threads: actuator.threads,
    heap: actuator.heap,
    nonheap: actuator.nonheap,
    env: actuator.env,
    loggers: actuator.loggers,
    mappings: actuator.mappings,
    threaddump: actuator.threaddump,
}))
class Environment extends Component {

  componentDidMount() {
    const { dispatch, location } = this.props;
    const queryParams = location.query;
    dispatch({
        type: 'actuator/fetch',
        payload: queryParams
    });
  }



render() {
    const { dispatch,location } = this.props;
    const queryParams = location.query.id;

    function getThreadData(){
        dispatch({
                type: 'actuator/fetchThreadDump',
                payload:{
                    id: queryParams,
                }
            },
        )
    }

    function getInfoData(){
        dispatch({
                type: 'actuator/fetch',
                payload:{
                    id: queryParams,
                }
            },
        )
    }

    function callback(key) {

        if ( key=== "1") {
            // 刷新单位为15s
            setInterval(getInfoData,15000);

        }else if (key === "3"){

            dispatch({
                type: 'actuator/fetchEnv',
                payload:{
                    id: queryParams,
                }
            });
        }else if (key === "4"){
            dispatch({
                type: 'actuator/fetchMapping',
                payload:{
                    id: queryParams,
                }
            });
        }else if (key === "5"){
            // 刷新间隔为3s
            setInterval(getThreadData,3000);
        }
        else if (key === "6"){
            dispatch({
                type: 'actuator/fetchLogger',
                payload:{
                    id: queryParams,
                }
            });
        }
    }
    // thread 图标数据初始化
    var threadsDv = new DataSet.View().source(this.props.threads);
    threadsDv.transform({
        type: "fold",
        fields: ["LIVE", "DAEMON","PEAK"],
        key: "type",
        nums: "value"
    });
    const scale = {
        nums: {
            // y 轴从0开始
            min: 0,
            alias: "线程数",
        },
        time: {
            range: [0, 1]
        }
    };


    // memory heap 图标数据初始化
    const heapDv = new DataSet.View().source(this.props.heap);
    heapDv.transform({
        type: "fold",
        fields: ["size", "used"],
        key: "type",
        nums: "value"
    });
    const heapScale = {
        nums: {
            min: 0,
            alias: "memory heap",
        },
        time: {
            range: [0, 1]
        }
    };

    // memory nonheap 图标数据初始化
    const nonHeapDv = new DataSet.View().source(this.props.nonheap);
    nonHeapDv.transform({
        type: "fold",
        fields: ["size", "used","metaspace"],
        key: "type",
        nums: "value"
    });
    const nonHeapScale = {
        nums: {
            min: 0,
            alias: "non-memory heap",
        },
        time: {
            range: [0, 1]
        }
    };

    const byteConvert = function (bytes) {
        if (Math.floor(bytes/(8*1024*1024*1024)) !== 0){
            return (bytes/(8*1024*1024*1024)).toFixed(2)+" GB";
        }else if (bytes/(8*1024*1024) !== 0){
            return (bytes/(8*1024*1024)).toFixed(2)+" MB";
        }else{
            return (bytes/(8*1024)).toFixed(2)+" KB";
        }
    }


    const threadDumpColor = function (threadState) {
        if (threadState === 'TIMED_WAITING' || threadState === 'WAITING'){
            return "wait-state-color";
        }else if (threadState === 'RUNNABLE'){
            return "runnable-color";
        }else{
            return "other-color";
        }
    }

    const dispatchCol = [
        {
            title: 'predicate',
            dataIndex: 'predicate',
            key: 'predicate',
            className: styles["table-col"],
            render: text => <div style={{overflow:"auto",width:"150px",wordWrap:"break-word"}}>{text}</div>,
        },
        {
            title: 'methods',
            dataIndex: 'methods',
            key: 'methods',

        },
        {
            title: 'paramsType',
            dataIndex: 'paramsType',
            key: 'paramsType',
            render: text => <div style={{overflow:"auto",width:"180px",wordWrap:"break-word" }}>{text}</div>,
        },
        {
            title: 'responseType',
            dataIndex: 'responseType',
            key: 'responseType',
            render: text => <div style={{overflow:"auto",width:"100px",wordWrap:"break-word" }}>{text}</div>,
        },
        {
            title: 'handler',
            dataIndex: 'handler',
            key: 'handler',
            render: text => <div style={{overflow:"auto",width:"300px",wordWrap:"break-word" }}>{text}</div>,
        },
    ];

    const filtersCol = [
        {
            title: 'Url Pattern',
            dataIndex: 'urlPatternMappings',
            key: 'urlPatternMappings',
        },
        {
            title: 'Servlet Name',
            dataIndex: 'servletNameMappings',
            key: 'servletNameMappings',
        },
        {
            title: 'Filter Name',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Class',
            dataIndex: 'className',
            key: 'className',
        },
    ];

    const servletCol = [
        {
            title: 'mappings',
            dataIndex: 'mappings',
            key: 'mappings',
        },
        {
            title: 'Servlet Name',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Class',
            dataIndex: 'className',
            key: 'className',
        },
    ];



    return (
        <PageHeaderWrapper>
          <Tabs defaultActiveKey="1" onChange={callback}>
              <TabPane tab="应用详情" key="1">
                  <div className={styles["tab-card"]}>
                      <Card title="Info" bordered={false} style={{ width: "100%" }}>
                          <div>
                              {
                                  Object.keys(this.props.info).map((key) => {
                                      const item = this.props.info[key];
                                      return (
                                              <div key={key} className={styles["item-info"]}>
                                                  <span className={styles["item-info-span-title"]}>{key}</span>
                                                  <span className={styles["item-info-span-content"]}>{item}</span>
                                              </div>
                                      );
                                  })
                              }
                          </div>
                      </Card>
                  </div>

                  <div className={styles["tab-card"]}>
                      <Card title="Health" bordered={false} style={{ width: "100%" }}>
                          <div className={styles["item-info"]}>
                              <span className={styles["item-info-span-title"]}>INSTANCE</span>
                              <span className={styles["item-info-span-content"]} style={{textAlign:"right"}}>{this.props.health.status}</span>
                          </div>
                          {
                              Object.keys(this.props.health).length && this.props.health.details.map(item => {
                                  return (
                                      <div key={item.name} className={styles["item-info"]} style={{textIndent:4}}>
                                          <span className={styles["item-info-span-title"]}>{item.name}</span>
                                          <span className={styles["item-info-span-content"]} style={{textAlign:"right"}}>{item.status}</span>
                                          <div>
                                              {
                                                  item.details && Object.keys(item.details).map(key =>{
                                                      const detailItem = item.details[key];
                                                      if(!(typeof detailItem === "object")){
                                                          if (typeof detailItem === "number") {
                                                              return(
                                                                  <div key={key} className={styles["item-info"]} style={{textIndent:16}}>
                                                                      {key}<span className={styles["item-info-span-content"]}>{byteConvert(detailItem)}</span>
                                                                  </div>
                                                              )
                                                          }

                                                      }
                                                  })
                                              }
                                          </div>
                                      </div>
                                  )
                              })
                          }
                      </Card>
                  </div>

                  <div className={styles["tab-card"]}>
                      <Card title="Threads" bordered={false} style={{ width: "100%" }}>
                          <div>
                              <Chart
                                  height={280}
                                  data={threadsDv}
                                  padding={"auto"}
                                  scale={scale}
                                  forceFit
                              >
                                  <Tooltip crosshairs />
                                  <Axis />
                                  <Legend />
                                  <Geom type="area" position="time*nums" color="tags" shape="smooth" />
                                  <Geom
                                      type="line"
                                      position="time*nums"
                                      color="tags"
                                      shape="smooth"
                                      size={2}
                                  />
                              </Chart>
                          </div>
                      </Card>
                  </div>

                  <div className={styles["tab-card"]}>
                      <Card title="Memory:Heap" bordered={false} style={{ width: "100%" }}>
                          <div>
                              <Chart
                                  height={280}
                                  data={heapDv}
                                  padding={"auto"}
                                  scale={heapScale}
                                  forceFit
                              >
                                  <Tooltip crosshairs />
                                  <Axis />
                                  <Legend />
                                  <Geom type="area" position="time*nums" color="tags" shape="smooth" />
                                  <Geom
                                      type="line"
                                      position="time*nums"
                                      color="tags"
                                      shape="smooth"
                                      size={2}
                                  />
                              </Chart>
                          </div>
                      </Card>
                  </div>

                  <div className={styles["tab-card"]}>
                      <Card title="Memory:Non Heap" bordered={false} style={{ width: "100%" }}>
                          <div>
                              <Chart
                                  height={280}
                                  data={nonHeapDv}
                                  padding={"auto"}
                                  scale={nonHeapScale}
                                  forceFit
                              >
                                  <Tooltip crosshairs />
                                  <Axis />
                                  <Legend />
                                  <Geom type="area" position="time*nums" color="tags" shape="smooth" />
                                  <Geom
                                      type="line"
                                      position="time*nums"
                                      color="tags"
                                      shape="smooth"
                                      size={2}
                                  />
                              </Chart>
                          </div>
                      </Card>
                  </div>
              </TabPane>
              <TabPane tab="Environment" key="3">
                  <div className={styles["tab-env-block"]}>
                      <Card title="activeProfiles" bordered={false} style={{width: "100%"}}>
                      {
                          Object.keys(this.props.env).length && this.props.env["activeProfiles"].map((key,i)=>{
                              return(
                                  <div key={i} className={styles["item-info"]}>
                                      {key}
                                  </div>
                               )
                          })
                      }
                      </Card>
                  </div>
                      {
                          Object.keys(this.props.env).length && this.props.env["propertySources"].map((item)=> {
                              return (
                                  <div className={styles["tab-env-block"]} key={item.name}>
                                  <Card title={item.name} bordered={false} style={{width: "100%"}}>
                                      <div key={item.name}>
                                          {
                                              Object.keys(item.properties).length && item.properties.map((property,index)=>{
                                                  return (
                                                      <div key={index}>
                                                          {
                                                              Object.keys(property).map((itemKey,i)=>{
                                                                  return(
                                                                      <div key={i} className={styles["item-info"]}>
                                                                          <span className={styles["item-info-span-title"]}>{itemKey}</span>
                                                                          <span className={styles["item-info-span-content"]}>{property[itemKey]}</span>
                                                                      </div>
                                                                  )
                                                              })
                                                          }
                                                      </div>
                                                  )
                                              })
                                          }
                                      </div>
                                  </Card>
                                  </div>
                              )
                          })
                      }
              </TabPane>
              <TabPane tab="mappings" key="4">
                  {
                      Object.keys(this.props.mappings).length &&Object.keys(this.props.mappings).map((appName)=> {
                          const appItem = this.props.mappings[appName];
                          return (
                              <div className={styles["tab-env-block"]} key={appName}>
                                  <Card title={appName} bordered={false} style={{width: "100%"}}>
                                      <div className={styles["tab-env-block"]}>
                                          <Card title="dispatcherServlets" bordered={false}>
                                              <Table
                                                  dataSource={appItem.dispatcherServlet}
                                                  columns={dispatchCol}
                                                  pagination={ false }
                                                  rowKey={r => (r.predicate+r.responseType+r.methods)}/>
                                          </Card>
                                      </div>
                                      <div className={styles["tab-env-block"]}>
                                          <Card title="servletFilters" bordered={false} style={{width: "100%"}}>
                                              <Table
                                                  dataSource={appItem.servletFilters}
                                                  columns={filtersCol}
                                                  pagination={ false }
                                                  scroll={{ x: true}}
                                                  rowKey={r => r.name}/>
                                          </Card>
                                      </div>

                                      <div className={styles["tab-env-block"]}>
                                          <Card title="servlets" bordered={false} style={{width: "100%"}}>
                                              <Table
                                                  dataSource={appItem.servlets}
                                                  columns={servletCol}
                                                  pagination={ false }
                                                  scroll={{ x: true}}
                                                  rowKey={r => r.name}/>
                                          </Card>
                                      </div>
                                  </Card>
                              </div>
                          )
                      })
                  }
              </TabPane>
              <TabPane tab="Threads" key="5">
                  <Collapse bordered={false} >
                      {
                          this.props.threaddump.length && this.props.threaddump.map((thread,i)=> {
                                  const state = thread.threadState;
                                  const classNameVal =threadDumpColor(state);
                                  return(
                                      <Panel header={thread.threadName} key={i} showArrow={false} className={styles[classNameVal]}>
                                              {
                                                  Object.keys(thread).map((key,index)=> {
                                                      return(
                                                          <div key={index} className={styles["item-info"]}>
                                                              <span className={styles["item-info-span-title"]}>{key}</span>
                                                              <span className={styles["item-info-span-content"]}>{thread[key]}</span>
                                                          </div>
                                                      )
                                                  })
                                              }
                                      </Panel>
                                  )
                          })
                      }
                  </Collapse>
              </TabPane>
              <TabPane tab="Loggers" key="6">
                  <div className={styles["tab-env-block"]}>
                      <Card title="levels" bordered={false} style={{width: "100%"}}>
                          <div className={styles["item-info"]}>
                          {
                              Object.keys(this.props.loggers).length && this.props.loggers["levels"].map((key,i)=>{
                                  // 输出html 必须要 return
                                  return(
                                      <span key={i} className={styles["item-info-span-inline"]}> {key}</span>
                                  )
                              })
                          }
                          </div>
                      </Card>
                  </div>

                  {
                      Object.keys(this.props.loggers).length && Object.keys(this.props.loggers["loggers"]).map((key)=> {
                          const loggerItem = this.props.loggers["loggers"][key];
                          return (
                              <div className={styles["tab-env-block"]} key={key}>
                                  <Card title={key} bordered={false} style={{width: "100%"}}>
                                      {
                                          Object.keys(loggerItem).map((itemKey)=>{
                                             return(
                                                 <div key={itemKey} className={styles["item-info"]}>
                                                     <span className={styles["item-info-span-title"]}>{itemKey}</span>
                                                     <span className={styles["item-info-span-content"]}>{loggerItem[itemKey]}</span>
                                                 </div>
                                             )
                                          })
                                      }
                                  </Card>
                              </div>
                          )
                      })
                  }

              </TabPane>
          </Tabs>
        </PageHeaderWrapper>
    );
  }
}
export default Environment;
