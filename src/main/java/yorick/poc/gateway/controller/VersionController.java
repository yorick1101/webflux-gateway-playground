package yorick.poc.gateway.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/version")
public class VersionController {

    private final Version defaultVersion = new Version();
    @PostMapping("")
    Mono<Version> getVersion(){
        return Mono.just(defaultVersion);
    }
}
