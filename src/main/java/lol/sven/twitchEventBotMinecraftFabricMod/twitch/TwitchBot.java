package lol.sven.twitchEventBotMinecraftFabricMod.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lol.sven.twitchEventBotMinecraftFabricMod.TwitchEventBotMinecraftFabricMod;
import lol.sven.twitchEventBotMinecraftFabricMod.config.ConfigManager;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TwitchBot {

    private TwitchClient twitchClient;

    private String channelName;
    private String broadcasterId;

    MinecraftServer server = TwitchEventBotMinecraftFabricMod.getServer();

    public TwitchBot() {

        String channelName = ConfigManager.getString("channel");
        String twitchToken = ConfigManager.getString("oauth-token");

        this.channelName = channelName;

        if (twitchToken == null || channelName == null) {
            TwitchEventBotMinecraftFabricMod.LOGGER.warn("Twitch Bot disabled: Missing token or channel in config.yml");
            return;
        }

        OAuth2Credential credential = new OAuth2Credential("twitch", twitchToken);

        twitchClient = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withChatAccount(credential)
                .withDefaultAuthToken(credential)
                .build();

        CompletableFuture.runAsync(() -> {
            try {
                TwitchEventBotMinecraftFabricMod.LOGGER.info("[Twitch] Fetching broadcaster ID...");

                broadcasterId = twitchClient.getHelix()
                        .getUsers(null, null, List.of(channelName))
                        .execute()
                        .getUsers()
                        .get(0)
                        .getId();

                TwitchEventBotMinecraftFabricMod.LOGGER.info("[Twitch] Broadcaster ID = " + broadcasterId);

                initPubSub(credential);
                joinChat();

            } catch (Exception ex) {
                TwitchEventBotMinecraftFabricMod.LOGGER.error("[Twitch] Failed to start bot: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private void initPubSub(OAuth2Credential credential) {
        if (broadcasterId == null) {
            TwitchEventBotMinecraftFabricMod.LOGGER.error("[Twitch] Cannot init PubSub: broadcasterId = null");
            return;
        }

        TwitchEventBotMinecraftFabricMod.LOGGER.info("[Twitch] Subscribing to Channel Points...");
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, broadcasterId);

        twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, TwitchRedemption::HandleRedemption);


        TwitchEventBotMinecraftFabricMod.LOGGER.info("[Twitch] PubSub initialized.");
    }

    private void joinChat() {
        server.execute(() -> {
            try {
                twitchClient.getChat().joinChannel(channelName);
                TwitchEventBotMinecraftFabricMod.LOGGER.info("[Twitch] Joined chat: #" + channelName);
                twitchClient.getEventManager().onEvent(com.github.twitch4j.chat.events.channel.ChannelMessageEvent.class, TwitchChat::handleChatMessage);

            } catch (Exception ex) {
                TwitchEventBotMinecraftFabricMod.LOGGER.warn("[Twitch] Failed to join chat: " + ex.getMessage());
            }
        });
    }

    /**
     * Send message to chat safely
     */
    public void sendChat(String message) {
        if (twitchClient == null) return;

        twitchClient.getChat().sendMessage(channelName, message);
    }

    /**
     * Stop bot
     */
    public void stop() {
        try {
            if (twitchClient != null) {
                twitchClient.close();
                TwitchEventBotMinecraftFabricMod.LOGGER.info("[Twitch] Bot shut down.");
            }
        } catch (Exception ex) {
            TwitchEventBotMinecraftFabricMod.LOGGER.warn("[Twitch] Error shutting down: " + ex.getMessage());
        }
    }
}
