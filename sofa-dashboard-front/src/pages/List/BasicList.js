import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { List, Card, Row, Col, Icon, Dropdown, Menu, Modal, Form } from 'antd';

import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import styles from './BasicList.less';

@connect(({ list, loading }) => ({
  list,
  loading: loading.models.list,
}))
@Form.create()
class BasicList extends PureComponent {
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'list/fetch',
    });
  }

  // 删除确认
  deleteItemConfirm = name => {
    const msg = '删除应用';
    Modal.confirm({
      title: msg + name,
      content: '确定删除该实例吗？',
      okText: '确认',
      cancelText: '取消',
      onOk: () => this.deleteItem(name),
    });
  };

  // 执行删除操作
  deleteItem = name => {
    const { dispatch } = this.props;
    dispatch({
      type: 'list/delete',
      payload: { name },
    });
  };

  render() {
    const {
      list: { list },
      loading,
    } = this.props;

    // 应用数据统计
    const Info = ({ title, value, bordered }) => (
      <div className={styles.headerInfo}>
        <span>{title}</span>
        <p>{value}</p>
        {bordered && <em />}
      </div>
    );
    const { list: data } = this.props;
    const { totalCount, successCount, failCount } = data;
    // 分页插件
    const paginationProps = {
      pageSize: 10,
      total: totalCount,
    };

    // goto详情页
    const gotoDetails = (key, currentItem) => {
        console.log("key",key)
        console.log("currentItem",currentItem)
        if (key === 'jvm') {
            // 跳转到 jvm 详情页
        } else if (key == 'env') {
            // 跳转到 env 详情页
            window.location.href = '/dashboard/env?id='+currentItem.id;
        } else if (key == 'thread') {
            // 跳转到 thread 详情页
        } else if (key == 'health') {
            // 跳转到 health 详情页
        }
    };

    // key -> link 点击选项跳转到具体的详情页面
    const MoreBtn = props => (
        <Dropdown overlay={
                <Menu onClick={({ key }) => gotoDetails(key, props.current)}>
                    <Menu.Item key="jvm">Jvm</Menu.Item>
                    <Menu.Item key="env">Enviroment</Menu.Item>
                    <Menu.Item key="thread">Threads</Menu.Item>
                    <Menu.Item key="health">Health</Menu.Item>
                </Menu>
            }
        >
            <a>
                details <Icon type="down" />
            </a>
        </Dropdown>
    );

    // 页面布局部分
    return (
      <PageHeaderWrapper>
        <div className={styles.standardList}>
          <Card bordered={false}>
            <Row>
              <Col sm={8} xs={24}>
                <Info title="应用总数" value={totalCount} bordered />
              </Col>
              <Col sm={8} xs={24}>
                <Info title="正常" value={successCount} bordered />
              </Col>
              <Col sm={8} xs={24}>
                <Info title="异常" value={failCount} />
              </Col>
            </Row>
          </Card>

          <Card
            className={styles.listCard}
            bordered={false}
            title="应用列表"
            style={{ marginTop: 24 }}
            bodyStyle={{ padding: '0 32px 40px 32px' }}
            // extra={extraContent}
          >
            <List
              size="large"
              rowKey="id"
              loading={loading}
              pagination={paginationProps}
              dataSource={list}
              renderItem={item => (
                <List.Item actions={[<MoreBtn current={item} />]}>
                  <List.Item.Meta
                    // avatar={<Avatar src={item.logo} shape="square" size="large" />}
                    title="应用名"
                    description={item.name}
                  />
                  <List.Item.Meta
                    // avatar={<Avatar src={item.logo} shape="square" size="large" />}
                    title="hostname"
                    description={item.host}
                  />

                  <List.Item.Meta
                    // avatar={<Avatar src={item.logo} shape="square" size="large" />}
                    title="port"
                    description={item.port}
                  />

                  <List.Item.Meta
                    // avatar={<Avatar src={item.logo} shape="square" size="large" />}
                    title="state"
                    description={item.state}
                  />
                  {/* <ListContent data={item} /> */}
                </List.Item>
              )}
            />
          </Card>
        </div>
      </PageHeaderWrapper>
    );
  }
}

export default BasicList;
