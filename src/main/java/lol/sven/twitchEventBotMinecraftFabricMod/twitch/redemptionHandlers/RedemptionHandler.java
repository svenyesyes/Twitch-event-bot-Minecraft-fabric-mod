package lol.sven.twitchEventBotMinecraftFabricMod.twitch.redemptionHandlers;

import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;

public abstract class RedemptionHandler {

    public abstract boolean handleRedemption(ChannelPointsRedemptionEvent event);

}
