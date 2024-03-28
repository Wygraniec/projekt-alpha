package pl.lodz.p.liceum.matura.external.worker.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.config.KafkaConfiguration;
import pl.lodz.p.liceum.matura.domain.result.Result;
import pl.lodz.p.liceum.matura.domain.result.ResultService;
import pl.lodz.p.liceum.matura.domain.subtask.Subtask;
import pl.lodz.p.liceum.matura.domain.task.Task;
import pl.lodz.p.liceum.matura.external.worker.task.events.*;


@Log
@AllArgsConstructor
@Service
public class KafkaConsumer {

    private final SubtaskEventMapper subtaskEventMapper;
    private final TaskEventMapper taskEventMapper;
    private final ResultService resultService;

    @KafkaListener(topics = KafkaConfiguration.TASKS_INBOUND_TOPIC, groupId = KafkaConfiguration.KAFKA_GROUP_ID,
            containerFactory = "taskKafkaListenerFactory")
    public void onReceive(TaskEvent taskEvent) {

        if (taskEvent instanceof TaskProcessingCompleteEvent) {
            Task task = taskEventMapper.toDomain(taskEvent);
            log.info("Processing of task completed successfully at " + task.getWorkspaceUrl());
        } else if (taskEvent instanceof SubtaskFastProcessingCompleteEvent event) {
            Subtask subtask = subtaskEventMapper.toDomain(event);
            log.info(String.format("Fast processing of subtask %s completed successfully with score %d", subtask.getNumber(), 100));
            Result result = new Result(null, subtask.getSubmissionId(), subtask.getNumber(), "", event.getScore());
            resultService.save(result);
        } else if (taskEvent instanceof SubtaskFullProcessingCompleteEvent event) {
            Subtask subtask = subtaskEventMapper.toDomain(event);
            log.info(String.format("Full processing of subtask %s completed successfully with score %d", subtask.getNumber(), 100));
            Result result = new Result(null, subtask.getSubmissionId(), subtask.getNumber(), "", event.getScore());
            resultService.save(result);
        } else if (taskEvent instanceof TaskProcessingFailedEvent) {
            Task task = taskEventMapper.toDomain(taskEvent);
            log.info("Processing of task failed at " + task.getWorkspaceUrl());
        } else if (taskEvent instanceof SubtaskProcessingFailedEvent event) {
            Subtask subtask = subtaskEventMapper.toDomain(event);
            log.info(String.format("Processing of subtask %s failed", subtask.getNumber()));
        } else {
            log.info("Received taskEvent: " + taskEvent);
        }

    }
}
