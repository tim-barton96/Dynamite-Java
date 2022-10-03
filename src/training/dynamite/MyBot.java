package training.dynamite;

import com.softwire.dynamite.bot.Bot;
import com.softwire.dynamite.game.*;
import com.softwire.dynamite.runner.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyBot implements Bot {

    private List<Move> opponentsMoves = new ArrayList<>();

    private int[] timesRPSplayed = {1, 1, 1};

    private int dynamiteCounter = 0;

    private int[] dynamiteRhythm = new int[2];

    private int dynamiteSpamCounter = 0;

    private int weightIndex;

    private boolean allOut;

    private List<Integer> opponentDynamiteRhythm = new ArrayList<>();

    private int roundsSinceLastDynamite;

    private int opponentRhythm;

    private int swagCount;

    private boolean slimeball = false;

    private int slimeballCounter;



    public MyBot() {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        System.out.println("Started new match");
    }

    @Override
    public Move makeMove(Gamestate gamestate) {

        String weight = "Rock";

        if (gamestate.getRounds().size() == 0) {
            return randomMove(weight);
        }

        Round lastRound = (Round)gamestate.getRounds().get(gamestate.getRounds().size() - 1);
        Move opponentsLastMove = lastRound.getP2();
        Move myLastMove = lastRound.getP1();

        addToPreviousMoveList(opponentsLastMove, myLastMove);

        dynamiteSpamCheck(opponentsLastMove);


        if (dynamiteSpamCounter > 2) {
            return Move.W;
        }

        if (isThereRhythm() && gamestate.getRounds().size() % opponentRhythm == 0) {
            return Move.W;
        }

        if (dynamiteCounter < 100 && isItDynamiteTime()) {
            dynamiteCounter++;
            return Move.D;
        } else if (dynamiteCounter == 100 && !allOut) {
            System.out.println("All out " + gamestate.getRounds().size());
            allOut = true;
        }

        weight = getMoveWeighted(opponentsMoves);

        return randomMove(weight);
    }

    public void addToPreviousMoveList(Move lastMove, Move demonTime) {
        opponentsMoves.add(lastMove);
        if (lastMove == Move.D) {
            opponentDynamiteRhythm.add(roundsSinceLastDynamite);
            roundsSinceLastDynamite = 1;
        } else {
            roundsSinceLastDynamite++;
        }
        switch (lastMove) {
            case R: timesRPSplayed[0]++; break;
            case P: timesRPSplayed[1]++; break;
            case S: timesRPSplayed[2]++;
        }


        if (lastMove == demonTime) {
            swagCount++;
        } else {
            swagCount = 0;
        }

    }

    public void dynamiteSpamCheck(Move lastMove) {
        if (lastMove == Move.D) {
            dynamiteSpamCounter++;
        } else {
            dynamiteSpamCounter = 0;
        }
    }

    public boolean isThereRhythm() {

        if (opponentDynamiteRhythm.size() < 2) {
            return false;
        }

        Integer firstRound = opponentDynamiteRhythm.get(1);

        for (Integer rounds: opponentDynamiteRhythm) {
            if (!Objects.equals(rounds, firstRound)) {
                return false;
            }
        }
        opponentRhythm = firstRound;
        return true;
    }

    public String getMoveWeighted(List<Move> opponentsMoves) {

        int timesRockPlayed = timesRPSplayed[0];
        int timesPaperPlayed = timesRPSplayed[1];
        int timesScissorsPlayed = timesRPSplayed[2];


        if (timesRockPlayed > timesScissorsPlayed && timesRockPlayed > timesPaperPlayed) {
                weightIndex = ((timesRockPlayed / timesScissorsPlayed + timesRockPlayed / timesPaperPlayed) / 2);
            return "Paper";
        } else if (timesPaperPlayed > timesRockPlayed && timesPaperPlayed > timesScissorsPlayed) {
                weightIndex = (timesPaperPlayed / timesRockPlayed + timesPaperPlayed / timesScissorsPlayed) / 2;
            return "Scissors";
        }
            weightIndex = (timesScissorsPlayed / timesPaperPlayed + timesScissorsPlayed / timesRockPlayed) / 2;


        return "Rock";

    }

    public boolean isItDynamiteTime() {


        if (swagCount > 2) {
            return true;
        }

        if(dynamiteRhythm[0] == 0) {
            if (dynamiteRhythm[1] > 0) {
                dynamiteRhythm[1]--;
                return true;
            } else if (dynamiteRhythm[1] == 0) {
                dynamiteRhythm[0] = (int)Math.floor(Math.random() * 20.0);
                dynamiteRhythm[1] = (int)Math.floor(Math.random() * 2.0);
            }
        } else {
            dynamiteRhythm[0]--;
        }

        return false;

    }

    public Move randomMove(String weight) {
        int random = (int)Math.floor((Math.random() * 10.0));

        Move weighted;
        Move unweighted1;
        Move unweighted2;

        switch (weight) {
            case "Rock" : weighted = Move.R; unweighted1 = Move.S; unweighted2 = Move.P; break;
            case "Scissors" : weighted = Move.S; unweighted1 = Move.R; unweighted2 = Move.P; break;
            default: weighted = Move.P; unweighted1 = Move.R; unweighted2 = Move.S;
        }

        if (random < 3) {
            return unweighted1;
        } else if (random < 6) {
            return unweighted2;
        }

        return weighted;
    }
}
