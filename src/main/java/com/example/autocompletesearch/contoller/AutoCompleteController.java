package com.example.autocompletesearch.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AutoCompleteController {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @GetMapping("/add_word/{word}")
    String addStringToCache(@PathVariable("word") String word) {
        ListOperations<String,String> list = redisTemplate.opsForList();
        return String.valueOf(list.rightPush("searchedcachekey",word)) +" size : "+ String.valueOf(list.size("searchedcachekey"));
    }

    @GetMapping("/autocomplete/query/{key}")
    List<String> getStringFromCache(@PathVariable("key") String key) {
        List<String> resultStrings = redisTemplate.opsForList().range("searchedcachekey",0,10);

        //Using Java 8
        //List<String> strList = resultStrings.stream().filter(s -> s.startsWith(key)).collect(Collectors.toList());

        List<String> results2 =  new ArrayList<>();
        for(String s : resultStrings) {
            if(s.startsWith(key)) {
                results2.add(s);
            }
        }

        return results2;
    }

}
