package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public Executor taskExecutor(){
        logger.debug("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("TopKThread-");
        executor.initialize();
        return executor;

    }
}
