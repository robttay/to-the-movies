package com.example.movie.controllers;

import com.example.movie.Movie;
import com.example.movie.ResultsPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller                             //be2a38521a7859c95e2d73c48786e4bb
public class MovieController {
    private static final String API_TOKEN = "be2a38521a7859c95e2d73c48786e4bb";
    private static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + API_TOKEN;
    private static RestTemplate restTemplate = new RestTemplate();
    private static Random random = new Random();

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    @RequestMapping(path = "/now-playing", method = RequestMethod.GET)
    public String nowPlaying(Model model) {
        model.addAttribute("movies", getMovies(NOW_PLAYING_URL));
        return "now-playing";
    }

    @RequestMapping(path = "/medium-popular-long-name", method = RequestMethod.GET)
    public String mediumPopularLongName(Model model) {
        model.addAttribute("movies", getMovies(NOW_PLAYING_URL)
                .stream()
                .filter(movie -> movie.getTitle().length() > 9)
                .filter(movie -> movie.getPopularity() > 29 && movie.getPopularity() < 81)
                .collect(Collectors.toList()));
        return "medium-popular-long-name";
    }

    @RequestMapping(path = "/overview-mashup", method = RequestMethod.GET)
    public String overviewMashup(Model model) {
        String mashupString = "";
        List<String> randomSentences = new ArrayList<>();
        getMovies(NOW_PLAYING_URL).stream()
                .limit(5)
                .forEach((movie -> {
                    randomSentences.add(movie.getOverview()
                            .split("\\.")[random.nextInt(
                            movie.getOverview().split("\\.").length)]);
                }));
        Collections.sort(randomSentences, (e1, e2) -> random.nextInt(3) - 1);
        for (String sentence : randomSentences) {
            mashupString += sentence + ". ";
        }
        model.addAttribute("mashupString", mashupString);
        return "overview-mashup";
    }

    public static List<Movie> getMovies(String route) {
        ResultsPage resultsPage = restTemplate.getForObject(route, ResultsPage.class);
        return resultsPage.getResults();
    }
}
