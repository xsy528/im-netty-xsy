package cn.gyyx.im.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 邢少亚
 * @date 2023/1/3  10:09
 * @description 线程池配置
 */
@EnableAsync
@Component
public class ThreadPoolConfig {

    /**
     * 线程池，项目启动时根据队列配置启用不同的线程池配置
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(8);
        //最大线程数
        executor.setMaxPoolSize(32);
        //缓冲队列大小
        executor.setQueueCapacity(16);
        //线程池名前缀
        executor.setThreadNamePrefix("im");
        // 设置线程保持活跃的时间（默认：60）
        executor.setKeepAliveSeconds(60);
        // 当任务完成后，长时间无待处理任务时，销毁线程池
        executor.setWaitForTasksToCompleteOnShutdown(false);
        //线程等待时间
        executor.setAwaitTerminationSeconds(120);
        // 设置任务拒绝策略
        /**
         * 4种
         * ThreadPoolExecutor类有几个内部实现类来处理这类情况：
         - AbortPolicy 丢弃任务，抛RejectedExecutionException
         - CallerRunsPolicy 由该线程调用线程运行。直接调用Runnable的run方法运行。
         - DiscardPolicy  抛弃策略，直接丢弃这个新提交的任务
         - DiscardOldestPolicy 抛弃旧任务策略，从队列中踢出最先进入队列（最后一个执行）的任务
         * 实现RejectedExecutionHandler接口，可自定义处理器
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
