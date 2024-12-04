package yorick.poc.gateway.config;

import io.micrometer.common.KeyValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;
import org.springframework.http.server.reactive.observation.ServerRequestObservationConvention;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.netty.http.server.HttpServer;
import yorick.poc.gateway.util.UriPatternUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Configuration
@Slf4j
public class NettyMetricsConfig {

    @Autowired
    TestConfig.BeanCollection beanCollection;

    private String[] uriTemplates = {
            "/chat/**",
            "/news/**",
            "/patron/**"
    };

    @Bean
    public NettyServerCustomizer nettyServerCustomizer(){
        return httpServer -> httpServer.metrics(true, url -> url);
    }

    @Bean
    HttpClientCustomizer httpClient() {
        TestConfig.BeanInterface[] beans = beanCollection.getBeans();
        for(TestConfig.BeanInterface bean : beans){
            log.info("Autowired beans {}",System.identityHashCode(bean));
        }
        return httpClient-> httpClient.wiretap(true).metrics(true, Function.identity());
    }

    /**
     * for reactor netty metrics: reactor_netty_http_server_
     * @return
     */
    @Bean
    NettyServerCustomizer serverMetrics(){
        return new NettyServerCustomizer(){
            @Override
            public HttpServer apply(HttpServer httpServer) {
                return httpServer.metrics(true, UriPatternUtil.createUriTagFunction(uriTemplates));
            }
        };
    }


    /**
     * for spring webflux metrics: http_server_request
     * @return
     */
    @Bean
    ServerRequestObservationConvention webFluxTagsProvider(){
        PathPatternParser parser = new PathPatternParser();
        List<PathPattern> patterns = Arrays.stream(uriTemplates).map(parser::parse)
                .toList();
        return new DefaultServerRequestObservationConvention(){
            @Override
            protected KeyValue uri(ServerRequestObservationContext context) {

                String path = context.getCarrier().getURI().getPath();

                // Match the request URI against predefined patterns
                for (PathPattern pattern : patterns) {
                    if (pattern.matches(PathContainer.parsePath(path))) {
                        return KeyValue.of("uri", pattern.getPatternString());  // Return the matched pattern as the tag value
                    }
                }

                return super.uri(context);
            }

        };

    }
}
