package me.despical.oitc.utils.conversation;

import me.despical.oitc.Main;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class SimpleConversationBuilder {

	private static final Main plugin = JavaPlugin.getPlugin(Main.class);
	private final ConversationFactory conversationFactory;

	public SimpleConversationBuilder() {
		conversationFactory = new ConversationFactory(plugin)
			.withModality(true)
			.withLocalEcho(false)
			.withEscapeSequence("cancel")
			.withTimeout(30).addConversationAbandonedListener(listener -> {
			if (listener.gracefulExit()) {
				return;
			}

			listener.getContext().getForWhom().sendRawMessage(plugin.getChatManager().colorMessage("Commands.Conversation.Operation-Timeout"));

		}).thatExcludesNonPlayersWithMessage(plugin.getChatManager().colorMessage("Commands.Conversation.Only-By-Player"));
	}

	public SimpleConversationBuilder withPrompt(Prompt prompt) {
		conversationFactory.withFirstPrompt(prompt);
		return this;
	}

	public void buildFor(Conversable conversable) {
		conversationFactory.buildConversation(conversable).begin();
	}
}