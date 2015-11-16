package facebookmessageanalyzer;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Contains all messages sent between a set of users.
 * 
 * FBThreads are created upon initialization of a FBAnalyzer and store all
 * messages in chronological order. FBThreads contain methods to analyze and 
 * generate statistics of its own messages.
 * 
 * @author Ditran
 * @version 0.1
 */
public class FBThread implements Serializable {
    /**
     * The list of messages in this thread.
     */
    private ArrayList<FBMessage> thread;
    
    /**
     * The set of participants in this thread.
     */
    private String participants;
    
    /**
     * Null constructor.
     */
    public FBThread() {
        this.thread = null;
        this.participants = null;
    }
    
    /**
     * Default constructor. 
     * 
     * @param thread the HTM element containing the thread data. 
     */
    public FBThread(Element thread) {
        this.participants = thread.ownText();
        this.thread = new ArrayList<FBMessage>();
        
        Elements threadData = thread.children();
        assert(threadData.size() > 0 && threadData.size() % 2 == 0);
        for (int i = 1; i <= threadData.size(); i+=2) {
            this.thread.add(new FBMessage(threadData.get(i-1), threadData.get(i)));
        }
    }
    
    /**
     * Calculates and returns the average number of words sent per message.
     * 
     * @return the average number of words per message.
     */
    public double averageWordsPerMessage() {
        return (double) numberOfWords() / (double) thread.size();
    }
    
    /**
     * Calculates and returns the average amount of idle time between messages.
     * Time is calculated in seconds and returned as a Duration object to allow
     * conversion to hours or days. The maximum accuracy is expressed in 
     * minutes.
     * @return the time represented as a Duration object.
     */
    public Duration averageTimeBetweenAllReplies() {
        long totalSeconds = 0;
        for (int i = 1; i < thread.size()-1; i++) {
            totalSeconds += timeBetweenMessages(thread.get(i), 
                    thread.get(i-1)).getSeconds();
        }
        totalSeconds = totalSeconds / (thread.size() - 1);
        return Duration.ofSeconds(totalSeconds);
    }
    
    /**
     * Checks if an instance of a word has occurred in any of the thread's 
     * messages.
     * 
     * @param word the word to find
     * @return true if the word was found; false otherwise.
     */
    public boolean findWord(String word) {
        for (FBMessage message : thread) {
            if (message.findWord(word) == true) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the FBMessage stored at a particular index.
     * 
     * @param i the index of the message
     * @return the message stored at the specified index.
     */
    public FBMessage getFBMessage(int i) {
        return thread.get(i);
    }
    
    /**
     * Returns the list of FBMessages in this thread.
     * 
     * @return the list of FBMessages
     */
    public List<FBMessage> getFBMessages() {
        return thread;
    }
    
    /**
     * Returns the list of FBMessages between two specified indices.
     * 
     * @param start the start index
     * @param end the end index
     * @return the list of threads between the two indices.
     */
    public List<FBMessage> getFBMessages(int start, int end) {
        return thread.subList(start, end);
    }
    
    /**
     * Returns all messages in this thread containing a specified word.
     * 
     * @param word the word to find.
     * @return a list of FBMessages containing that word.
     */
    public List<FBMessage> getFBMessages(String word) {
        ArrayList<FBMessage> list = new ArrayList<>();
        for (FBMessage message : thread) {
            if (message.findWord(word)) {
                list.add(message);
            }
        }
        return list;
    }
    
    /**
     * Returns the participants in this thread.
     * 
     * @return the participants in this thread.
     */
    public String getParticipants() {
        return this.participants;
    }
    
    /**
     * This method has not yet been implemented; intended function: returns the
     * most common word that occurred in this thread.
     * 
     * @return the most common word.
     */
    public String mostCommonWord() {
        return null;
    }
    
    /**
     * Returns the number of times a word has occurred in this thread.
     * 
     * @param word the word to count.
     * @return the number of times a word has occurred.
     */
    public int numberOfOccurences(String word) {
        int count = 0;
        for (FBMessage message : thread) {
            count += message.numberOfOccurences(word);
        }
        return count;
    }
    
    /**
     * Returns the number of total messages within this thread.
     * 
     * @return the number of messages in this thread.
     */
    public int numberOfMessages() {
        return this.thread.size();
    }
    
    /**
     * Returns the number of messages that are between two dates.
     * 
     * @param start The starting date.
     * @param end The end date
     * @return the number of messages in between the start & end date.
     */
    public int numberOfMessages(LocalDateTime start, LocalDateTime end) {
        int count = 0;
        for (FBMessage message : thread) {
            if (message.isBetween(start, end)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Returns the number of messages a particular user has sent.
     * 
     * @param user The user sending the messages
     * @return the number of messages a user has sent.
     */
    public int numberOfMessages(String user) {
        int count = 0;
        for (FBMessage message : thread) {
            if (message.getUser().equals(user)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Calculates and returns the total number of words in this thread.
     * 
     * @return the number of words in this thread.
     */
    public int numberOfWords() {
        int count = 0;
        for (FBMessage message : thread) {
            count += message.numberOfWords();
        }
        return count;
    }
    
    // median time between replies
    // longest reply time
    // shortest reply time
    
    /**
     * Prints all the messages in this thread in reverse chronological order.
     */
    public void printAllMessages() {
        for (FBMessage message : thread) {
            System.out.println(message.toString());
        }
    }
    
    /**
     * Prints all timestamps associated with each message.
     */
    public void printAllTimeStamps() {
        for (FBMessage message : thread) {
            System.out.println(message.getDateTimeString());
        }
    }
    
    /** Calculates the time between two messages.
     * 
     * @param first the Facebook message occurring first.
     * @param last the Facebook message occurring after.
     * @return the duration between these messages
     */
    public Duration timeBetweenMessages(FBMessage first, FBMessage last) {
        return Duration.between(first.getDateTime(), last.getDateTime()).abs();
    }
    
    /**
     * Calculates and returns the total duration of this thread.
     * 
     * @return the total time length of the thread.
     */
    public Duration timeOfThread() {
        return timeBetweenMessages(thread.get(thread.size() - 1),
                                   thread.get(0));
    }
}
