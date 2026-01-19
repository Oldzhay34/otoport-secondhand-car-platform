package com.example.otoportdeneme.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/login"})
    public String login() {
        return "forward:/templates/Login.html";
    }


    @GetMapping({"/Login.html", "/login.html"})
    public String loginAlias() {
        return "forward:/templates/Login.html";
    }

    @GetMapping({"/home", "/home.html"})
    public String home() {
        return "forward:/templates/home.html";
    }

    @GetMapping({"/filter", "/filter.html"})
    public String filter() {
        return "forward:/templates/filter.html";
    }
    @GetMapping({"/profile", "/profile.html"})
    public String profile() {
        return "forward:/templates/profile.html";
    }
    @GetMapping({"/store", "/store.html"})
    public String store() {
        return "forward:/templates/store.html";
    }
    @GetMapping({"/favorites", "/favorites.html"})
    public String favorites() {
        return "forward:/templates/favorites.html";
    }



}


