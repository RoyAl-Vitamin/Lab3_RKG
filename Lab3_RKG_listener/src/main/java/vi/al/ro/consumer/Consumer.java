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

//    @Value("${message.name}")
    private static final String ORDER_RESPONSE_QUEUE = "testQueue";

    @JmsListener(destination = ORDER_RESPONSE_QUEUE) // = "${message.name}"
    public void receiveMessage(ObjectMessage message) throws JMSException {
        logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (message instanceof MessageTextWithTimestamp) {
            logger.info("yep");
            MessageTextWithTimestamp mtwt = (MessageTextWithTimestamp) message;
            logger.info("text: " + mtwt.getText());
            logger.info("timestamp: " + mtwt.getTimestamp());
        }
    }

//    @Autowired
//    public void setOrderResponseQueue(@Value("${message.name}") String orderResponseQueue) {
//        this.ORDER_RESPONSE_QUEUE = orderResponseQueue;
//    }
}
