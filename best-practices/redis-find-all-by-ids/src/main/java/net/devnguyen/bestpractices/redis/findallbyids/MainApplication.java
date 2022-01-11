package net.devnguyen.bestpractices.redis.findallbyids;

import lombok.extern.log4j.Log4j2;
import net.devnguyen.bestpractices.redis.findallbyids.domain.HighSpeedDomain;
import net.devnguyen.bestpractices.redis.findallbyids.domain.LowSpeedDomain;
import net.devnguyen.bestpractices.redis.findallbyids.repository.HighSpeedRepository;
import net.devnguyen.bestpractices.redis.findallbyids.repository.LowSpeedRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@Log4j2
@EnableRedisRepositories
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }

    @Bean
    CommandLineRunner initData(HighSpeedRepository highSpeedRepository, LowSpeedRepository lowSpeedRepository){
        return args -> {

            int size = 5000;
//            size = 1;
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("save LowSpeedDomain");
//            for(var i = 1L; i <= size; ++i){
//                lowSpeedRepository.save(LowSpeedDomain.builder()
//                        .id(i + "")
//                        .data(UUID.randomUUID().toString())
//                        .build());
//            }
//            lowSpeedRepository.saveAll(List.of(LowSpeedDomain.builder()
//                    .id(UUID.randomUUID().toString())
//                    .data(UUID.randomUUID().toString())
//                    .build(),LowSpeedDomain.builder()
//                    .id(UUID.randomUUID().toString())
//                    .data(UUID.randomUUID().toString())
//                    .build()));

            stopWatch.stop();
            stopWatch.start("save HighSpeedDomain");
            for(var i = 1L; i <= size; ++i){
                highSpeedRepository.save(HighSpeedDomain.builder()
                        .id(i + "")
                        .data(UUID.randomUUID().toString())
                        .build());
            }
            stopWatch.stop();
            log.info("TotalTimeSeconds : {}s",stopWatch.getTotalTimeSeconds());
            log.info(stopWatch.prettyPrint());
        };
    }
}
