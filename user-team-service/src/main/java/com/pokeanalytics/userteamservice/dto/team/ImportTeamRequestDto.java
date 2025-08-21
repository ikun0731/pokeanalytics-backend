package com.pokeanalytics.userteamservice.dto.team;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImportTeamRequestDto {
    @NotBlank(message = "导入的文本内容不能为空")
    private String pasteText;

    // 让用户在导入时可以顺便命名，是很好的体验
    private String teamName;
    private String description;
    private String format;
}