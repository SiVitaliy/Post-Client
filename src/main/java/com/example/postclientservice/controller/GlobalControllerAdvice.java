package com.example.postclientservice.controller;
import com.example.postclientservice.dto.Dto.CountryOption;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.service.CountryOptionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalControllerAdvice {
    private final HttpSession session;
    private final CountryOptionService countryOptionService;
    @Value("${post.client.base-url}")
    private String apiBaseUrl;

    public GlobalControllerAdvice(HttpSession session, CountryOptionService countryOptionService) {
        this.session = session;
        this.countryOptionService = countryOptionService;
    }

    @ModelAttribute("currentUser")
    public UserDto addCurrentUser() {
        UserDto user = (UserDto) session.getAttribute("user");
        System.out.println("user from session: " + user);
        return user;
    }
    @ModelAttribute("countries")
    public List<CountryOption> addCountries() {
        return countryOptionService.getCountries();
    }

    @ModelAttribute("countryNames")
    public Map<String, String> addCountryNames() {
        return countryOptionService.getCountries()
                .stream()
                .collect(Collectors.toMap(
                        CountryOption::code,
                        CountryOption::name
                ));
    }



    @ModelAttribute("apiBaseUrl")
    public String apiBaseUrl() {
        return apiBaseUrl;
    }

}
