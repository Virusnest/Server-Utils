package net.kyrptonaught.serverutils.discordBridge;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.function.Consumer;

public class BridgeBot extends ListenerAdapter {
    private final MinecraftServer server;
    private final JDA jda;
    private final WebhookClient client;
    private final String channelID;

    private final HashMap<String, Consumer<SlashCommandInteraction>> commands = new HashMap<>();

    public BridgeBot(MinecraftServer server, JDA jda, WebhookClient webhookClient, String channelID) {
        this.server = server;
        this.jda = jda;
        this.client = webhookClient;
        this.channelID = channelID;
        this.jda.addEventListener(this);
    }

    public void registerCommand(String cmd, String description, Consumer<SlashCommandInteraction> execute) {
        this.jda.updateCommands().addCommands(Commands.slash(cmd, description)).queue();
        this.commands.put(cmd, execute);
    }

    public void sendMessage(String name, String url, String msg) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(name);
        builder.setAvatarUrl(url);
        builder.setContent(msg);
        client.send(builder.build());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isWebhookMessage() && event.getChannel().getId().equals(channelID)) {
            //event.getAuthor().getAsTag()
            Text message = Text.literal("[" + event.getAuthor().getName() + "]: " + event.getMessage().getContentDisplay());
            this.server.sendMessage(message);
            for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
                serverPlayerEntity.sendMessage(message, false);
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (commands.containsKey(event.getName()))
            commands.get(event.getName()).accept(event);
    }

    public void close() {
        if (jda != null) jda.shutdown();
        if (client != null) client.close();
    }
}
