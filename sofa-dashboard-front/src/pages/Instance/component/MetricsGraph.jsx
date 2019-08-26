import React from "react";
import { Card } from 'antd';
import DataSet from "@antv/data-set";
import { Axis, Chart, Geom, Legend, Tooltip, } from "bizcharts";

class MetricsGraph extends React.Component {

  render() {
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
      <Card title={this.props.title} bordered={false}
        style={{ width: "44%", paddingLeft: "2%", paddingRight: "2%" }}>
        <Chart data={this.props.data} height={300} scale={{
          value: {
            min: 0
          },
          timestamp: {
            range: [0, 1]
          }
        }}>
          <Axis name="date" />
          <Axis name="value" />
          <Legend />
          <Tooltip crosshairs={{ type: "y" }} />
          <Geom type="line" position="timestamp*value" color="name" />
          <Geom type="line" position="timestamp*value" size={2} color="name" />
        </Chart>
      </Card>
    );
  }
}

export default MetricsGraph;
