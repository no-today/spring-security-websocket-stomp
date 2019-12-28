<template>
  <div id="app">
    <Col offset="1" span="10">
      <Card id="config">
        <p slot="title">
          Config
        </p>
        <Form>
          <FormItem label="Auth">
            <i-switch size="small" v-model="form.enableAuth"/>
            <Input size="large" v-model="form.token" v-show="form.enableAuth" placeholder="Url carry authentication information, including parameter name: access_token=*"/>
          </FormItem>
          <FormItem label="Socket Endpoint">
            <Input size="large" v-model="form.endpoint" placeholder="Socket Endpoint..."/>
          </FormItem>
          <FormItem>
            <Button type="primary" @click="connect" :disabled="connected">Connect</Button>
            <Button style="margin-left: 8px" @click="disconnect" :disabled="!connected">DiConnect</Button>
          </FormItem>

          <FormItem label="Subscribe Topic">
            <Select size="large" v-model="form.subscribes" filterable multiple allow-create @on-create="addSubscribe">
              <Option v-for="subscribe in form.subscribes" :value="subscribe" :key="subscribe">{{ subscribe }}</Option>
            </Select>
          </FormItem>
          <FormItem>
            <Button type="primary" @click="subscribe" :disabled="!connected">Subscribe</Button>
          </FormItem>

          <FormItem label="SendTo">
            <Input size="large" v-model="form.sendTo" placeholder="Mapping..."/>
          </FormItem>
          <FormItem label="Message">
            <Input size="large" v-model="form.content" placeholder="Message..."/>
          </FormItem>
          <FormItem>
            <Button type="primary" @click="send" :disabled="!connected">Send</Button>
          </FormItem>
        </Form>
      </Card>
    </Col>

    <Col id="message" offset="1" span="11">
      <Card>
        <p slot="title">
          Message
        </p>
        <a href="#" slot="extra" @click.prevent="table.messages = []">
          <Icon type="ios-loop-strong"></Icon>
          Clear
        </a>
        <Table :max-height="height - 52" :columns="table.headers" :data="table.messages"></Table>
      </Card>
    </Col>
  </div>
</template>

<script>
  import SockJS from "sockjs-client";
  import Stomp from "webstomp-client";

  export default {
  name: 'app',
  data() {
    return {
      height: 0,
      form: {
        enableAuth: false,
        token: 'access_token=',
        subscribes: ['/topic/tracker', '/topic/dashboard'],
        endpoint: '/websocket/tracker',
        sendTo: '/topic/activity',
        content: ''
      },
      table: {
        headers: [
          {title: 'Subscribe', key: 'subscribe', width: 150},
          {title: 'Time', key: 'time', width: 160},
          {title: 'Content', key: 'content', tooltip: true},
        ],
        messages: []
      },
      connected: false,
      client: {}
    }
  },
  mounted() {
    this.height = document.getElementById("config").offsetHeight;
    document.getElementById('message').style['height'] = this.height + 'px';
    document.getElementById('message').style['max-height'] = this.height + 'px';

    document.getElementsByTagName('table')[0].style['height'] = this.height + 'px';
    document.getElementsByTagName('table')[0].style['max-height'] = this.height + 'px';
  },
  methods: {
    addSubscribe(val) {
      this.form.subscribes.push(val);
    },
    send() {
      console.log("send message:" + this.form.content);
      if (this.client && this.client.connected) {
        this.client.send(this.form.sendTo, this.form.content, {});
      }
    },
    connect() {
      let endpoint = this.form.enableAuth ? this.form.endpoint + '?' + this.form.token :  this.form.endpoint;

      this.client = Stomp.over(new SockJS(endpoint));
      this.client.connect({}, frame => {
          this.connected = true;
        },
        error => {
          this.$Message.error('endpoint connected error');
          this.connected = false;
        }
      );
    },
    disconnect() {
      if (this.client) {
        this.client.disconnect();
      }
      this.connected = false;
    },
    subscribe() {
      this.form.subscribes.forEach(e => {
        this.client.subscribe(e, tick => {
          this.table.messages.push({
            subscribe: e,
            time: new Date().toLocaleTimeString(),
            content: tick.body
          });
        });
      })
    }
  }
}
</script>

<style scoped>
#app {
  font-size: 25px;
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  margin-top: 10px;
  margin-bottom: 10px;
}

#app #message /deep/ .ivu-card-body {
  padding: 0;
}

#app #message /deep/ .ivu-card.ivu-card-bordered {
  height: 100%;
}
</style>
