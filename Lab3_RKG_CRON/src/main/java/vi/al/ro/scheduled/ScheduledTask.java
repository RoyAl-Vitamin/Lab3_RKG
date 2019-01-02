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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private Producer producer;

    private MessageRepository messageRepository;

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

    @Scheduled(fixedRate = 5000, fixedDelay = 5000)
    public void reportCurrentTime() {
        logger.info("The time is now {}", dateFormat.format(new Date()));
//        viewDB();
    }

    /**
     * Просматривает БД и ищет изменённые записи
     */
    private void viewDB() {
        String text = "";
        try {
            MessageTextWithTimestamp mtwt = new MessageTextWithTimestamp();
            mtwt.setText(text);
            mtwt.setTimestamp(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).getTimeInMillis());
            logger.debug("ORDER_QUEUE == " + ORDER_QUEUE);
//            producer.send(ORDER_QUEUE, mtwt);

            Message message = modelMapper.map(mtwt, Message.class);

//            messageRepository.save(message);

//            "message:\n" + mtwt.toString();
        } catch (JmsException e) {
            logger.error("JmsException: ", e);
        }
    }
}