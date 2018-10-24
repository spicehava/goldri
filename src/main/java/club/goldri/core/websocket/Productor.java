package club.goldri.core.websocket;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Productor {

    public static void main(String[] args) throws IOException, TimeoutException {
        String QUEUE_NAME = "cc3u-mq";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.110");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        for(int i=0;i<100;i++){
            String message = "Hello World!" + (i + 1);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }

        channel.close();
        connection.close();
    }
}
