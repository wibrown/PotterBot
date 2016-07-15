package ir;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Contains functionality to get quotes from a certain GoodReads page using JSoup
 * and regular expressions.
 */
public class QuoteGetter {
      
    /**
     * Takes in a map from a GoodReads book URL to the number of pages of quotes for that book. 
     * For example, Order of the Phoenix has 17 pages so its entry in the map would be 
     * ("https://www.goodreads.com/work/quotes/2809203-harry-potter-and-the-order-of-the-phoenix?page=", 17)
     * Filters out anything less than 100 likes.
     * 
     * @param m The map from URL to number of pages
     * @return A collection of quotes from the pages
     */
    public static Collection<String> getQuotes(Map<String, Integer> m){
        HttpURLConnection httpConnection;
        
        Set<String> quotes = new HashSet<String>();
        for (String s : m.keySet()){
            for (int i = 1; i <= m.get(s); i++){
                String u = s + i;
                try {
                    URL url = new URL(u);

                    URLConnection connection = url.openConnection();
                    httpConnection = (HttpURLConnection) connection;
                    
                    ArrayList<String> contents = new ArrayList<String>();
                    Scanner in;

                    in = new Scanner(httpConnection.getInputStream());

                    while (in.hasNextLine()) {
                        String line = in.nextLine();
                        contents.add(line);
                    }
                    
                    StringBuilder page = new StringBuilder();
                    for (String c : contents) {
                        page.append(c);
                    }
                    Document doc = Jsoup.parse(page.toString());
                    Elements q = doc.getElementsByClass("quoteText");
                    for (Element e : q){
                        int likes;
                        String likeInfo;
                        try {
                            likeInfo = e.nextSibling().childNode(3).childNode(1).childNode(0).toString();
                            String[] info = likeInfo.split(" ");
                            likes = Integer.parseInt(info[0]);
                        } catch (Exception ex){
                            likes = 0;
                            likeInfo = " ";
                        }
                        if (e.text() != null && likes >= 100){
                            String[] qs = e.text().split(" ― J.K. Rowling");
                            if (qs[0].length() < 111){
                                quotes.add(qs[0]); //+ " -- "+ likeInfo
                            }       
                        }
                        
                    }
                    
                    in.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
        }
        return quotes;
    }
}
