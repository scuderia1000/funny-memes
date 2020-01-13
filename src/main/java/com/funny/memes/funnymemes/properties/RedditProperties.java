package com.funny.memes.funnymemes.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("reddit")
public class RedditProperties {

    private List<String> redditGroups;

    public List<String> getRedditGroups() {
        return redditGroups;
    }

    public void setRedditGroups(List<String> redditGroups) {
        this.redditGroups = redditGroups;
    }
}
