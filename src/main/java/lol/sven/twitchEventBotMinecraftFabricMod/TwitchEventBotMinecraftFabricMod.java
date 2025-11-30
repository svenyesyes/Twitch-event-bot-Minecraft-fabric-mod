package lol.sven.twitchEventBotMinecraftFabricMod;

import lol.sven.twitchEventBotMinecraftFabricMod.config.ConfigManager;
import lol.sven.twitchEventBotMinecraftFabricMod.twitch.TwitchBot;
import lol.sven.twitchEventBotMinecraftFabricMod.twitch.TwitchChat;
import lol.sven.twitchEventBotMinecraftFabricMod.twitch.TwitchRedemption;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TwitchEventBotMinecraftFabricMod implements ModInitializer {

    private static TwitchBot twitchBot;
    private static MinecraftServer server;
    public static final Logger LOGGER = LogManager.getLogger("TwitchEventBotFabric");

    @Override
    public void onInitialize() {

        ConfigManager.load();
        TwitchChat.registerCommands();
        TwitchRedemption.updateRedemptionNames();

        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer srv) -> {
            server = srv;
            twitchBot = new TwitchBot();
            LOGGER.info("Server loaded! Twitch bot ready.");
        });

        ServerLifecycleEvents.SERVER_STOPPING.register((MinecraftServer srv) -> {
            LOGGER.info("Server stopping!");
        });

    }

    public static TwitchBot getTwitchBot() {
        return twitchBot;
    }

    public static MinecraftServer getServer() {
        return server;
    }

}
