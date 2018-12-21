package vi.al.ro.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;

@Component
public class Producer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(String queueName, String text) {
        jmsTemplate.send(queueName, session -> {
            TextMessage textMessage = session.createTextMessage(text);
            return textMessage;
        });
    }

    public <T extends Serializable> void send(String queueName, T obj) {
        jmsTemplate.send(queueName, session -> {
            ObjectMessage objMessage = session.createObjectMessage(obj);
            return objMessage;
        });
    }
}
