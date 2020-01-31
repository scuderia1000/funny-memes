package com.funny.memes.funnymemes.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Author: Valentin Ershov
 * Date: 15.01.2020
 */
public class RedditMemeDeserializer extends StdDeserializer<Meme> {

    public RedditMemeDeserializer(Class<?> vc) {
        super(vc);
    }

    public Meme deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        Meme meme = new Meme();

        JsonNode node = parser.getCodec().readTree(parser);
        JsonNode data = node.get("data");
        if (data != null) {
            boolean isSelfPost = data.get("is_self").asBoolean();
            if (isSelfPost) return null;

            String url = data.get("url").toString();
            if (!StringUtils.isEmpty(url) && url.contains("\"")) {
                url = url.replaceAll("\"", "");
            }
            meme.setSourceMediaUrl(url);
            meme.setAuthorName(data.get("author").toString());
            meme.setPublishDate(new Date(data.get("created_utc").asLong() * 1000));
            meme.setScore(data.get("score").intValue());
            meme.setTitle(data.get("title").toString());
        }

        return meme;
    }
}
