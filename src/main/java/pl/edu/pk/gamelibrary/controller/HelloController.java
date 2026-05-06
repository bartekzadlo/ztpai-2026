package pl.edu.pk.gamelibrary.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello World!";
    }

    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name){
    return "Hello, " + name + "!";}

    @GetMapping("/greet")
    public String greet(@RequestParam String name){
        return "Hello, " + name + "!";}
}

