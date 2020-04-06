/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capstoneeb02;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author kingtahir
 */
public class FBMmain {

    /**
     * @param args the command line arguments
     */
    
    public ArrayList<String> buildKey(HashMap<String,Double> map){
        ArrayList<String> docNames = new ArrayList<>();
        ArrayList<String> keyList = new ArrayList<>();
        
        for(String docName : map.keySet()){
            docNames.add(docName);
        }
        
        for(int i = 0; i < docNames.size() - 1; i++){
            for(int j = i+1; j < docNames.size(); j++){
                keyList.add(docNames.get(i) + " " + docNames.get(j));
            }
        }
        
        return keyList;
    }
    public static void main(String[] args) throws Exception {
        FrequencyBasedModel fbm = new FrequencyBasedModel();
        PreProcess prep = new PreProcess(fbm.fileContentsMap);
        HashMap<String,Double>[] ScoreMapArray = prep.getScoreMapArray();
        int numOfQ = ScoreMapArray.length;
        System.out.println("Total Number of Queries is: " + numOfQ);
        // for all queries
        FBMmain main = new FBMmain();
        ArrayList<String>[] keysOfQarray = new ArrayList[numOfQ];
        for(int queryN = 0; queryN < numOfQ; queryN++){
            if(ScoreMapArray[queryN].get("null") == -1.0){
                keysOfQarray[queryN] = null;
                continue;
            }

            keysOfQarray[queryN] = main.buildKey(ScoreMapArray[queryN]);
            System.out.println("size: " + ScoreMapArray[queryN].size() + " Query: " + (queryN+1));
        }
        
        // building a HashMap<String, Double> for making its Graph
        HashMap<String, Double>[] queryMaps = new HashMap[numOfQ];
        for(int queryN = 0; queryN < numOfQ; queryN++){
            for(String key : keysOfQarray[queryN]){
                String[] docName = key.split(" ");
                String anotherPossibleKey = docName[1] + " " + docName[0];
                if(fbm.bigCompleteMap.containsKey(key)){
                    queryMaps[queryN].put(key, fbm.bigCompleteMap.get(key));
                }
                else if(fbm.bigCompleteMap.containsKey(anotherPossibleKey)){
                    queryMaps[queryN].put(key, fbm.bigCompleteMap.get(anotherPossibleKey));
                }
            }
            queryMaps[queryN] = fbm.sortMap(queryMaps[queryN]);
        }
        
        // Building a Graph for Each Query 
        FBCgraph[] graph = new FBCgraph[numOfQ];
        for(int queryN = 0; queryN < numOfQ; queryN++){
            graph[queryN] = new FBCgraph(queryMaps[queryN]);
            System.out.println("\nTotal # of Vertices: " + graph[queryN].getGraph().getVertexCount() + " Total # of Edges: " +graph[queryN].getGraph().getEdgeCount());
            // Betweenness Centrality is performed here
            System.out.println("\n----- Displaying Centrality Measure Result -----\n");
            BetweennessCentrality measure = new BetweennessCentrality(graph[queryN].getGraph(), true, false);
            measure.setRemoveRankScoresOnFinalize(false);
            measure.evaluate();
            List<Ranking<?>> ranking = measure.getRankings();
            Iterator it = ranking.iterator();
            // The maximum score = rank 1 score
            double maxScore = ranking.get(0).rankScore;
            // The minimum score = last rank score
            double minScore = ranking.get(ranking.size()-1).rankScore;
            int rCounter = 1;
            while(it.hasNext()){
                Ranking rank = (Ranking) it.next();
                // normalizing formula is applied here
                double normScore = (rank.rankScore - minScore)/(maxScore - minScore);
                //System.out.println("Rank #" + rCounter + ": Vertex Name: " + rank.getRanked() + " Score: " + normScore + " Original TF-IDF Score: " + ScoreMapArray[0].get(""+rank.getRanked()));
                System.out.println(queryN+1 + " Q0 " + rank.getRanked() + " " + rCounter + " " + normScore + " Default");
                rCounter++;   
            }
        }
 
    }
    
}
