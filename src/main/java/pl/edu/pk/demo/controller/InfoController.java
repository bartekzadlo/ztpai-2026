package pl.edu.pk.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pk.demo.model.Info;

@RestController
public class InfoController {
    @GetMapping("/info")
    public Info info() {
        return new Info(
                "Bartłomiej",
                "Spring Boot",
                "1.0"
        );
    }
}
