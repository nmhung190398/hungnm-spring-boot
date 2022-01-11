package net.devnguyen.bestpractices.redis.findallbyids.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighSpeedDomain {
    private String id;
    private String data;
}
