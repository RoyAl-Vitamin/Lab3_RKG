package vi.al.ro.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import vi.al.ro.entity.Message;
import vi.al.ro.message.MessageTextWithTimestamp;

import java.util.List;
import java.util.Set;

public interface MessageRepository extends PagingAndSortingRepository<Message, Integer> {

    /**
     * @return Возвращает последний ID записанный в БД, он является и кол-вом объектов в БД
     */
    @Query("SELECT MAX(m.id) FROM Message m")
    Integer getLastNumber();

    /**
     * @param set список ID
     * @return Возвращает все сообщения с задаными ID
     */
    @Query("SELECT m FROM Message m WHERE m.id IN :set")
    List<Message> getMessages(@Param("set") Set<Integer> set);
}
