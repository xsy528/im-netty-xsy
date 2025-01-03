package cn.gyyx.im.config;

import cn.gyyx.im.service.SensitiveWordService;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.ignore.SensitiveWordCharIgnores;
import com.github.houbb.sensitive.word.support.resultcondition.WordResultConditions;
import com.github.houbb.sensitive.word.support.tag.WordTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zxw
 * @Description:
 * @create 2024/4/1/000114:18
 * @Version 1.0
 **/
@Configuration
public class SpringSensitiveWordConfig {

    private final SensitiveWordService myDdWordDeny;

    public SpringSensitiveWordConfig(SensitiveWordService myDdWordDeny) {
        this.myDdWordDeny = myDdWordDeny;
    }

    /**
     * 初始化引导类
     * @return 初始化引导类
     * @since 1.0.0
     */
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
                //设置敏感词
                .wordDeny(myDdWordDeny)
                //忽略大小写
                .ignoreCase(true)
                //忽略半角圆角
                .ignoreWidth(true)
                //忽略数字的写法
                .ignoreNumStyle(true)
                //忽略中文的书写格式
                .ignoreChineseStyle(true)
                //忽略英文的书写格式
                .ignoreEnglishStyle(true)
                //忽略重复词
                .ignoreRepeat(false)
                //是否启用数字检测
                .enableNumCheck(false)
                //是有启用邮箱检测
                .enableEmailCheck(false)
                //是否启用链接检测
                .enableUrlCheck(false)
                //是否启用敏感单词检测
                .enableWordCheck(true)
                //词对应的标签
                .wordTag(WordTags.none())
                //忽略的字符
                .charIgnore(SensitiveWordCharIgnores.defaults())
                //针对匹配的敏感词额外加工，比如可以限制英文单词必须全匹配
                .wordResultCondition(WordResultConditions.alwaysTrue())
                .init();
        return sensitiveWordBs;
    }
}

