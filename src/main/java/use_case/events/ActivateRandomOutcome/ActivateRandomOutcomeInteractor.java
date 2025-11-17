package use_case.events.ActivateRandomOutcome;
import entity.Event;
import entity.EventOutcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ActivateRandomOutcomeInteractor implements ActivateRandomOutcomeInputBoundary {

    public ActivateRandomOutcomeInteractor() {
    }
    public void execute(ActivateRandomOutcomeInputData activateRandomOutcomeInputData) {
        Random random = new Random();
        double totalWeight = 0.0;
        ArrayList<Double> cumulativeWeights = new ArrayList<Double>();
        HashMap<Integer, EventOutcome> outcomes = activateRandomOutcomeInputData.getOutcomes();

        for (EventOutcome outcome : outcomes.values()) {
            totalWeight += outcome.getOutcomeChance();
            cumulativeWeights.add(totalWeight);
        }
        double randomNumber = random.nextDouble() * totalWeight;

        for (int i = 0; i < cumulativeWeights.size(); i++) {
            if (randomNumber < cumulativeWeights.get(i)) {
            }
        }
    }
}
