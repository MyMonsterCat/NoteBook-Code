package monster;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.Controller;

import java.io.IOException;

/**
 * 第二讲：BeanFactory 和 ApplicationContext 类的重要实现类
 * 2.ApplicationContext的实现类
 */

public class part2_2 {

    @Test
    // ⬇️1.最为经典的容器，基于classpath 下 xml 格式的配置文件来创建
    public void testClassPathXmlApplicationContext_b01() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("part2_2-bean.xml");
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    @Test
    // ⬇️2.基于磁盘路径下 xml 格式的配置文件来创建
    public void testFileSystemXmlApplicationContext_b01() {
        // 可以用绝对路径或者相对路径
        // FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("D:\\ideacode\\spring-source-study\\spring_02_02_applicationcontext_impl\\src\\main\\resources\\spring_bean.xml");
        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("src\\main\\resources\\part2_2-bean.xml");
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    @Test
    // ⬇️模拟一下ClassPathXmlApplicationContext和FileSystemXmlApplicationContext底层的一些操作
    public void testMockClassPathAndFileSystemXmlApplicationContext() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        System.out.println("读取之前");
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println("读取之后");
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // reader.loadBeanDefinitions("part2_2-bean.xml");
        // reader.loadBeanDefinitions(new ClassPathResource("part2_2-bean.xml"));
        reader.loadBeanDefinitions(new FileSystemResource("src\\main\\resources\\part2_2-bean.xml"));
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }


    @Test
    // ⬇️3.较为经典的容器，基于java配置类来创建
    public void testAnnotationConfigApplicationContext() {
        // 会自动加上5个后处理器
        // org.springframework.context.annotation.internalConfigurationAnnotationProcessor
        // org.springframework.context.annotation.internalAutowiredAnnotationProcessor
        // org.springframework.context.annotation.internalCommonAnnotationProcessor
        // org.springframework.context.event.internalEventListenerProcessor
        // org.springframework.context.event.internalEventListenerFactory
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    @Test
    // ⬇️4.较为经典的容器，基于java配置类来创建，并且还可以用于web环境
    // 模拟了 springboot web项目内嵌Tomcat的工作原理
    public void testAnnotationConfigServletWebServerApplicationContext() throws IOException {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        // 防止程序终止
        System.in.read();
    }

    @Configuration
    static class WebConfig {
        @Bean
        // 1. WebServer工厂
        // ServletWebServerFactory是WebServerFactory的工厂，TomcatServletWebServerFactory是其中一个实现
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        // 2. web项目必备的DispatcherServlet
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean
        // 3. 将DispatcherServlet注册到WebServer上
        // "/"代表所有请求都先经过这个dispatcherServlet，由他进行请求分发
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet) {
            return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }

        // 4.控制器,有一个约定：如果bean是以斜杠开头，把斜杠后面的名称作为访问路径
        @Bean("/hello")
        public Controller controller1() {
            return (request, response) -> {
                response.getWriter().println("hello");
                return null;
            };
        }
    }


    // 这里的配置是：testAnnotationConfigApplicationContext()
    // 单元测试的过程中如果要解析一些Spring注解，比如@Configuration的时候不要把相关类定义到写单元测试类的内部类，会读取不到
    // 加上static后就可以
    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2(Bean1 bean1) {
            Bean2 bean2 = new Bean2();
            bean2.setBean1(bean1);
            return bean2;
        }
    }


    static class Bean1 {

    }

    static class Bean2 {
        private Bean1 bean1;

        public Bean1 getBean1() {
            return bean1;
        }

        public void setBean1(Bean1 bean1) {
            this.bean1 = bean1;
        }
    }

}
