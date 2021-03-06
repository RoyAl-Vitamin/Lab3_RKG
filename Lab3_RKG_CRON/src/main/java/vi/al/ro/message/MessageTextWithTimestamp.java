package vi.al.ro.message;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Сообщение с текстом и датой
 */
public class MessageTextWithTimestamp implements Serializable {

    /**
     * Текст сообщения
     */
    private String text;

    /**
     * Время отправки сообщения в канал
     */
    private long timestamp;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
