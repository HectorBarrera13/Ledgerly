package toast.appback.src.shared.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.model.entities.CursorIdentifiable;

public class PageMapper {
    private PageMapper() {
    }

    public static Pageable toPageable(PageRequest pageRequest) {
        return org.springframework.data.domain.PageRequest.of(
                pageRequest.page(),
                pageRequest.size()
        );
    }

    public static <U> Pageable toPageable(CursorRequest<U> pageRequest) {
        return org.springframework.data.domain.PageRequest.of(
                0,
                pageRequest.limit()
        );
    }

    public static <T extends CursorIdentifiable<U>, U> PageResult<T, U> toPageResult(Page<T> page) {
        U nextCursor = null;
        if (page.hasNext()) {
            T lastElement = page.getContent().getLast();
            nextCursor = lastElement.getCursorId();
        }
        return new PageResult<>(page.getContent(), nextCursor);
    }
}
