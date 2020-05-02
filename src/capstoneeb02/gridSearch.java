/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capstoneeb02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kiyoshimohammad
 */
public class gridSearch {

    public int numOfTopics = 200;
    private String best_trec = "/home/kiyoshimohammad/Documents/results/improved_result_TF_IDF_with_FBM_full_corpus.txt";
    private HashMap<String, Double>[] ScoreMapArray;
    private ArrayList<Double>[] old_score;
    private ArrayList<Double>[] new_score;

    public gridSearch(double lambda1, double lambda2, FileWriter textFile, FileWriter testFile) throws Exception {
        //his.ScoreMapArray = new HashMap[numOfTopics];
        this.old_score = new ArrayList[numOfTopics];
        this.new_score = new ArrayList[numOfTopics];

        BufferedReader br = new BufferedReader(new FileReader(best_trec));
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
                double tfidf_score = Double.parseDouble(element[4]);
                double centrality_score = Double.parseDouble(element[5]);
                if (queryNo != prevQueryNo) {
                    index++;
                    theoreticalQN++;

                    if (queryNo != theoreticalQN) {
                        for (theoreticalQN = theoreticalQN; theoreticalQN < queryNo; theoreticalQN++) {
                            //this.ScoreMapArray[theoreticalQN - 1] = new HashMap<String, Double>();
                            this.old_score[theoreticalQN - 1] = new ArrayList<>();
                            this.new_score[theoreticalQN - 1] = new ArrayList<>();
                            //this.ScoreMapArray[theoreticalQN - 1].put(empty, -1.0);
                            this.old_score[theoreticalQN - 1].add(-1.0);
                            this.new_score[theoreticalQN - 1].add(-1.0);
                            // debugging purpose
//                            for(String str : this.ScoreMapArray[theoreticalQN-1].keySet()){
//                                System.out.println("query# " + theoreticalQN + " is " + str + " with score: " + this.ScoreMapArray[theoreticalQN-1].get(str));
//                            } 
                        }
                        index = theoreticalQN - 1;
                    }
                    //this.ScoreMapArray[index] = new HashMap<String, Double>();
                    this.old_score[index] = new ArrayList<>();
                    this.new_score[index] = new ArrayList<>();
                    // String = Document name, Double = Original TF-IDF OR SDM score
                }
                // if(fileContentsMap.containsKey(docName)){
                // each hashmap represents the graph entries (200 docs) for its particular
                // query number. query number = index + 1
                //this.ScoreMapArray[index].put(docName, score);
                //this.old_score[index].add(tfidf_score);
                //this.new_score[index].add(centrality_score);
                double score = lambda1 * tfidf_score + lambda2 * centrality_score;
                //textFile.write(queryNo + " Q0 " + docName + " " + element[3] + " " + score + " Default" + "\n");
                //testFile.write(queryNo + " Q0 " + docName + " " + element[3] + " " + score + " Default" + "\n");
                System.out.println(queryNo + " Q0 " + docName + " " + element[3] + " " + score + " Default" + "\n");
                // }

                prevQueryNo = queryNo;
                line = br.readLine();
            }
        } finally {
            br.close();
        }
    }

    public static void readFromText(String path, double lambda1, double lambda2, FileWriter textFile) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));

        String line = br.readLine();

        while (line != null) {
            String[] entry = line.split(" ");
            int queryNo = Integer.parseInt(entry[0]);
            String docName = entry[2];
            double tfidf_score = Double.parseDouble(entry[4]);
            double centrality_score = Double.parseDouble(entry[5]);

            double score = lambda1 * tfidf_score + lambda2 * centrality_score;

            //textFile.write(queryNo + " Q0 " + docName + " " + entry[3] + " " + score + " Default" + "\n");
            //testFile.write(queryNo + " Q0 " + docName + " " + entry[3] + " " + score + " Default" + "\n");
            System.out.print(queryNo + " Q0 " + docName + " " + entry[3] + " " + score + " Default" + "\n");
            line = br.readLine();
        }
        br.close();
    }

    public static void main(String[] args) throws Exception {
        double[] lambda1 = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        String path = "/home/kiyoshimohammad/Documents/results/improved_result_TF_IDF_with_FBM_full_corpus.txt";

        for (int i = 11; i < 12; i++) {
            double lambda2 = 1 - lambda1[i];
            FileWriter resultTextFile = new FileWriter("/home/kiyoshimohammad/Documents/results/grid_search_results/improved_trec_result_" + lambda1[i] + ".txt");
            //FileWriter resultTestFile = new FileWriter("/home/kiyoshimohammad/Documents/results/grid_search_results/improved_trec_result_" + lambda1[i] + ".test");
            //gridSearch grid = new gridSearch(lambda1[i], lambda2, resultTextFile, resultTestFile);
            readFromText(path, lambda1[i], lambda2, resultTextFile);
        }
    }
}
