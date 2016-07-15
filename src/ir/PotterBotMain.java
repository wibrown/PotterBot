package ir;

import java.util.Collection;

/**
 * Instantiates a PotterBot and executes its methods to post to Twitter.
 */
public class PotterBotMain {

    public static void main(String[] args) {
        PotterBot bot = new PotterBot();
        Collection<String> quotes = bot.getTrending();
        int counter = 0;
        
        /**
         * ONLY UNCOMMENT THE LINE BELOW IF YOU WOULD LIKE THE BOT TO POST TO TWITTER. 
         * DO NOT RUN THE BOT TOO MANY TIMES WHILE THIS LINE IS UNCOMMENTED. TO SEE THE QUOTES,
         * JUST LEAVE THE LINE COMMENTED OUT AND THE QUOTES WILL BE PRINTED TO THE CONSOLE.
         * 
         * The application will exceed Twitter's rate limits if this is run too many
         * times. Please be mindful of this.
         */
        for (String quote : quotes) {
            if (counter < 5) {
                //bot.postQuote(quote);
                counter++;
                System.out.println(quote);
            } else {
                break;
            }
        }
    }
}
