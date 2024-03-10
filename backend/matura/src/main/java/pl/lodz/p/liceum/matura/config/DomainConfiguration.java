package pl.lodz.p.liceum.matura.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.lodz.p.liceum.matura.external.workspace.WorkspaceService;
import pl.lodz.p.liceum.matura.domain.task.TaskExecutor;
import pl.lodz.p.liceum.matura.domain.task.TaskRepository;
import pl.lodz.p.liceum.matura.domain.task.TaskService;
import pl.lodz.p.liceum.matura.domain.template.TemplateRepository;
import pl.lodz.p.liceum.matura.domain.template.TemplateService;
import pl.lodz.p.liceum.matura.domain.user.EncodingService;
import pl.lodz.p.liceum.matura.domain.user.UserRepository;
import pl.lodz.p.liceum.matura.domain.user.UserService;
import pl.lodz.p.liceum.matura.domain.workspace.Workspace;
import pl.lodz.p.liceum.matura.external.storage.task.JpaTaskRepository;
import pl.lodz.p.liceum.matura.external.storage.task.TaskEntityMapper;
import pl.lodz.p.liceum.matura.external.storage.task.TaskStorageAdapter;
import pl.lodz.p.liceum.matura.external.storage.template.JpaTemplateRepository;
import pl.lodz.p.liceum.matura.external.storage.template.TemplateEntityMapper;
import pl.lodz.p.liceum.matura.external.storage.template.TemplateStorageAdapter;
import pl.lodz.p.liceum.matura.external.worker.TaskWorkerAdapter;
import pl.lodz.p.liceum.matura.external.worker.kafka.KafkaTaskEvent;
import pl.lodz.p.liceum.matura.external.worker.task.events.SubtaskEventMapper;
import pl.lodz.p.liceum.matura.external.storage.user.JpaUserRepository;
import pl.lodz.p.liceum.matura.external.storage.user.UserEntityMapper;
import pl.lodz.p.liceum.matura.external.storage.user.UserStorageAdapter;

import java.time.Clock;

@Configuration
@ConfigurationProperties("domain.properties")
public class DomainConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public UserRepository userRepository(JpaUserRepository jpaUserRepository, UserEntityMapper mapper) {
        return new UserStorageAdapter(jpaUserRepository, mapper);
    }

    @Bean
    public UserService userService(UserRepository userRepository, EncodingService encoder) {
        return new UserService(userRepository, encoder);
    }

    @Bean
    public TaskExecutor taskExecutor1(KafkaTaskEvent kafkaTaskEvent, SubtaskEventMapper subtaskEventMapper) {
        return new TaskWorkerAdapter(kafkaTaskEvent, subtaskEventMapper);
    }

    @Bean
    public TemplateRepository templateRepository(JpaTemplateRepository jpaTemplateRepository, TemplateEntityMapper mapper) {
        return new TemplateStorageAdapter(jpaTemplateRepository, mapper);
    }

    @Bean
    public TemplateService templateService(TemplateRepository templateRepository) {
        return new TemplateService(templateRepository);
    }

    @Bean
    public TaskRepository taskRepository(JpaTaskRepository jpaTaskRepository, TaskEntityMapper mapper) {
        return new TaskStorageAdapter(jpaTaskRepository, mapper);
    }

    @Bean
    public TaskService taskService(TaskRepository taskRepository, TemplateService templateService, Workspace workspace) {
        return new TaskService(taskRepository);
    }
    @Bean
    public Workspace workspace() {
        return new WorkspaceService("UserWorkspaces");
    }
}
