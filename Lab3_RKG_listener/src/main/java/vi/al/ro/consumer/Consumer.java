package vi.al.ro.consumer;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import vi.al.ro.entity.Message;
import vi.al.ro.message.MessageTextWithTimestamp;
import vi.al.ro.repository.MessageRepository;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

@Component
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private static ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.createTypeMap(MessageTextWithTimestamp.class, Message.class).addMappings(mapper -> {
            mapper.map(MessageTextWithTimestamp::getText, Message::setText);
            mapper.map(MessageTextWithTimestamp::getTimestamp, Message::setTimestamp);
        });
    }

    @Autowired
    private MessageRepository messageRepository;

    @JmsListener(destination = "${message.name}")
    public void receiveMessage(ObjectMessage objMessage) throws JMSException {
        if (objMessage.getObject() instanceof MessageTextWithTimestamp) {
            MessageTextWithTimestamp mtwt = (MessageTextWithTimestamp) objMessage.getObject();

            Message message = modelMapper.map(mtwt, Message.class);

            messageRepository.save(message);

            logger.debug("Save OK!");
        }
    }
}
