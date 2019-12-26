<template>
  <div id="app">
    <Row class="text-center">
      Websocket Stomp Client
    </Row>
    <Row>
      <Col offset="1" span="10">
        <Form>
          <FormItem label="Socket Endpoint">
            <Input size="large" v-model="form.endpoint" placeholder="Socket Endpoint..."/>
          </FormItem>
          <FormItem>
            <Button type="primary" @click="connect" :disabled="connected">Connect</Button>
            <Button style="margin-left: 8px" @click="disconnect" :disabled="!connected">DiConnect</Button>
          </FormItem>

          <FormItem label="Subscribe">
            <Select size="large" v-model="form.subscribes" filterable multiple allow-create @on-create="addSubscribe">
              <Option v-for="subscribe in form.subscribes" :value="subscribe" :key="subscribe">{{ subscribe }}</Option>
            </Select>
          </FormItem>
          <FormItem>
            <Button type="primary" @click="subscribe" :disabled="!connected">Subscribe</Button>
          </FormItem>

          <FormItem label="Topic">
            <Input size="large" v-model="form.topic" placeholder="Topic..."/>
          </FormItem>
          <FormItem label="Message">
            <Input size="large" v-model="form.message" placeholder="Message..."/>
          </FormItem>
          <FormItem>
            <Button type="primary" @click="send" :disabled="!connected">Send</Button>
            <Button style="margin-left: 8px" @click="table.messages = []">Clear</Button>
          </FormItem>
        </Form>
      </Col>
      <Col offset="1" span="11">
        <Table :columns="table.headers" :data="table.messages" resizable border height="550"></Table>
      </Col>
    </Row>
  </div>
</template>

<script>
  import SockJS from "sockjs-client";
  import Stomp from "webstomp-client";

  export default {
  name: 'app',
  data() {
    return {
      baseUrl: 'http://localhost:8090',
      table: {
        headers: [
          {title: 'Time', key: 'time', width: 200},
          {title: 'Content', key: 'content'},
        ],
        messages: []
      },
      form: {
        subscribes: ['/topic/tracker'],
        endpoint: '/websocket/tracker',
        topic: '/topic/activity',
        message: ''
      },
      connected: false,
      client: {}
    }
  },
  methods: {
    addSubscribe(val) {
      this.form.subscribes.push(val);
    },
    send() {
      console.log("send message:" + this.form.message);
      if (this.client && this.client.connected) {
        this.client.send(this.form.topic, this.form.message, {});
      }
    },
    connect() {
      this.client = Stomp.over(new SockJS(this.baseUrl + this.form.endpoint));
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
            time: new Date().toLocaleString(),
            content: JSON.parse(tick.body)
          });
        });
      })
    }
  }
}
</script>

<style>
#app {
  font-size: 25px;
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.text-center {
  text-align: center;
  margin: 30px;
}
</style>
