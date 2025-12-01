package use_case.events.ActivateRandomOutcome;
import entity.EventOutcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ActivateRandomOutcomeInteractor implements ActivateRandomOutcomeInputBoundary {
    ActivateRandomOutcomeDataAccessInterface activateRandomOutcomeDataAccess;
    ActivateRandomOutcomeOutputBoundary activateOutcomePresenter;

    public ActivateRandomOutcomeInteractor(ActivateRandomOutcomeDataAccessInterface activateRandomOutcomeDataAccess,
                                           ActivateRandomOutcomeOutputBoundary activateOutcomePresenter) {
        this.activateRandomOutcomeDataAccess = activateRandomOutcomeDataAccess;
        this.activateOutcomePresenter = activateOutcomePresenter;
    }

    public void execute(ActivateRandomOutcomeInputData activateRandomOutcomeInputData) {
        HashMap<Integer, EventOutcome> outcomes = activateRandomOutcomeInputData.getOutcomes();
        int selectedIndex;
            // Fall back to random selection based on weights
            Random random = new Random();
            double totalWeights = 0.0;
            ArrayList<Double> cumulativeWeights = new ArrayList<>();

            for (EventOutcome outcome : outcomes.values()) {
                totalWeights += outcome.getOutcomeChance();
                cumulativeWeights.add(totalWeights);
            }
            double randomNumber = random.nextDouble();

            selectedIndex = 0;
            for (int i = 0; i < cumulativeWeights.size(); i++) {
                if (randomNumber < cumulativeWeights.get(i)) {
                    selectedIndex = i;
                    break;
                }
            }
        // Apply the selected outcome
        EventOutcome selectedOutcome = outcomes.get(selectedIndex);
        if (selectedOutcome.getOutcomeResult() > 0){
            activateRandomOutcomeDataAccess.addDailyEarnings(selectedOutcome.getOutcomeResult());
        }
        else if (selectedOutcome.getOutcomeResult() < 0){
            activateRandomOutcomeDataAccess.addDailySpending(selectedOutcome.getOutcomeResult() * -1);
        }
        activateRandomOutcomeDataAccess.setBalance(activateRandomOutcomeDataAccess.getBalance() +
                selectedOutcome.getOutcomeResult());
        ActivateRandomOutcomeOutputData activateRandomOutcomeOutputData =
                new ActivateRandomOutcomeOutputData(selectedOutcome, selectedIndex);
        activateOutcomePresenter.prepareSuccessView(activateRandomOutcomeOutputData);
    }
}
