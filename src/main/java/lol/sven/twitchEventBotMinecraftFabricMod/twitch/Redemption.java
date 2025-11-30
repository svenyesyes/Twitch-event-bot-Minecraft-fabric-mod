package lol.sven.twitchEventBotMinecraftFabricMod.twitch;

import lol.sven.twitchEventBotMinecraftFabricMod.twitch.redemptionHandlers.RedemptionHandler;
import lol.sven.twitchEventBotMinecraftFabricMod.twitch.redemptionHandlers.WhitelistHandler;

public enum Redemption {

    ADD_WHITELIST("whitelist", new WhitelistHandler());

    private final String id;
    private final RedemptionHandler handler;

    Redemption(String id, RedemptionHandler handler) {
        this.id = id;
        this.handler = handler;
    }

    public String getId() {
        return id;
    }

    public RedemptionHandler getHandler() {
        return handler;
    }

    @Override
    public String toString() {
        return id;
    }
}
