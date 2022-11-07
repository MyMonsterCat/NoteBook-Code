package cn.monster;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * 第二讲：BeanFactory 和 ApplicationContext 类的重要实现类
 * 1.beanfactory的实现
 */

// 启动需打开此注释，并停止掉其他类的@SpringBootApplication或其他自定义的dispatcherServlet
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
    public void TestBeanFactory(){
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // bean 的定义（即bean的一些描述信息，包含class：bean是哪个类，scope：单例还是多例，初始化、销毁方法等）
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class).setScope("singleton").getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);

        // 打印BeanFactory中Bean
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }


//    @Configuration
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
