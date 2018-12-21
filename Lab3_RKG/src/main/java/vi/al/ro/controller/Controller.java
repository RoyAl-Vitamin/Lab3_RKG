package vi.al.ro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vi.al.ro.message.MessageTextWithTimestamp;
import vi.al.ro.producer.Producer;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

@RestController
public class Controller {

    @Autowired
    private Producer producer;

    @RequestMapping("send")
    public String send(@RequestParam(name = "text") String text) {
        try {
            MessageTextWithTimestamp mtwt = new MessageTextWithTimestamp();
            mtwt.setText(text);
            mtwt.setTimestamp(Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).getTimeInMillis());
            producer.send("testQueue", mtwt);
            return "message:\n" + mtwt.toString();
        } catch (JmsException e) {
            return "ERROR";
        }
    }
}
