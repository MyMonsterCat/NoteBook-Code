package monster;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;
import java.util.Map;
@SpringBootApplication
public class part1 {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(part1.class, args);
        Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        singletonObjects.setAccessible(true);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Map<String, Object> map = (Map<String, Object>) singletonObjects.get(beanFactory);
        map.entrySet().forEach(System.out::println);
//        map.entrySet().stream().filter(entry -> entry.getKey().startsWith("component")).forEach(System.out::println);

    }

}
