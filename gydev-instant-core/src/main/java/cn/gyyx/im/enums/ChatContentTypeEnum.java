package cn.gyyx.im.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author 邢少亚
 * @date 2024/7/2  16:46
 * @description 聊天文本内容
 */
@Getter
@AllArgsConstructor
public enum ChatContentTypeEnum {
    /**
     * 文本
     */
    TEXT("text"),
    /**
     * 图片
     */
    IMAGE("image"),
    /**
     * 视频
     */
    VIDEO("video"),

    /**
     * 音频
     */
    AUDIO("audio"),

    /**
     * 通用卡片
     */
    COMMON_CARD("common_card"),

    /**
     * 日记文本
     */
    DIARY_TEXT("diary_text"),

    /**
     * 日记图像
     */
    DIARY_IMAGE("diary_image"),

    /**
     * 日记视频
     */
    DIARY_VIDEO("diary_video"),

    /**
     * 日记音频
     */
    DIARY_AUDIO("diary_audio"),

    /**
     * 位置信息
     */
    LOCATION("location"),



    ;

    private final String type;


    public static ChatContentTypeEnum search(String messageType) {
        Optional<ChatContentTypeEnum> findFirst = Arrays.stream(ChatContentTypeEnum.values())
                .filter(p -> p.getType().equals(messageType))
                .findFirst();
        return findFirst.orElse(null);
    }
}
