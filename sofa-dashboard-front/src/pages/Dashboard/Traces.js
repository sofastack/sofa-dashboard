import React, { Component } from 'react';
import { connect } from 'dva';
import {Empty} from "antd";
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
// profile 的数据会被挂在到 this.props
@connect(({ actuator }) => ({
    actuator,
}))
class Traces extends Component {

    render() {
        return (
            <PageHeaderWrapper>
                <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description="欢迎共建"
                />
            </PageHeaderWrapper>
        );
    }
}
export default Traces;
