package facebookmessageanalyzer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Analyzes and generates statistics of a user's Facebook messages, given
 * the HTM file downloaded from a Facebook User's Archive. 
 * <p>
 * Analyzer accepts a HTM file and beings parsing it based on Facebook's
 * HTML code. FBAnalyzer stores the data by using objects called FBThreads, 
 * which contain FBMessages.
 * 
 * @author Di Tran
 * @version 0.1
 */
public class FBAnalyzer implements Serializable {
    
    /**
     * The serial version ID of this analyzer, used for serialization.
     */
    private static final long serialVersionUID = 0x5d0aa1c1054e0892L;
    
    /**
     * The list of threads of this analyzer.
     */
    List<FBThread> threads;
    
    /**
     * The current working thread of this analyzer.
     */
    FBThread thread;
    
    /**
     * The word count of all messages in this analyzer.
     */
    HashMap<String, Integer> wordMap = new HashMap<>();
    
    /**
     * Sole constructor. Takes a Facebook generated HTM file and parses it.
     * @param htmlFile The file that contains message data.
     */
    public FBAnalyzer(File htmlFile) {
        this.threads = new ArrayList<>();
        this.thread = new FBThread();
        
        try {
            Document doc = Jsoup.parse(htmlFile, "UTF-8", "");
            Elements elements = doc.getElementsByClass("thread");
            
            elements.stream().forEach((element) -> {
                threads.add(new FBThread(element));
            });
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    /**
     * Retrieves a thread based on its position in the list.
     * 
     * @param index the index of the desired t
     * @return the FBThread in the list at the specified index.
     */
    public FBThread getThread(int index) {
        return threads.get(index);
    }
    
    /**
     * Deserializes a file and returns the FBAnalyzer object from a file.
     * 
     * @param fileName the name of the file to be loaded.
     * @return the saved FBAnalyzer object.
     */
    public static FBAnalyzer load(String fileName) throws FileNotFoundException {
        String name = fileName;
        
        // sets to default name if none is provided.
        if (fileName == null)
            name = "saveFile.ser";
        
        // checks if the specified file exists.
        File varTmpDir = new File(fileName);
        if (!varTmpDir.exists())
            throw new FileNotFoundException();
        
        // read in the file
        try {
            InputStream file = new FileInputStream(name);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream (buffer);
            return (FBAnalyzer)input.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Counts and returns the most frequently occurring word in all of the 
     * user's Facebook messages.
     * This method breaks down each message using a string tokenizer and stores
     * the count of words in a hashmap. Special characters ,.:;?![] that are 
     * attached to a token are ignored.
     * @return the word used most frequently.
     */
    public String mostCommonWord() {
        StringTokenizer st;
        for (FBThread workingThread : threads) {
            for (FBMessage message : workingThread.getFBMessages()) {
                st = new StringTokenizer(message.getText(), " \t\n\r\f,.:;?![]");
                while (st.hasMoreTokens()) {
                    String word = st.nextToken().toLowerCase();
                    if( !wordMap.containsKey(word) ) {
                        wordMap.put(word, 1);
                    } else {
                        wordMap.put(word, wordMap.get(word) + 1);
                    }
                }
            }
        }
        wordMap = (HashMap<String, Integer>) sortMap(wordMap);
        printMap(wordMap);
        return null;
    }
    
    /**
     * Calculates and returns the number of total messages the user has sent
     * and received.
     * @return the number of messages sent and received.
     */
    public int numberOfMessages() {
        int count = 0;
        for (FBThread thread : threads) {
            count += thread.numberOfMessages();
        }
        return count;
    }
    
    /**
     * Returns the number of messages sent by a particular user.
     * 
     * @param user The user owning the messages
     * @return The number of messages sent by user.
     */
    public int numberOfMessages(String user) {
        int count = 0;
        for (FBThread thread : threads) {
            count += thread.numberOfMessages(user);
        }
        return count;
    }
    
    /**
     * Returns the number of times a word has occurred in all of the user's
     * Facebook messages.
     * 
     * @param word the word to count.
     * @return the number of times a word has occurred.
     */
    public int numberOfOccurences(String word) {
        int count = 0;
        for (FBThread thread : threads) {
            count += thread.numberOfOccurences(word);
        }
        return count;
    }
    
    /**
     * Returns the total number of threads the user is participating in.
     * 
     * @return the total number of threads.
     */
    public int numberOfThreads() {
        return threads.size();
    }
    
    /**
     * Returns the total number of threads where the last person that replied
     * was a specified user.
     * 
     * @param user the user to check.
     * @return the number of threads with the user replying last.
     */
    public int numberOfThreadsWithLastReply(String user) {
        int count = 0;
        for (FBThread t : threads) {
            if (t.getFBMessage(t.numberOfMessages()).getUser().equals(user)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Returns the number of messages sent by a specified user.
     * 
     * @param user the specified user
     * @return the number of messages sent by user.
     */
    public int numberOfMessagesSentByUser(String user) {
        int count = 0;
        for (FBThread thread : threads) {
            count += numberOfMessagesSentByUser(user);
        }
        return count;
    }
    
    /**
     * Prints the map in a list form. The entries are listed in the form "key: 
     * value."
     * 
     * @param map the map to be printed. 
     */
    private void printMap(Map<String, Integer> map) {
        int count = 1;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(count + ": " + entry.getKey() + ": " + entry.getValue());
            count++;
        }
    }
    
    /**
     * Saves the current analyzer into a file in the same directory as the 
     * source. If no filename is specified, the default filename 
     * "saveFile.ser" is chosen. If the provided filename is the same as a file 
     * in the directory, that file will be overwritten. Otherwise, a new file 
     * is created using the given filename.
     * 
     * @param fileName the name of the file to save to.
     */
    public void save(String fileName) {
        String name = fileName;
        if (fileName == null)
            name = "saveFile.ser";
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutput oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the current working thread to one with a specified set of 
 participants.
     * Thread participants must be exact. For example, the set "Alice Smith, 
     * John Smith" differ from "John Smith, Alice Smith."
     * 
     * @param participants the participants in the thread
     */
    public void setThread(String participants) {
        for (FBThread t : threads) {
            if (t.getParticipants().equals(participants)) {
                this.thread = t;
                return;
            }
        }
    }
    
    /**
     * Sorts the map by values instead of keys.
     * 
     * @param unsorted the unsorted map
     * @return the sorted map.
     */
    private static Map<String, Integer> sortMap(Map<String, Integer> unsorted) {
        List<Map.Entry<String, Integer>> list = 
                new LinkedList<>(unsorted.entrySet());
        
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, 
                    Map.Entry<String, Integer> o2) {
                return(o2.getValue()).compareTo(o1.getValue());
            }
        });
        
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    
    /**
     * Main method used for internal testing.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File inputFile = new File("messages.htm");
            
            FBAnalyzer analyzer = FBAnalyzer.load("testSave.ser");
            
            System.out.println(analyzer.numberOfThreads());
            
        } catch(Exception e) {
            System.err.println("Error occurred: " + e);
            e.printStackTrace(System.out);
        }
    }
    
}
