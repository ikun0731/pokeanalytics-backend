package com.pokeanalytics.userteamservice.dto.team;

import com.pokeanalytics.userteamservice.entity.TeamMember;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // 继承父类的 equals 和 hashCode 方法
public class TeamMemberDetailDto extends TeamMember {
    // 【新增】用于存放从 pokedex-data-service 获取的图片地址
    private String spritePixel;
}