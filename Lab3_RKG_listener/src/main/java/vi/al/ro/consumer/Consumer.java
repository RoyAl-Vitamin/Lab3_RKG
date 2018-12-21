package vi.al.ro.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import vi.al.ro.message.MessageTextWithTimestamp;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

@Component
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @JmsListener(destination = "${message.name}")
    public void receiveMessage(ObjectMessage message) throws JMSException {
        if (message.getObject() instanceof MessageTextWithTimestamp) {
            logger.info("yep");
            MessageTextWithTimestamp mtwt = (MessageTextWithTimestamp) message.getObject();
            logger.info("text: " + mtwt.getText());
            logger.info("timestamp: " + mtwt.getTimestamp());
        }
    }
}
