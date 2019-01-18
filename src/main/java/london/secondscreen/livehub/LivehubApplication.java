package london.secondscreen.livehub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
public class LivehubApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivehubApplication.class, args);
    }

    @Bean
    public Scheduler jdbcScheduler(@Value("${spring.datasource.pool-size}") int connectionPoolSize) {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
    }

}
