package monster;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * 第二讲：BeanFactory 和 ApplicationContext 类的重要实现类
 * 1.beanfactory的实现
 */

@SpringBootApplication
public class part2_1 {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(part2_1.class, args);
        // org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // 查看实际类型
        // class org.springframework.beans.factory.support.DefaultListableBeanFactory
        System.out.println(beanFactory.getClass());
    }

    @Test
    public void TestBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // bean 的定义（即bean的一些描述信息，包含class：bean是哪个类，scope：单例还是多例，初始化、销毁方法等）
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class).setScope("singleton").getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);

        // 给 BeanFactory添加一些常用的后处理器，让它具备解析@Configuration、@Bean等注解的能力
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().forEach(beanFactoryPostProcessor -> {
            System.out.println(beanFactoryPostProcessor);
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        });


        // 打印BeanFactory中Bean
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        // 从BeanFactory中取出Bean1，然后再从Bean1中取出它依赖的Bean2
        // 可以看到结果为null，所以@Autowired注解并没有被解析
        Bean1 bean1 = beanFactory.getBean(Bean1.class);
        System.out.println(bean1.getBean2());

        // Bean的后处理器:要想@Autowired、@Resource等注解被解析，还要添加Bean的后处理器，可以针对Bean的生命周期的各个阶段提供扩展
        // 从bean工厂中取出Bean的后处理器，并且执行这些后处理器
        // BeanFactory 后处理器主要功能，补充了一些 bean 的定义
        // beanFactory.getBeansOfType(BeanPostProcessor.class).values().forEach(beanFactory::addBeanPostProcessor);
        // beanFactory.addBeanPostProcessors(beanFactory.getBeansOfType(BeanPostProcessor.class).values());
        // 改变Bean后处理器加入BeanFactory的顺序
        // 写法1：
        // ArrayList<BeanPostProcessor> list = new ArrayList<>(beanFactory.getBeansOfType(BeanPostProcessor.class).values());
        // Collections.reverse(list);
        // beanFactory.addBeanPostProcessors(list);
        // 写法2：
//        beanFactory.addBeanPostProcessors(beanFactory.getBeansOfType(BeanPostProcessor.class).values().stream().sorted(beanFactory.getDependencyComparator()).collect(Collectors.toCollection(ArrayList::new)));
//        // 准备好所有单例，get()前就把对象初始化好
//        beanFactory.preInstantiateSingletons();
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        Bean1 bean1_ = beanFactory.getBean(Bean1.class);
//        System.out.println(bean1_.getBean2());


    }


    //        @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }

        @Bean
        public Bean3 bean3() {
            return new Bean3();
        }

        @Bean
        public Bean4 bean4() {
            return new Bean4();
        }
    }

    @Slf4j
    static class Bean1 {
        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2() {
            return bean2;
        }

        @Autowired
        @Resource(name = "bean4")
        private Inter bean3;

        public Inter getInter() {
            return bean3;
        }

        public Bean1() {
            log.debug("构造 Bean1()");
        }
    }

    @Slf4j
    static class Bean2 {
        public Bean2() {
            log.debug("构造 Bean2()");
        }
    }

    interface Inter {

    }

    @Slf4j
    static class Bean3 implements Inter {
        public Bean3() {
            log.debug("构造 Bean3()");
        }
    }

    @Slf4j
    static class Bean4 implements Inter {
        public Bean4() {
            log.debug("构造 Bean4()");
        }
    }
}
