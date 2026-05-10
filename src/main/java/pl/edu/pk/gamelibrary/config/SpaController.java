package pl.edu.pk.gamelibrary.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Przekierowuje wszystkie nieznane ścieżki do index.html,
 * żeby Angular Router mógł obsłużyć nawigację po stronie klienta.
 */
@Controller
public class SpaController {

    @RequestMapping(value = {
        "/",
        "/games",
        "/games/**",
        "/auth/**",
        "/library"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
