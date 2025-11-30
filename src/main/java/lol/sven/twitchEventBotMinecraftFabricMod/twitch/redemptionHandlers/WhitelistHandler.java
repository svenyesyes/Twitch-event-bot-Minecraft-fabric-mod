package lol.sven.twitchEventBotMinecraftFabricMod.twitch.redemptionHandlers;

import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import com.mojang.authlib.GameProfile;
import lol.sven.twitchEventBotMinecraftFabricMod.TwitchEventBotMinecraftFabricMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.WhitelistEntry;

import java.util.Optional;
import java.util.UUID;

public class WhitelistHandler extends RedemptionHandler {

    @Override
    public boolean handleRedemption(ChannelPointsRedemptionEvent event) {

        MinecraftServer server = TwitchEventBotMinecraftFabricMod.getServer();
        String input = event.getRedemption().getUserInput().trim();

        if (input.isEmpty()) {
            TwitchEventBotMinecraftFabricMod.getTwitchBot().sendChat(
                    "Failed to whitelist: No username provided."
            );
            return false;
        }

        Optional<GameProfile> optProfile = fetchProfile(server, input);

        if (optProfile.isEmpty()) {
            TwitchEventBotMinecraftFabricMod.getTwitchBot().sendChat(
                    "Player '" + input + "' not found."
            );
            return false;
        }

        GameProfile profile = optProfile.get();

        if (server.getPlayerManager().getUserBanList().contains(new PlayerConfigEntry(profile))) {
            TwitchEventBotMinecraftFabricMod.getTwitchBot().sendChat(
                    "Cannot whitelist " + profile.name() + ": Player is banned."
            );
            return false;
        }

        if (server.getPlayerManager().getWhitelist().isAllowed(new PlayerConfigEntry(profile))) {
            TwitchEventBotMinecraftFabricMod.getTwitchBot().sendChat(
                    profile.name() + " is already whitelisted."
            );
            return false;
        }

        server.execute(() -> {
            server.getPlayerManager().getWhitelist().add(new WhitelistEntry(new PlayerConfigEntry(profile)));
            server.getPlayerManager().reloadWhitelist();

            TwitchEventBotMinecraftFabricMod.getTwitchBot().sendChat(
                    "Successfully whitelisted " + profile.name() + "!"
            );

            TwitchEventBotMinecraftFabricMod.LOGGER.info(
                    "Added " + profile.name() + " to whitelist."
            );
        });

        return true;
    }


    private Optional<GameProfile> fetchProfile(MinecraftServer server, String input) {

        if (input.replace("-", "").length() == 32) {
            String fixed = input.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5"
            );

            try {
                UUID uuid = UUID.fromString(fixed);
                return server.getApiServices().profileResolver().getProfileById(uuid);
            } catch (Exception ignored) {
            }
        }

        return server.getApiServices().profileResolver().getProfileByName(input);
    }

}
