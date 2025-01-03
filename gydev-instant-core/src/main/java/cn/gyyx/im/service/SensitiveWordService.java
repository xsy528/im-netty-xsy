package cn.gyyx.im.service;

import cn.gyyx.im.dao.SensitiveWordsDao;
import com.github.houbb.sensitive.word.api.IWordDeny;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zxw
 * @Description:
 * @create 2024/4/1/000114:32
 * @Version 1.0
 **/
@Service
public class SensitiveWordService implements IWordDeny {

    private final SensitiveWordsDao sensitiveWordsDao;

    public SensitiveWordService(SensitiveWordsDao sensitiveWordsDao) {
        this.sensitiveWordsDao = sensitiveWordsDao;
    }

    @Override
    public List<String> deny() {
        List<String> sensitiveWord = sensitiveWordsDao.getSensitiveWord();
        return sensitiveWord;
    }
}
