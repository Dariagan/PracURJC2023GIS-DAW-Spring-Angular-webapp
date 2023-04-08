package es.codeurjc.backend.utilities.queriers;

import java.util.Optional;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.service.TweetService;

public class TweetQuerier extends IndividualQueriable<Tweet, TweetService>
{
    public TweetQuerier(TweetService service)
    {
        super(service);
    }

    @Override
    public Optional<Tweet> doQuery(String subjectId) 
    {
        return getService().getTweetBy(subjectId);
    }
}
