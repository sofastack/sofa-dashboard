import React from "react";
import styles from '../monitor.less';
import { Card } from "antd";
import ReactJson from 'react-json-view'

/**
 * 用于展示属性详情的弹窗
 */
class PropertyDetail extends React.Component {

  convertToList(data, ref, prefix) {
    if (ref === undefined || data === undefined) {
      ref = []
    }
    const thisPrefix = prefix === undefined ? "" : prefix;
    if (data === undefined) {
      // Do nothing
    } else if (data.children === undefined) {
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

  render() {
    const list = this.convertToList(this.props.data);
    console.log("this.props.data", this.props.data);
    return (
      <Card title={this.props.title}>
        <ReactJson src={this.props.data}
          collapsed={3}
          // 当设置为true，对象和数组被标记为大小。例如: { a: 'a1',b: 'b1' },会显示2 items
          displayObjectSize={false}
          // 当设置为true，数据类型会出现在数据的前缀值.例如: { a: 123, b: 'b1'},会显示{ a: int 123, b: string 'b1'}
          displayDataTypes={false}
        />
        {/* {
          <TreeNode title={item.title} key={item.key} dataRef={item}>
            {this.renderTreeNodes(item.children)}
          </TreeNode>
          // list.map(item => {
          //   return (
          //     <div key={item.prefix + item.name} className={styles["item-info"]}>
          //       <span className={styles["item-info-span-title"]}>{item.prefix + item.name}</span><br />
          //       <span className={styles["item-info-span-content"]}>
          //         {item.value === "" ? "<none>" : item.value}
          //       </span>
          //     </div>
          //   );
          // })
        } */}
      </Card>
    );
  }

}

export default PropertyDetail;
