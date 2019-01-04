package vi.al.ro.scheduled;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vi.al.ro.entity.Message;
import vi.al.ro.message.MessageTextWithTimestamp;
import vi.al.ro.producer.Producer;
import vi.al.ro.repository.MessageRepository;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private Producer producer;

    private MessageRepository messageRepository;

    private Integer num = null;

    @Value("${message.name}")
    private String ORDER_QUEUE;

    private static ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.createTypeMap(MessageTextWithTimestamp.class, Message.class).addMappings(mapper -> {
            mapper.map(MessageTextWithTimestamp::getText, Message::setText);
            mapper.map(MessageTextWithTimestamp::getTimestamp, Message::setTimestamp);
        });
    }

    public ScheduledTask(Producer producer, MessageRepository messageRepository) {
        this.producer = producer;
        this.messageRepository = messageRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        logger.info("The time is now {}", dateFormat.format(new Date()));
        Integer lastNumber = messageRepository.getLastNumber();
        logger.info("ORDER_QUEUE == " + ORDER_QUEUE);
        logger.info("lastNumber == " + lastNumber);
        logger.info("num == " + num);
        if (lastNumber != null && (num == null || num < lastNumber)) {
            viewDBAndPasteInMQ(lastNumber);
        }
    }

    /**
     * Просматривает БД и ищет изменённые записи
     * @param lastNumber последний номер сохранённый в БД
     */
    private void viewDBAndPasteInMQ(int lastNumber) {
        int capasity = lastNumber;
        if (num != null) {
            capasity -= num;
        }
        Set<Integer> set = new HashSet<>(capasity);
        for (int i = num; i < lastNumber; i++) {
            set.add(i);
        }
        List<MessageTextWithTimestamp> list = messageRepository.getMessages(set);
        try {
            list.forEach(mtwt -> {
                logger.debug("mtwt == " + mtwt.toString());
                producer.send(ORDER_QUEUE, mtwt);
            });
        } catch (JmsException e) {
            logger.error("JmsException: ", e);
        }
        num = lastNumber;
    }
}