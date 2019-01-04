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

    /**
     * Кол-во переданных объектов
     */
    private Integer num = null;

    @Value("${message.name}")
    private String ORDER_QUEUE;

    private static ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.createTypeMap(Message.class, MessageTextWithTimestamp.class).addMappings(mapper -> {
            mapper.map(Message::getText, MessageTextWithTimestamp::setText);
            mapper.map(Message::getTimestamp, MessageTextWithTimestamp::setTimestamp);
        });
    }

    public ScheduledTask(Producer producer, MessageRepository messageRepository) {
        this.producer = producer;
        this.messageRepository = messageRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void reportCurrentTime() {
        logger.info("The time is now {}", dateFormat.format(new Date()));

        Integer lastNumber = messageRepository.getLastNumber();

        logger.info("lastNumber == " + lastNumber);
        logger.info("num == " + num);

        if (lastNumber == null) return;

        if (num == null || num < lastNumber) {
            viewDBAndPasteInMQ(lastNumber);
        }
    }

    /**
     * Просматривает БД и ищет изменённые записи
     * @param countAll последний номер сохранённый в БД
     */
    private void viewDBAndPasteInMQ(int countAll) {
        int capacity = countAll;
        if (num != null) {
            capacity -= num;
        } else {
            num = 0;
        }

        Set<Integer> set = new HashSet<>(capacity);
        for (int i = num; i < countAll; i++) {
            set.add(i + 1);
        }
        logger.info("set foreach:");
        set.forEach(System.out::println);
        List<Message> list = messageRepository.getMessages(set);
        try {
            list.forEach(message -> {

                MessageTextWithTimestamp mtwt = modelMapper.map(message, MessageTextWithTimestamp.class);

                logger.debug("mtwt == " + mtwt.toString());
                producer.send(ORDER_QUEUE, mtwt);
            });
        } catch (JmsException e) {
            logger.error("JmsException: ", e);
        }
        num = countAll;
    }
}