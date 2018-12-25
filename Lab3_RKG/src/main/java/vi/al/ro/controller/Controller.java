package vi.al.ro.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vi.al.ro.entity.Message;
import vi.al.ro.message.MessageTextWithTimestamp;
import vi.al.ro.producer.Producer;
import vi.al.ro.repository.MessageRepository;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

@RestController
public class Controller {

    private Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private Producer producer;

    @Autowired
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

    @RequestMapping("send")
    public String send(@RequestParam(name = "text") String text) {
        try {
            MessageTextWithTimestamp mtwt = new MessageTextWithTimestamp();
            mtwt.setText(text);
            mtwt.setTimestamp(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).getTimeInMillis());
            logger.debug("ORDER_QUEUE == " + ORDER_QUEUE);
            producer.send(ORDER_QUEUE, mtwt);

            Message message = modelMapper.map(mtwt, Message.class);

            messageRepository.save(message);

            return "message:\n" + mtwt.toString();
        } catch (JmsException e) {
            return "ERROR";
        }
    }
}
