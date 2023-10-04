package ru.bereshs.HHWorkSearch.model;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VacancyAnalizeService {


    public static  boolean excludeWordsList(String title) {
        List<String> words = new ArrayList<>();
        words.add("senior");
        words.add("ведущий");
        words.add("android");
        words.add("kotlin");
        words.add("scala");
        words.add("lead");
        for(String word: words) {
            if(title.toLowerCase().contains(word.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

}
