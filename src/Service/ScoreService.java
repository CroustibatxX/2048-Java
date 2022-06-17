package Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreService {
    private int score = 0;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void scoreReset(){
        score = 0;
    }

    public void updateScore(int score){
        this.score += score;
    }



    public void saveScore(int gameSize){
        try {
            List<Integer> scores = getSavedScore(gameSize);
            Collections.sort(scores,Collections.reverseOrder());
            if(scores.size() < 5){
                scores.add(score);
                Collections.sort(scores,Collections.reverseOrder());
            }
            else if(score > scores.get(scores.size() - 1)){
                scores.remove(scores.size() - 1);
                scores.add(score);
                Collections.sort(scores,Collections.reverseOrder());
            }

            FileWriter writer = new FileWriter(getScoreFile(gameSize), false);
            for (int i=0; i < scores.size(); i++) {
                writer.write(Integer.toString(scores.get(i))+ "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getScoreFile(int gameSize){
        String scoreFile = "score.txt";
        switch(gameSize) {
            case 5:
                scoreFile = "score5.txt";
                break;
            case 8:
                scoreFile = "score8.txt";
                break;
        }
        return scoreFile;
    }

    public List<Integer> getSavedScore(int gameSize){
        List<Integer> scores = new ArrayList<Integer>();
        try {
            FileReader reader = new FileReader(getScoreFile(gameSize));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                scores.add(Integer.parseInt(line));
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scores;
    }




}
