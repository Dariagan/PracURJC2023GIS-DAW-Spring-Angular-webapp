package es.codeurjc.backend.controller;

import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.google.gson.Gson;
import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.repository.TweetRepository;

// Group 13-A

@Controller
public class ChartController 
{
    @Autowired 
    private TweetRepository tweetRepository;
    
    @RequestMapping("/barchart")
    public String showChart(Model model, HttpServletRequest request)
    {
        List<Tweet> topTweets = tweetRepository.findMostLikedTweets(PageRequest.of(0, 5));
        List<String> displayedTexts = new ArrayList<>();
        List<Integer> displayedNumbers = new ArrayList<>();

        topTweets.forEach(tweet -> updateTweetDisplays(tweet, displayedTexts, displayedNumbers));

        Gson gson = new Gson();
        String leftSideNumbersJsonList, bottomTextsJsonList, chartDescription, yAxisLabel;

        leftSideNumbersJsonList = gson.toJson(displayedNumbers);
        bottomTextsJsonList = gson.toJson(displayedTexts);
        chartDescription = "Top 5 most liked tweets";
        yAxisLabel = "Likes";

        model.addAttribute("bottomTextsJsonList", bottomTextsJsonList);
        model.addAttribute("leftSideNumbersJsonList", leftSideNumbersJsonList);
        model.addAttribute("chartDescription", chartDescription);
        model.addAttribute("yAxisLabel", yAxisLabel);

        return "chartpage";
    }

    private static void updateTweetDisplays(
        Tweet tweet,
        List<String> displayedTexts,
        List<Integer> displayedNumbers
    ) {
        displayedTexts.add(
            String.format("“%s”\\nby user: %s", tweet.getText(), tweet.getAuthor())
        );
        Try.of(() -> displayedNumbers.add(tweet.getLikes().size()));
    }
}

