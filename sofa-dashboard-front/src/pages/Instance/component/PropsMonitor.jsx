import React from "react";
import {Card} from 'antd';
import styles from '../monitor.less';

/**
 * 应用属性变量相关展示组件
 */
class PropsMonitor extends React.Component {

  render() {
    const hasData = this.props.data['overview'] !== undefined
      && this.props.data['overview'].length > 0;
    return (
      <Card title={this.props.title} bordered={false}
            style={{width: this.props.width ? this.props.width : "44%", paddingLeft: "2%", paddingRight: "2%"}}>
        <div id={this.props.anchor}>
          <div>
            {
              //展示由接口生成的 overview 信息
              hasData ? this.props.data['overview'].map(item => {
                return (
                  <div key={item.key} className={styles["item-info"]}>
                    <span className={styles["item-info-span-title"]}>{this.trimText(item.key, 40)}</span>
                    <span
                      className={[
                        styles["item-info-span-content"],
                        styles["text-right"]
                      ].join(' ')}>{item.value}</span>
                  </div>
                )
              }) : (<p>No data</p>)
            }
          </div>
        </div>
      </Card>
    )
  }

  trimText(origin, limit) {
    return origin.length > limit ? origin.substring(0, limit) + "..." : origin;
  }
}

export default PropsMonitor;
