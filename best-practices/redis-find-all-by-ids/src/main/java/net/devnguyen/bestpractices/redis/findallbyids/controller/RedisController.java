package net.devnguyen.bestpractices.redis.findallbyids.controller;

import lombok.extern.log4j.Log4j2;
import net.devnguyen.bestpractices.redis.findallbyids.repository.HighSpeedRepository;
import net.devnguyen.bestpractices.redis.findallbyids.repository.LowSpeedRepository;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@Log4j2
public class RedisController {
    private final HighSpeedRepository highSpeedRepository;
    private final LowSpeedRepository lowSpeedRepository;


    public RedisController(HighSpeedRepository highSpeedRepository, LowSpeedRepository lowSpeedRepository) {
        this.highSpeedRepository = highSpeedRepository;
        this.lowSpeedRepository = lowSpeedRepository;
    }

    @GetMapping("/low/find-by-ids/{size}")
    public Object findByIdsLow(@PathVariable Integer size){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("findByIdsLow");
        var ids = IntStream.range(1,size + 1).mapToObj(Objects::toString).collect(Collectors.toList());
        var data = lowSpeedRepository.findAllById(ids);
        stopWatch.stop();
        log.info("TotalTimeSeconds {}s",stopWatch.getTotalTimeSeconds());
        log.info(stopWatch.prettyPrint());
        return data;
    }
    @GetMapping("/high/find-by-ids/{size}")
    public Object findByIdsHigh(@PathVariable Integer size){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("findByIdsHigh");
        var ids = IntStream.range(1,size + 1).mapToObj(Objects::toString).collect(Collectors.toList());
        var data = highSpeedRepository.findAllById(ids);
        stopWatch.stop();
        log.info("TotalTimeSeconds {}s",stopWatch.getTotalTimeSeconds());
        log.info(stopWatch.prettyPrint());
        return data;
    }
}
