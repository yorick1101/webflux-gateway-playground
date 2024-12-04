package yorick.poc.gateway.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TestConfig {

    @Bean
    public BeanA createBeanA(){
        BeanA bean =  new BeanA("A1");
        log.info("BeanA1 {}", System.identityHashCode(bean));
        return bean;
    }

    @Bean
    public BeanB createBeanB(){
        BeanB bean = new BeanB("B");
        log.info("BeanB {}", System.identityHashCode(bean));
        return bean;
    }


    private BeanInterface[] getBeanInterface(){
        return new BeanInterface[]{createBeanA(), createBeanB()};
    }

    @Bean
    public BeanCollection createBeanCollection(){
        return new BeanCollection(getBeanInterface());
    }

    public interface BeanInterface{
        String getName();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BeanA implements BeanInterface{
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BeanB implements BeanInterface{
        private String name;
    }

    public static class BeanCollection {
        private BeanInterface[] beans;

        public BeanCollection(BeanInterface... beans){
            this.beans = beans;
        }

        public BeanInterface[] getBeans(){
            return beans;
        }
    }

    @RequiredArgsConstructor
    @Data
    public static class BeanAWrapper{
        private final BeanA beanA;
    }
}
