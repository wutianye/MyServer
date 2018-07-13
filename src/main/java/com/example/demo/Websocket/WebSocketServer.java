package com.example.demo.Websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created with IntelliJ IDEA.
 * User: yunxi
 * Date: 2018/7/13
 * Time: 9:17
 * Description: No Description
 */

// 其中id为用户的id
@ServerEndpoint(value = "/websocket/{id}")
@Component
public class WebSocketServer {

    private static int  onLineCount = 0;// 在线数目
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static ConcurrentHashMap<String, WebSocketServer> webSocketSet = new ConcurrentHashMap<String, WebSocketServer>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String id=""; //  用户id

    /**
     * 连接建立成功调用的方法*/

    @OnOpen
    public void onOpen(@PathParam(value="id") String id, Session session){

        this.session = session;
        this.id = id;//接收到发送消息的人员编号
        webSocketSet.put(id, this); // 加入set中
        addOnlineCount(); // 在线人数+1
        System.out.println("有新连接加入，当前在线人数为,用户"+this.id +"----" + getOnlineCount());
        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            System.out.println("websocket IO异常");
            e.printStackTrace();
        }
    }

    @OnClose
    public void  onClose(){
        System.out.println("断开连接");
        System.out.println(this.id);;
        webSocketSet.remove(this);
        subOnlineCount();// 在线人数--
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

}
