package com.app.caresync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @GetMapping(value = "/providers", produces = "application/json")
    public Mono<String> providerFallback() {
        return redisTemplate.opsForValue().get("providers::all_approved")
                .map(val -> {
                    // Spring Cache with GenericJackson2JsonRedisSerializer wraps collections like:
                    // ["java.util.ArrayList", [{"@class": "...", ...}, {...}]]
                    // We need to unwrap it so the frontend gets a clean array: [{...}, {...}]
                    if (val.startsWith("[\"java.util.ArrayList\",[")) {
                        int firstComma = val.indexOf(",[");
                        if (firstComma != -1) {
                            // Extract everything from the second '[' to the second to last ']'
                            return val.substring(firstComma + 1, val.length() - 1);
                        }
                    }
                    return val;
                })
                .switchIfEmpty(Mono.just("[]"));
    }
}
