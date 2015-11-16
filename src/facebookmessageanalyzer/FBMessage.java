package facebookmessageanalyzer;

import java.io.Serializable;
import org.jsoup.nodes.Element;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
/**
 * Container for an instance of a Facebook message. FBMessages belong within
 * a FBThread, and contain data regarding an actual message sent from a
 * particular user.
 * 
 * @author Ditran
 * @version 0.1
 */
public class FBMessage implements Serializable {
    
    /**
     * The dateTime this message was sent.
     */
    private LocalDateTime dateTime;
    
    /**
     * The formatted string of the dateTime.
     */
    private String dateTimeString;
    
    /**
     * The message's text
     */
    private String text; 
    
    /**
     * The name of the user that sent this message.
     */
    private String user;
    
    /**
     * Constructor. Elements are passed in and parsed into data for this
     * instance of the FBMessage.
     * 
     * @param metaData the Element containing the message's metadata.
     * @param text the textElement of the message
     */
    public FBMessage(Element metaData, Element text) {
        String rawDateText = metaData.children().get(0).
                getElementsByClass("meta").text();
        this.user = metaData.children().get(0).
                getElementsByClass("user").text();
        this.dateTimeString = capitalizeTimeOfDay(rawDateText);
        this.text = text.text();
        
        try {
            DateTimeFormatter DTF = DateTimeFormatter.
                    ofPattern("EEEE, MMMM d, yyyy 'at' h:mma z");
            this.dateTime = LocalDateTime.parse(this.dateTimeString, DTF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Processes a date string such that its AM / PM marker is capitalized.
     * 
     * Adjusts to DateTimeFormatter's pattern specifications.
     * @param dateString
     * @return the corrected dateString.
     */
    private String capitalizeTimeOfDay(String dateString) {
        String processedString = dateString.replace("am", "AM");
        processedString = processedString.replace("pm", "PM");
        return processedString;
    }
    
    /**
     * Checks to see if a word occurs in this message.
     * 
     * @param word the word to find.
     * @return true if there is at least one instance, false otherwise.
     */
    public boolean findWord(String word) {
        if (this.text.contains(" " + word + " ")) {
            return true;
        }
        return false;
    }
    
    /**
     * Method has not been implemented yet; intended function: returns a list of
     * sentences within this message containing a specified word.
     * 
     * @param word the word to find
     * @return the list of sentences containing the word.
     */
    public List<String> findSentencesWithWord(String word) {
        return null;
    }
    
    /**
     * Gets the dateTime this message has been sent.
     * 
     * @return the dateTime.
     */
    public LocalDateTime getDateTime() {
        return this.dateTime;
    } 
    
    /**
     * Gets the dateTime string of this message.
     * @return the dateTime in string format
     */
    public String getDateTimeString() {
        return this.dateTimeString;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getUser() {
        return this.user;
    }
    
    /**
     * Checks if this dateTime is after another dateTime.
     * 
     * @param dateToCheck the dateTime which the message is possibly after.
     * @return true if this dateTime is after, false otherwise.
     */
    public boolean isAfter(LocalDateTime dateToCheck) {
        return this.dateTime.isAfter(dateToCheck);
    }
    
    /**
     * Checks if this dateTime is before another dateTime.
     * 
     * @param dateToCheck the dateTime which the message is possibly before.
     * @return true if this dateTime is before, false otherwise.
     */
    public boolean isBefore(LocalDateTime dateToCheck) {
        return this.dateTime.isBefore(dateToCheck);
    }
    
    /**
     * Checks if the message is between two times.
     * 
     * @param startDate the initial date
     * @param endDate the ending date
     * @return true if the message is between the dates; false otherwise
     */
    public boolean isBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (this.isAfter(startDate) && this.isBefore(endDate));
    }
    
    /**
     * Counts the numbers of times a word has occurred in this message.
     * 
     * @param word the word to find.
     * @return the number of times the word has shown up.
     */
    public int numberOfOccurences(String word) {
        int count = 0;
        StringTokenizer tokenizer = new StringTokenizer(this.text);
        while (tokenizer.hasMoreTokens()) {
            if (tokenizer.nextToken().equalsIgnoreCase(word)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Calculates and returns the total number of words in this message.
     * 
     * @return the number of words in this message.
     */
    public int numberOfWords() {
        StringTokenizer tokenizer = new StringTokenizer(this.text);
        return tokenizer.countTokens();
    }
    
}
