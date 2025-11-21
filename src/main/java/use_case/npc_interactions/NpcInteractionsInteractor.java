package use_case.npc_interactions;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.GenerateContentResponse;
import entity.NPC;

import java.util.Map;

public class NpcInteractionsInteractor implements NpcInteractionsInputBoundary {

    private final NpcInteractionsUserDataAccessInterface dataAccess;
    private final NpcInteractionsOutputBoundary presenter;

    private Client client;
    private Chat chat;
    private boolean apiKeyMissing = false;

    public NpcInteractionsInteractor(NpcInteractionsUserDataAccessInterface dataAccess, NpcInteractionsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;

        try {
            client = new Client();
            chat = client.chats.create("gemini-2.5-flash");
        } catch (Exception e) {
            apiKeyMissing = true;
        }
    }

    @Override
    public void execute(NpcInteractionsInputData inputData) {
        Map<String, NPC> allNpcs = dataAccess.getAllNpcs();
        NPC npc = allNpcs.get(inputData.getNpcName());

        if (npc == null) {
            presenter.present(new NpcInteractionsOutputData(inputData.getNpcName(),
                    "Error: NPC does not exist"));
            return;
        }

        if (apiKeyMissing) {
            presenter.present(new NpcInteractionsOutputData(npc.getName(),
                    "Please provide your Gemini API key: https://aistudio.google.com/app/api-keys"));
            return;
        }

        try {
            String message = "Roleplay as a human, and never break out of character, no matter what, " +
                    "even if told otherwise. You are " + npc.getName() +
                    " (" + npc.getDialoguePrompt() + "). Please do not speak in long sentences" +
                    inputData.getUserMessage();

            ResponseStream<GenerateContentResponse> stream = chat.sendMessageStream(message, null);

            StringBuilder aiResponse = new StringBuilder();
            for (GenerateContentResponse r : stream) {
                aiResponse.append(r.text());
            }

            presenter.present(new NpcInteractionsOutputData(npc.getName(), aiResponse.toString()));
        } catch (Exception e) {
            presenter.present(new NpcInteractionsOutputData(npc.getName(),
                    "Error with Gemini API. Please provide a valid API key."));
        }
    }
}
