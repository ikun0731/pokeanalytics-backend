package com.pokeanalytics.userteamservice.service.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import com.pokeanalytics.userteamservice.dto.request.AiAnalyzeRequestDto;
import com.pokeanalytics.userteamservice.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    private final ZhipuAiClient zhipuAiClient;

    public AiServiceImpl(ZhipuAiClient zhipuAiClient) {
        this.zhipuAiClient = zhipuAiClient;
    }

    /**
     * 获取宝可梦配置分析
     * 
     * @param requestDto 包含宝可梦及其配置信息的请求对象
     * @return AI生成的宝可梦配置分析结果
     */
    @Override
    public String getPokemonAnalysis(AiAnalyzeRequestDto requestDto) {
        try {
            String prompt = buildPrompt(requestDto);

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.builder()
                    .role(ChatMessageRole.SYSTEM.value())
                    .content("你是一位顶尖的宝可梦对战大师和战术分析师，请使用简体中文回答。你的回答应该专业、简洁，并提供有价值的对战建议。")
                    .build());
            messages.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(prompt)
                    .build());

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(Constants.ModelChatGLM4)
                    .messages(messages)
                    .temperature(0.7f)
                    .maxTokens(1024)
                    .build();

            ChatCompletionResponse response = zhipuAiClient.chat().createChatCompletion(params);

            // 检查是否有有效的AI响应
            boolean hasValidResponse = response.isSuccess() && 
                                      response.getData() != null && 
                                      !CollectionUtils.isEmpty(response.getData().getChoices()) && 
                                      response.getData().getChoices().get(0).getMessage() != null && 
                                      response.getData().getChoices().get(0).getMessage().getContent() != null;
            
            if (hasValidResponse) {
                return response.getData().getChoices().get(0).getMessage().getContent().toString();
            } else {
                log.error("Zhipu AI API call failed or returned empty response: {}", response.getMsg());
                return "抱歉，AI分析服务暂时出现问题，未能获取到有效的分析结果。";
            }

        } catch (Exception e) {
            log.error("!!! An unhandled exception was caught in AiServiceImpl !!!");
            e.printStackTrace();
            return "抱歉，调用AI服务时发生内部错误，请查看后端控制台日志。";
        }
    }

    /**
     * 构建AI分析提示语
     * 
     * @param request 宝可梦分析请求对象
     * @return 构建好的AI提示文本
     */
    private String buildPrompt(AiAnalyzeRequestDto request) {
        List<String> validMoves = Optional.ofNullable(request.getMoves())
                .orElse(List.of())
                .stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

        boolean isConfigured = !CollectionUtils.isEmpty(validMoves)
                || StringUtils.hasText(request.getAbility())
                || StringUtils.hasText(request.getItem());

        StringBuilder sb = new StringBuilder();

        sb.append(isConfigured 
            ? "请对以下宝可梦配置进行战术分析，并提供1-2个其他可行的配置思路（包括技能、特性、道具）。请重点分析当前配置的优缺点和战术定位。\n\n宝可梦: " + request.getPokemonName() + "\n" 
            : "请为宝可梦【" + request.getPokemonName() + "】推荐2套在常规对战中常见且强度较高的配置方案。每个方案请清晰地列出推荐的【技能组合】、【特性】和【携带道具】，并用一句话简述该配置的战术核心。");

        // 如果提供了配置信息，则添加详细信息
        if (isConfigured) {
            if (StringUtils.hasText(request.getAbility())) {
                sb.append("特性: ").append(request.getAbility()).append("\n");
            }
            if (StringUtils.hasText(request.getItem())) {
                sb.append("携带道具: ").append(request.getItem()).append("\n");
            }
            if (!CollectionUtils.isEmpty(validMoves)) {
                sb.append("技能: ").append(String.join(", ", validMoves));
            }
        }
        return sb.toString();
    }
}