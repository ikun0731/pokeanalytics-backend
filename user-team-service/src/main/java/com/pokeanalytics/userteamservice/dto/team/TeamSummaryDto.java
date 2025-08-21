package com.pokeanalytics.userteamservice.dto.team;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeamSummaryDto {

    private Long id;
    private String teamName;
    private String description;
    private String format;
    private LocalDateTime updatedAt;

    // 我们未来会在这里添加一个 `List<String> memberSpriteUrls` 字段，
    // 用于在列表页直接展示6个宝可梦的头像，但这属于下一步的“数据聚合”优化。
    private List<String> memberSprites;
}