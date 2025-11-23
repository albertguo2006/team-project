package use_case.events.ActivateRandomOutcome;
import data_access.EventDataAccessObject;
import entity.Event;
import entity.EventOutcome;
import entity.Player;
import interface_adapter.events.ActivateOutcomePresenter;

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
        /// Creates a list of weights from the chances of each eventOutcome
        Random random = new Random();
        double totalWeights = 0.0; // The current total weight of all seen outcomes (should always == 1.0 by the end)
        ArrayList<Double> cumulativeWeights = new ArrayList<Double>(); // An ordered list of the cumulative weights of
                                                                       // all outcomes
        HashMap<Integer, EventOutcome> outcomes = activateRandomOutcomeInputData.getOutcomes();

        for (EventOutcome outcome : outcomes.values()) {
            totalWeights += outcome.getOutcomeChance();
            cumulativeWeights.add(totalWeights);
        }
        double randomNumber = random.nextDouble(); //Gets a random double from 0 to 1.0

        /// If the cumulative weight of the ith outcome is greater than the random number, select it,
        /// otherwise move to the next outcome in the list.

        /// Since the final totalWeight should always == 1.0, the final cumulativeWeight == 1.0, so this
        /// will always return an outcome.
        for (int i = 0; i < cumulativeWeights.size(); i++) {
            if (randomNumber < cumulativeWeights.get(i)) {
                /// Adds the outcome's result to the player's balance
                activateRandomOutcomeDataAccess.setBalance(activateRandomOutcomeDataAccess.getBalance() +
                        outcomes.get(i).getOutcomeResult());
                ActivateRandomOutcomeOutputData activateRandomOutcomeOutputData =
                        new ActivateRandomOutcomeOutputData(outcomes.get(i), i);
                activateOutcomePresenter.prepareSuccessView(activateRandomOutcomeOutputData);
                break; /// Break the loop when an outcome is picked
            }
        }
    }
}
