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

    public <T extends Serializable> void send(String queueName, T obj) {
        jmsTemplate.send(queueName, session -> {
            ObjectMessage objMessage = session.createObjectMessage(obj);
            return objMessage;
        });
    }
}
