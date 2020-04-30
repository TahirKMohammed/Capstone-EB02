/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capstoneeb02;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kingtahir
 */
public class FrequencyBasedModel {
    
    public int numOfTopics = 200;
    int counter = 0;
    //String dataDir = "/Users/kingtahir/Documents/TestCorpus";
    String dataDir = "/Users/kingtahir/Downloads/clueweb09PoolFilesTest";
    ArrayList<String> list = new ArrayList<String>(); 
    ArrayList<String> fileNameList = new ArrayList<String>();
    ArrayList<String> textList = new ArrayList<String>();
    ArrayList<String> noSTPtextList = new ArrayList<String>();
    HashMap<String,String> fileContentsMap = new HashMap<String, String>();
    HashMap<String,Double> bigCompleteMap = new HashMap<>();
    //HashMap<String,Double> freqMap = new HashMap<>();
    
    public FrequencyBasedModel() throws Exception{
        File folder = new File(dataDir);
        File[] listOfFiles = folder.listFiles();
        for(File file : listOfFiles) {
            if (file.isFile()) {
                String tmpFilePath = dataDir + "/" + file.getName();
                counter++;
                //System.out.println("Path# " + counter + ": " + tmpFilePath);
                // Adding the file contents in a format of html which I take care of later
                list.add(readFile(tmpFilePath));
                // Adding file names into an ArrayList
                fileNameList.add(file.getName());
            }
        }
        //System.out.println(list.get(0));
        
        for(String str : list){
            // Converting file contents from html to String
            Document document = Jsoup.parse(str, "ASCII");
            // Adding file contents in String format 
            textList.add(document.text());
        }
        int fileNO = 0;
        for(String str : textList){
            // Removing stop words from String file contents
            noSTPtextList.add(removeStopWords(str));
            fileContentsMap.put(fileNameList.get(fileNO), removeStopWords(str));
            fileNO++;
        }
        // Generating a Complete Big Graph entries (nodes and edges)
        // then storing it in a text file
//        bigCompleteMap = buildMap(fileContentsMap);
//        bigCompleteMap = sortMap(bigCompleteMap);
//        writeToText(this.bigCompleteMap);
        
        // reconstructing the Complete Big Graph entries from the text file
        readFromText("/Users/kingtahir/Documents/BigCompleteGraph.txt");
    }
    
    private String readFile(String filePath) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filePath));
    String str = "";
    try {
        String line = br.readLine();
        
        while (line != null) {
           // queryNumTopic(line);
           if(line.matches("\\A\\p{ASCII}*\\z")){
               str = str + line;
           }
            line = br.readLine();
        }
    } finally {
        br.close();
    }
    return str;
    //return str.replaceAll("\\s+", "");
  }
    
    private String removeStopWords(String str) throws IOException{
        //String stopWords = readFile("/Users/kingtahir/NetBeansProjects/CapstoneEB02/stopwords.txt");
        String stopwords = "!! ?! ?? !? ` `` '' -lrb- -rrb- -lsb- -rsb- , . : ; \" ' ? < > { } [ ] + - ( ) & % $ @ ! ^ # * .. ... 'll 's 'm a about above after again against all am an and any are aren't as at be because been before being below between both but by can can't cannot could couldn't did didn't do does doesn't doing don't down during each few for from further had hadn't has hasn't have haven't having he he'd he'll he's her here here's hers herself him himself his how how's i i'd i'll i'm i've if in into is isn't it it's its itself let's me more most mustn't my myself no nor not of off on once only or other ought our ours ourselves out over own same shan't she she'd she'll she's should shouldn't so some such than that that's the their theirs them themselves then there there's these they they'd they'll they're they've this those through to too under until up very was wasn't we we'd we'll we're we've were weren't what what's when when's where where's which while who who's whom why why's with won't would wouldn't you you'd you'll you're you've your yours yourself yourselves ### return arent cant couldnt didnt doesnt dont hadnt hasnt havent hes heres hows im isnt its lets mustnt shant shes shouldnt thats theres theyll theyre theyve wasnt were werent whats whens wheres whos whys wont wouldnt youd youll youre youve";
        String[] allWords = str.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for(String word : allWords) {
            if(!stopwords.contains(word)) {
                builder.append(word);
                builder.append(' ');
            } 
        }
        String result = builder.toString().trim();
        return result;
    }
    
    public HashMap<String, Double> buildMap(HashMap<String, String> GraphMap){
        HashMap<String, Double> freqMap = new HashMap<>();
        // Adding all the file contents to a temporary array list for reducing the time complexity
        ArrayList<String> tmpFileContents = new ArrayList<>();
        ArrayList<String> tmpFileNames = new ArrayList<>();
        int docSize = 0;
        for(String fileName : GraphMap.keySet()){
            tmpFileContents.add(GraphMap.get(fileName));
            tmpFileNames.add(fileName);
            docSize++;
        }
        System.out.println(""+docSize);
        // Represents the file index to avoid comparison between same files
        int index = 0;
        
        for(String fileName : GraphMap.keySet()){
            String fileContents = GraphMap.get(fileName);
            String[] wordsArray = fileContents.split(" ");
            int numOfWords_f1 = wordsArray.length;
            
            for(String fn : tmpFileNames){
                if(fileName.equals(fn)){
                        continue;
                    }
                String doc2bCompared = GraphMap.get(fn);
                int numOfWords_f2 = doc2bCompared.split(" ").length;
                double sharedNW = 0;
                
                for(String word : wordsArray){
                    if(doc2bCompared.contains(word)){
                            sharedNW++;
                        }  
                    }
                
                if(numOfWords_f2 > numOfWords_f1){
                    sharedNW = sharedNW / numOfWords_f2;
                }
                else{
                    sharedNW = sharedNW / numOfWords_f1;
                }
                
                String key = fn + " " + fileName;
                if(freqMap.containsKey(key)){
                    if((1/sharedNW) < freqMap.get(key)){
                        freqMap.replace(key, 1/sharedNW);
                    }
                }
                else{
                    freqMap.put((fileName + " " + fn), 1/sharedNW);
                }
            }
            index++; 
        }
        return freqMap;
    } 
    
    public ArrayList<String> getNoSTPtextList(){
        return this.noSTPtextList;
    }
    
    public HashMap<String, Double> sortMap(HashMap<String, Double> unsortedVMap){
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortedVMap.entrySet());
        
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>(){
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2){
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        
        // Putting data from sorted list to hashmap
        HashMap<String, Double> sortedVMap = new LinkedHashMap<String, Double>();
        for(Map.Entry<String, Double> aa : list){
            sortedVMap.put(aa.getKey(), aa.getValue());
        }
        
        return sortedVMap;
    }
    
    public void writeToText(HashMap<String,Double> bigCompleteMap) throws IOException{
        FileWriter resultFile = new FileWriter("/Users/kingtahir/Documents/BigCompleteGraph.txt");
        
        for(String nodes : bigCompleteMap.keySet()){
            resultFile.write(nodes + "||" + bigCompleteMap.get(nodes) + "\n");
        }
        resultFile.close();
        System.out.println("Successfully wrote to the file.");
    }
    
    public void readFromText(String path) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(path));

        String line = br.readLine();

        while (line != null) {
            String[] entry = line.split("\\|\\|");
            this.bigCompleteMap.put(entry[0], Double.parseDouble(entry[1]));
            line = br.readLine();
        }
        br.close();
    }
    
    public static void main(String[] args) throws Exception {
      
        int counter = 1;
        FrequencyBasedModel fbm = new FrequencyBasedModel();
        for(String key : fbm.bigCompleteMap.keySet()){
            double value = fbm.bigCompleteMap.get(key);
            System.out.println("" + counter + ": " + key + " frequency = " + value);
            counter++;
        }
    }
  
}
