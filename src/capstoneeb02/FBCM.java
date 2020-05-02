/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capstoneeb02;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
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
public class FBCM {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        TopicMap topicMap = new TopicMap();
        HashMap<String, Double>[] queryMaps = topicMap.queryMaps;

        // Step 1: Pre-Process => obtaining doc entries and TF-IDF/SDM score for
        // each query ranking entries
        PreProcess prep = new PreProcess();
        HashMap<String, Double>[] ScoreMapArray = prep.getScoreMapArray();
        int numOfQ = ScoreMapArray.length;

        // Step 2: possible keys (doc1 doc2) are created for each query ranking
        // if there are more than 1 doc entry in the ranking
        FileWriter resultFile = new FileWriter("/home/kiyoshimohammad/Documents/results/improved_result_TF_IDF_with_FBM_full_corpus.txt");

        for (int queryN = 0; queryN < numOfQ; queryN++) {
            // empty ranking for a query is handled here
            if (ScoreMapArray[queryN].size() == 1 && ScoreMapArray[queryN].containsKey("empty")) {
                System.out.println("\nSkipping query# " + (queryN + 1));
            } // if there is only 1 doc entry in the original ranking then a Graph 
            // cannot be created, so the measurement score is 0
            else if (ScoreMapArray[queryN].size() == 1) {
                System.out.println("\nQuery# " + (queryN + 1) + "Graph cannot be built as it has only 1 entry");
                System.out.println("----- Displaying Centrality Measure Result -----\n");
                for (String docName : ScoreMapArray[queryN].keySet()) {
                    //System.out.println(queryN + 1 + " Q0 " + docName + " " + 1 + " " + 0.0 + " Default");
                    resultFile.write(queryN + 1 + " Q0 " + docName + " " + 1 + " " + 0.0 + " Default" + "\n");
                }
            } // otherwise, we build a graph and do centrality measure to re-construct
            // the ranking for a query doc entries here
            else {
                FBCgraph graph = new FBCgraph(queryMaps[queryN]);
                System.out.println("\nQuery# " + (queryN + 1) + " Total # of Vertices: " + graph.getGraph().getVertexCount() + " Total # of Edges: " + graph.getGraph().getEdgeCount());
                // Betweenness Centrality is performed here
                System.out.println("----- Displaying Centrality Measure Result -----\n");
                BetweennessCentrality measure = new BetweennessCentrality(graph.getGraph(), true, false);
                measure.setRemoveRankScoresOnFinalize(false);
                measure.evaluate();
                List<Ranking<?>> ranking = measure.getRankings();
                Iterator it = ranking.iterator();
                // The maximum score = rank 1 score
                double maxScore = ranking.get(0).rankScore;
                // The minimum score = last rank score
                double minScore = ranking.get(ranking.size() - 1).rankScore;
                int rCounter = 1;
                while (it.hasNext()) {
                    Ranking rank = (Ranking) it.next();
                    // normalizing formula is applied here
                    double normScore = (rank.rankScore - minScore) / (maxScore - minScore);
                    //System.out.println("Rank #" + rCounter + ": Vertex Name: " + rank.getRanked() + " Score: " + normScore + " Original TF-IDF Score: " + ScoreMapArray[0].get(""+rank.getRanked()));
                    //System.out.println(queryN + 1 + " Q0 " + rank.getRanked() + " " + rCounter + " " + normScore + " " + ScoreMapArray[queryN].get(""+rank.getRanked()) + " Default");
                    resultFile.write(queryN + 1 + " Q0 " + rank.getRanked() + " " + rCounter + " " + normScore + " " + ScoreMapArray[queryN].get("" + rank.getRanked()) + " Default" + "\n");
                    rCounter++;
                }
                //graph.displayGraph(graph.getGraph());
            }
        }
        resultFile.close();
        System.out.println("All done. Successfully wrote to the file.");
    }

}
    

