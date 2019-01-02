package vi.al.ro.entity;

import com.google.gson.Gson;

import javax.persistence.*;

/**
 * Сущность сообщения
 */
@Entity
@Table(name="MESSAGE", schema="public")
public class Message {

    @Id
    @Column(name = "MESSAGE_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * Текст сообщения
     */
    @Column(name = "MESSAGE_TEXT", nullable = false)
    private String text;

    /**
     * Время отправки сообщения в канал
     */
    @Column(name = "MESSAGE_TIMESTAMP", nullable = false)
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
