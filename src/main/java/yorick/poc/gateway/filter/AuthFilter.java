package yorick.poc.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import yorick.poc.gateway.service.Constant;

@Component
@Slf4j
public class AuthFilter implements WebFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String usrId = request.getHeaders().getFirst("usrId");
        if(usrId == null){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        exchange.getAttributes().put(Constant.USRID, usrId);
        return chain.filter(exchange).contextWrite(ctx -> ctx.put(ServerWebExchange.class, exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
