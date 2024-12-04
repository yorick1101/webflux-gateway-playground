package yorick.poc.gateway;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws InterruptedException {
        Hooks.enableAutomaticContextPropagation();
        System.setProperty("reactor.netty.ioWorkerCount", "1");
        SpringApplication.run(Application.class, args);
    }
}
