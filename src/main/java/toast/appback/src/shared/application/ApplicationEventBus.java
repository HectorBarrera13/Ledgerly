package toast.appback.src.shared.application;

public interface ApplicationEventBus {
    void publish(ApplicationEvent event);
}
