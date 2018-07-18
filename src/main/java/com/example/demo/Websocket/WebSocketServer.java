package com.example.demo.Websocket;

import com.example.demo.Entity.UserDevice;
import com.example.demo.Entity.UserToken;
import com.example.demo.Service.Impl.UserDeviceServiceImpl;
import com.example.demo.Service.Impl.UserTokenServiceImpl;
import com.example.demo.Service.UserDeviceService;
import com.example.demo.Service.UserTokenService;
import com.example.demo.Utils.DataProcess;
import com.example.demo.Utils.MQTTUtil;
import com.example.demo.Utils.RealTimeUtil;
import com.example.demo.Utils.SpringBeanFactoryUtil;
import org.fusesource.mqtt.client.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * Created with IntelliJ IDEA.
 * User: yunxi
 * Date: 2018/7/13
 * Time: 9:17
 * Description: No Description
 */

// 其中devEUI为设备唯一编号
@ServerEndpoint(value = "/websocket/{devEUI}/{token}")
@Component
public class WebSocketServer {

    private static UserTokenService userTokenService = SpringBeanFactoryUtil.getBean(UserTokenServiceImpl.class);
    private static UserDeviceService userDeviceService = SpringBeanFactoryUtil.getBean(UserDeviceServiceImpl.class);

    private static int  onLineCount = 0;// 在线数目
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static ConcurrentHashMap<String, WebSocketServer> webSocketSet = new ConcurrentHashMap<String, WebSocketServer>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String id=""; //  用户id
    private String devEUI="";

    private boolean flag;//订阅状态
    private boolean exitStatue = false;//OnClose时判定接收线程是否退出

//    private BlockingConnection connection;

    //记录用户状态
    public static HashMap<String, Boolean> userState = new HashMap<String, Boolean>();

    /**
     * 连接建立成功调用的方法*/

    @OnOpen
    public void onOpen(@PathParam(value="devEUI") String devEUI, @PathParam(value="token") String token, Session session){
        //token认证
        UserToken userToken = userTokenService.findByToken(token);
        if (userToken == null) {
            throw new RuntimeException("用户名不存在或token已过期！");
        }

        this.session = session;
        this.devEUI = devEUI;
        this.id = userToken.getUserid();//接收到发送消息的人员编号

        //校验该user下有无对应设备devEUI
        if (!checkUserDevEUI(this.id, this.devEUI)) {
            throw new RuntimeException("该用户下不存在相应设备！");
        }

        webSocketSet.put(id, this); // 加入set中
        addOnlineCount(); // 在线人数+1
        System.out.println("有新连接加入，当前在线人数为,用户"+this.id +"----" + getOnlineCount());

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            System.out.println("websocket IO异常");
            e.printStackTrace();
        }

        //新连接建立，开始推数据
        subscribe(makeTopic(this.devEUI), this.id);

    }

    @OnClose
    public void  onClose(){

        System.out.println("等待线程结束...");
//        try {
//            connection.disconnect();
//        } catch (Exception e) {
//            System.out.println("disconnect 异常！");
//            e.printStackTrace();
//        }
//        webSocketSet.remove(this);
//        subOnlineCount();// 在线人数--

        //关闭socket连接时取消用户的订阅
        this.flag = false;
        while (true) {
            if (this.exitStatue) {
                System.out.println(this.id + "断开连接");
                webSocketSet.remove(this);
                subOnlineCount();// 在线人数--
                break;
            }
        }

        System.out.println("=======退出订阅！======");

    }

    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("来自客户端"+this.id+"的消息 "+ message);
        // 一旦有用户订阅消息，就像用户推送事实的传感器信息
        //群发消息
/*        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @OnError
    public void onError(Session session, Throwable error){

        System.out.println("发生错误");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException{

        this.session.getBasicRemote().sendText(message); // 发送消息

    }

    public void sendtoUser(String  message, String sendUserId) throws IOException{
        if (webSocketSet.get(sendUserId) != null){

            if (!id.equals(sendUserId))
                webSocketSet.get(sendUserId).sendMessage("用户" + id + "发来消息" + message);
            else
                webSocketSet.get(sendUserId).sendMessage(message);
        }else {
            sendtoUser("当前用户不在线",this.id);
        }
    }

    /**
     *
     *群发自定义消息
     * @param message
     * @throws IOException
     */
    public void sendtoAll(String message) throws IOException {
        for (String key : webSocketSet.keySet()) {
            try {
                webSocketSet.get(key).sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 线程安全的返回在线人数
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onLineCount;
    }
    public static synchronized void addOnlineCount() {
        WebSocketServer.onLineCount++;
    }
    public static synchronized void subOnlineCount() {
        WebSocketServer.onLineCount--;
    }


    //MQTT处理
    //订阅
    public void subscribe(String topic, String userid) {
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost("tcp://39.106.54.222:1883");
//			mqtt.setHost("tcp://www.liuyunxing.cn:1883/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//		mqtt.setUserName("test2");
//		mqtt.setPassword("123456");
        mqtt.setUserName("cdx");
        mqtt.setPassword("cdxhhhhh");
        BlockingConnection connection = mqtt.blockingConnection();
        try {
            connection.connect();
            // 成功建立连接
            this.flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("mqtt连接成功！订阅" + topic +"：");
        Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};
        try {
            byte[] qoses = connection.subscribe(topics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //退出条件根据webSocket userid的状态实现，设置一个状态值
        new Thread(new Runnable() {
            @Override
            public void run() {
//                int timeout = 30;
//                int time = 0;
                while(flag) {
                    Message message = null;
                    try {
                        //receive最多等待5秒继续执行下面的代码
                        message = connection.receive(5, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        System.out.println("receive 异常！");
                        e.printStackTrace();
                        break;
                    }
                    if (message != null) {
//                        time = 0;

                        //处理data
                        List<JSONObject> data = DataProcess.realTimeDataHander(new String(message.getPayload()));
                        try {
                            if (data != null) {
                                sendtoUser(data.toString(), userid);
                            }
                        } catch (IOException e) {
                            System.out.println("sendtoUser 异常！");
                            e.printStackTrace();
                            break;
                        }
                        message.ack();
                    } else {
//                        time += 5;
//                        if (time >= timeout) {
//                            throw new RuntimeException("长时间无数据，退出连接");
//                        }
                        System.out.println("暂无数据...");
                    }
                }
                exitStatue = true;
            }
        }).start();
    }

    //构造topic，这里只需rx
    //type : rx   tx
    public static String makeTopic(String devEUI) {
        return "application/2/device/" + devEUI + "/rx";
    }


    //校验user有无devEUI
    public boolean checkUserDevEUI(String userid, String devEUI) {
        List<UserDevice> userDeviceList = userDeviceService.findAllByUserid(userid);
        for (UserDevice userDevice : userDeviceList) {
            if (userDevice.getDevEUI().equals(devEUI)) {
                return true;
            }
        }
        return false;
    }
}
