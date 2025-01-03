FROM gyyx/debian11-jdk11:1.0

COPY target/*.jar /data/program.jar
# 如果使用k8s的configmap来控制配置文件，请放开如下两行
COPY src/main/resources/bootstrap.yml /data/conf/bootstrap.yml
COPY ws-link-gyyx-cn.yml /data/conf/configmap.yml
COPY src/main/resources/application.yml /data/conf/application.yml
EXPOSE 8081

RUN rm -f /etc/localtime \
&& ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

# 基于内部配置文件启动的
#CMD ["java","-Xmx256m","-Xms256m","-javaagent:/data/opt/jmx/jmx_prometheus_javaagent-0.16.1.jar=9001:/data/opt/jmx/jmx_config.yaml","-jar","/data/program.jar","--spring.config.addition-location=/data/conf/application.yml"]
# 基于k8s内CM启动的
CMD ["java","-Xmx256m","-Xms256m","-javaagent:/data/opt/jmx/jmx_prometheus_javaagent-0.16.1.jar=9001:/data/opt/jmx/jmx_config.yaml","-jar","/data/program.jar","--spring.cloud.bootstrap.location=/data/conf/bootstrap.yml"]
