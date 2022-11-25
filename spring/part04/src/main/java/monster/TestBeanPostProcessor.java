package monster;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;

@Slf4j
public class TestBeanPostProcessor {
    @Test
    public void testBeanPostProcessor() throws Exception {
        // ⬇️GenericApplicationContext 是一个【干净】的容器，默认不会添加任何后处理器，方便做测试
        // 这里用DefaultListableBeanFactory也可以完成测试，只是会比使用GenericApplicationContext麻烦一些
        GenericApplicationContext context = new GenericApplicationContext();

        // 用原始方法注册三个Bean
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        context.registerBean("bean4", Bean4.class);

        // 设置解析 @Value 注解的解析器
        context.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 添加解析 @Autowired 和 @Value 注解的后处理器
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        // 添加解析 @Resource、@PostConstruct、@PreDestroy 注解的后处理器
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        // 添加解析 @ConfigurationProperties注解的后处理器
        // ConfigurationPropertiesBindingPostProcessor后处理器不能像上面几种后处理器那样用context直接注册上去
        // context.registerBean(ConfigurationPropertiesBindingPostProcessor.class);
        // 需要反着来注册一下
        ConfigurationPropertiesBindingPostProcessor.register(context.getDefaultListableBeanFactory());


        // ⬇️初始化容器
        context.refresh();
        System.out.println(context.getBean(Bean4.class));

        // ⬇️销毁容器
        context.close();
    }
}
