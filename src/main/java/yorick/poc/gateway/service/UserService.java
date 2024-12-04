package yorick.poc.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserService {

    public Mono<String> getUserName(){

        return Mono.deferContextual(context-> {
            return Mono.just(context.get(ServerWebExchange.class));
        }).doOnNext(exchange -> log.info("usrId:{}", exchange.getAttributes().get(Constant.USRID)))
                .map(exchange -> exchange.getAttributes().get(Constant.USRID)+"NAME");
    }


}
