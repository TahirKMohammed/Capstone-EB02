/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capstoneeb02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author kingtahir
 */
public class PreProcess {
    
    public int numOfTopics = 200;
    private String relevanceJudgementPath = "/home/kiyoshimohammad/Documents/capstone/Capstone-EB02/result_TF_IDF_full_corpus.txt";
    private HashMap<String,Double>[] ScoreMapArray;
    
    public PreProcess() throws IOException{
        
        this.ScoreMapArray = new HashMap[numOfTopics];
        
        BufferedReader br = new BufferedReader(new FileReader(relevanceJudgementPath));
        try {
            String empty = "empty";
            int prevQueryNo = 0;
            // Theoretical Query Number for double checking inside while loop
            int theoreticalQN = 0;
            int index = -1;
            String line = br.readLine();
            while (line != null) {
                String[] element = line.split(" ");
                int queryNo = Integer.parseInt(element[0]);
                String docName = element[2];
                double score = Double.parseDouble(element[4]);
                if(queryNo != prevQueryNo){
                    index++;
                    theoreticalQN++;
                     
                    if(queryNo != theoreticalQN){
                        for(theoreticalQN = theoreticalQN; theoreticalQN < queryNo; theoreticalQN++){
                            this.ScoreMapArray[theoreticalQN-1] = new HashMap<String,Double>();
                            this.ScoreMapArray[theoreticalQN-1].put(empty, -1.0);
                            // debugging purpose
//                            for(String str : this.ScoreMapArray[theoreticalQN-1].keySet()){
//                                System.out.println("query# " + theoreticalQN + " is " + str + " with score: " + this.ScoreMapArray[theoreticalQN-1].get(str));
//                            } 
                        }
                        index = theoreticalQN - 1;
                    }
                    this.ScoreMapArray[index] = new HashMap<String,Double>();
                    // String = Document name, Double = Original TF-IDF OR SDM score
                }
               // if(fileContentsMap.containsKey(docName)){
                    // each hashmap represents the graph entries (200 docs) for its particular
                    // query number. query number = index + 1
                    this.ScoreMapArray[index].put(docName, score);
               // }

                prevQueryNo = queryNo;
                line = br.readLine();
            }
        } finally {
            br.close();
        }
    }
    
    public HashMap<String,Double>[] getScoreMapArray(){
        return this.ScoreMapArray;
    }
}
