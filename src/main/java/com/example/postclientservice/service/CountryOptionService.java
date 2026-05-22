package com.example.postclientservice.service;

import com.example.postclientservice.dto.Dto.CountryOption;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class CountryOptionService {
    public List<CountryOption> getCountries() {
        Locale displayLocale = Locale.forLanguageTag("ru");

        return Arrays.stream(Locale.getISOCountries())
                .map(code -> new CountryOption(
                        code,
                        new Locale("", code).getDisplayCountry(displayLocale)
                ))
                .sorted(Comparator.comparing(CountryOption::name))
                .toList();
    }




}
