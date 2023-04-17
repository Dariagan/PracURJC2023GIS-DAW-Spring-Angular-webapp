package es.codeurjc.nexusapp.utilities.queriers;

import java.util.Optional;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.service.TweetService;

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
