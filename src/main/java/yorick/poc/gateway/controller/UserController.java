package yorick.poc.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.core.scheduler.SchedulersExt;
import reactor.netty.http.server.HttpServerRequest;
import yorick.poc.gateway.service.UserService;


@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/name")
    public Mono<String> getUserName(@RequestParam("name") String name1){

        log.info("received {}", name1);

        return Mono.deferContextual(ctx-> {
            log.info("ServerWebExchange:{}", ctx.hasKey(ServerWebExchange.class));
                return userService.getUserName()
                        .subscribeOn(SchedulersExt.sporty())
                        .doOnNext(x-> log.info("xx {}", x));
        });



    }
}
