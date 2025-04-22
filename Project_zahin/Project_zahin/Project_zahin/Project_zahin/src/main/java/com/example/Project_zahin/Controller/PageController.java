package com.example.Project_zahin.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/HomePageHotel")
    public String showHomePage() {
        return "HomePageHotel"; // This maps to HomePageHotel.html
    }


}
