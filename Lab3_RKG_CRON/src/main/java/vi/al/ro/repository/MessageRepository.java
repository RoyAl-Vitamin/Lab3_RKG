package vi.al.ro.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import vi.al.ro.entity.Message;

public interface MessageRepository extends PagingAndSortingRepository<Message, Integer> {
}
