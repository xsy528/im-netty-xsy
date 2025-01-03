package cn.gyyx.im.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ImConfig {

    @Value(("${login.out-time}"))
    private Integer outTime;

    @Value("${login.key}")
    private String key;

    /**
     * pod名，为多pod做准备
     */
    @Value("${POD_NAME:}")
    private String podName;

    public String getPodName() {
        return StringUtils.isEmpty(podName)?"default":podName;
    }
}
