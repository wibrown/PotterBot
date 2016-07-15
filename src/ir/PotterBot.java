package ir;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Represents the TwitterBot. Contains functionality to post quotes to Twitter, as well as process
 * current top Twitter trends and obtain Harry Potter quotes that are most relevant to them.
 */
public class PotterBot {

    Twitter twitter;
    VectorSpaceModel vectors;
    
    /**
     * Initializes the vector space model and Twitter communication.
     */
    PotterBot() {
        initializeTwitter();
        initializeVectorSpace();
    }
    
    /**
     * Gets the top tweets for the bot based on today's top trends from New York City, Chicago,
     * San Fransisco, and Philadelphia, and how similar these trends are to Harry Potter
     * quotes. Uses tf-idf in the VectorSpaceModel class to compute this similarity.
     * 
     * @return A collection of the top quotes with their hashtags
     */
    Collection<String> getTrending() {
        List<String> quotes = new LinkedList<>();
        try {
            int[] woeIds = {2471217, 2487956, 2459115, 2379574};
            Set<Trend> trendsList = new HashSet<>();
            for (int id : woeIds) {
                Trends trends = twitter.getPlaceTrends(id);
                for (Trend trend : trends.getTrends()) {
                    if (!trendsList.contains(trend)) {
                        trendsList.add(trend);
                    }
                }
            }
            List<String> unformattedTrends = new ArrayList<>();
            
            for (Trend trend : trendsList) {
                unformattedTrends.add(trend.getName());
            }
            Map<String, String> formattedTrends = parseForHashtags(unformattedTrends);
            List<Document> docs = new LinkedList<>();
            for (String s : formattedTrends.keySet()) {
                Document doc = new Document(formattedTrends.get(s));
                docs.add(doc);
            }
            Map<String, String> result = vectors.getTopResult(docs);
            for (String quote : result.keySet()) {
                String key = null;
                for (String s : formattedTrends.keySet()) {
                    if (formattedTrends.get(s).equals(result.get(quote))) {
                        key = s;
                        break;
                    }
                }
                if (quote.length() > 2) {
                    quotes.add(quote + " " + key);
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return quotes;
    }
    
    /**
     * Posts the given quote to the bot's Twitter page.
     * 
     * @param quote The quote to post
     */
    @SuppressWarnings("unused")
    void postQuote(String quote) {
        StatusUpdate statusUpdate = new StatusUpdate(quote);
        try {
            Status status = twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes Twitter and sets all authentications.
     */
    void initializeTwitter() {
        //Twitter App's Consumer Key
        String consumerKey = "pw75wpOw9aWrBnKgf6ZUHqzUS";

        //Twitter App's Consumer Secret
        String consumerSecret = "1uQxvmIbCDIMDffaZRNJJMU9x5xP6X6jNhHMTKpvqsRhD2Io4u";

        //Twitter Access Token
        String accessToken = "726610701654052864-YJdUn2CRQgLsv3FiIP8UhmhPfJXItrn";

        //Twitter Access Token Secret
        String accessTokenSecret = "RW3IU9JhX5GZkBOkG3OsGT4qpk44zhSYdiGtdIigaxDUC";
        
        TwitterFactory factory = new TwitterFactory();
        this.twitter = factory.getInstance();
        
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
    }
    
    /**
     * Initializes the vector space model with Harry Potter quotes.
     */
    void initializeVectorSpace() {
        Map<String, Integer> m = new HashMap<>();
        List<Document> documents = new ArrayList<>();
        Corpus corpus;
        
        m.put("https://www.goodreads.com/work/quotes/4640799-harry-potter-and-the-philosopher-s-stone?page=", 5);
        m.put("https://www.goodreads.com/work/quotes/6231171-harry-potter-and-the-chamber-of-secrets?page=", 5);
        m.put("https://www.goodreads.com/work/quotes/2402163-harry-potter-and-the-prisoner-of-azkaban-harry-potter-3?page=", 5);
        m.put("https://www.goodreads.com/work/quotes/3046572-harry-potter-and-the-goblet-of-fire?page=", 5);
        m.put("https://www.goodreads.com/work/quotes/2809203-harry-potter-and-the-order-of-the-phoenix?page=", 5);
        m.put("https://www.goodreads.com/work/quotes/41335427-harry-potter-and-the-half-blood-prince?page=", 5);
        m.put("https://www.goodreads.com/work/quotes/2963218-harry-potter-and-the-deathly-hallows?page=", 5);
        
        Collection<String> quotes = QuoteGetter.getQuotes(m);
        for (String quote : quotes) {
            Document doc = new Document(quote);
            documents.add(doc);
        }
        corpus = new Corpus(documents);
        vectors = new VectorSpaceModel(corpus);
    }
    
    /**
     * Takes in a collection of trends, whose entries may look like #ThisIsAHashtag, and parses them
     * strings like "this is a hashtag". Stores a map from unformatted hashtag to formatted hashtag.
     * Note that hashtags should be left unchanged while formatted trends should be lowercase.
     * 
     * @param trends Collection of unformatted trends
     * @return map of formatted trends
     */
    Map<String, String> parseForHashtags(Collection<String> trends) {
        Map<String, String> m = new HashMap<String, String>();
        for (String s : trends){
            if (s.charAt(0) == '#'){
                String term = s.substring(1);
                String[] words = term.split("(?=[A-Z])");
                for (int i = 0; i < words.length; i++){
                    words[i] = words[i].toLowerCase();
                }
                String form = "";
                for (int i = 0; i < words.length - 1; i++){
                    form += (words[i] + " ");
                }
                form += (words[words.length - 1]);
                m.put(s, form);
            }
        }
        return m;
    }
}
