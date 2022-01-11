package net.devnguyen.bestpractices.redis.findallbyids.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("LowRedisDomain")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowSpeedDomain {
    private String id;
    private String data;
}
