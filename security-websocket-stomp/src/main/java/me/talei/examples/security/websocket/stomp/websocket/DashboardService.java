package me.talei.examples.security.websocket.stomp.websocket;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.websocket.server.ServerEndpoint;
import java.util.Random;
import java.util.UUID;

/**
 * @author levi
 * @date 2019/12/27 9:49
 */
@Controller
public class DashboardService {

    private final SimpMessageSendingOperations messagingTemplate;

    public DashboardService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 模拟后端数据更新了, 通知前端事实更新
     */
    @PostConstruct
    public void init() {
        Random random = new Random();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(random.nextInt(10000) + 5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messagingTemplate.convertAndSend("/topic/dashboard", UUID.randomUUID().toString());
            }
        }).start();
    }
}
