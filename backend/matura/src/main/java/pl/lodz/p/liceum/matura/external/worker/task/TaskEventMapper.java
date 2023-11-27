package pl.lodz.p.liceum.matura.external.worker.task;

import org.mapstruct.Mapper;
import pl.lodz.p.liceum.matura.domain.task.Task;

@Mapper(componentModel = "spring")
public interface TaskEventMapper {

    TaskEvent toDto(Task task);

    Task toDomain(TaskEvent taskEvent);

}
