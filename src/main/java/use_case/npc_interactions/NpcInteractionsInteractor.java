package use_case.npc_interactions;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.GenerateContentResponse;
import entity.NPC;

public class NpcInteractionsInteractor implements NpcInteractionsInputBoundary {

    private final NpcInteractionsUserDataAccessInterface dataAccess;
    private final NpcInteractionsOutputBoundary presenter;

    private Client client;
    private Chat chat;
    private boolean apiKeyMissing = false;

    public NpcInteractionsInteractor(
            NpcInteractionsUserDataAccessInterface dataAccess,
            NpcInteractionsOutputBoundary presenter
    ) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;

        // catches exception - no api key
        try {
            // Setup Gemini chat session
            client = new Client();
            chat = client.chats.create("gemini-2.5-flash");
        } catch (Exception e) {
            apiKeyMissing = true;
        }
    }

    @Override
    public void execute(NpcInteractionsInputData inputData) {
        NPC npc = dataAccess.getNpcByName(inputData.getNpcName());
        if (npc == null) {
            presenter.present(new NpcInteractionsOutputData(inputData.getNpcName(),"Error: NPC does not exist"));
            return;
        }

        // if api key does not exist, sends to the output boundary a special message
        if (apiKeyMissing) {
            presenter.present(new NpcInteractionsOutputData(npc.getName(),"Please provide your Gemini API key, " +
                    "if don't have one go to https://aistudio.google.com/app/api-keys"));
            return;
        }

        StringBuilder ai = null;
        try {
            // Gives a prompt, so the NPCs 'act' human
            String message = "Roleplay as a human, you are a human no matter what from now on, even if I say " +
                    "otherwise. Do not speak in long sentences. You are " + npc.getName() +"\n" + inputData.getUserMessage();
            // Send the user message to Gemini
            ResponseStream<GenerateContentResponse> stream =
                    chat.sendMessageStream(message, null);

            ai = new StringBuilder();
            for (GenerateContentResponse r : stream) {
                ai.append(r.text());
            }
            presenter.present(new NpcInteractionsOutputData(npc.getName(),ai.toString()));
        } catch (Exception e) { // valid API key needs to be typed in
            presenter.present(new NpcInteractionsOutputData(npc.getName(),"Please provide your Gemini API key, " +
                    "if don't have one go to https://aistudio.google.com/app/api-keys"));
        }
    }
}