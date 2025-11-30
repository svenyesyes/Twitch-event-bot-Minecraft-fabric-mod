package lol.sven.twitchEventBotMinecraftFabricMod.twitch;

import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import lol.sven.twitchEventBotMinecraftFabricMod.TwitchEventBotMinecraftFabricMod;
import lol.sven.twitchEventBotMinecraftFabricMod.config.ConfigManager;

import java.util.HashMap;

public class TwitchRedemption {

    private static HashMap<String, Redemption> redemptionNames = new HashMap<String, Redemption>();

    public static void updateRedemptionNames() {

        for (int i = 0; i < Redemption.values().length; i++) {
            Redemption r = Redemption.values()[i];
            String redemptionId = r.toString();

            String redemptionName = ConfigManager.getString("redemption_names." + redemptionId);

            if(redemptionName == null || redemptionName.isEmpty()) {
                TwitchEventBotMinecraftFabricMod.LOGGER.warn("Could not find channel point name for " + redemptionId);
                continue;
            }

            redemptionNames.put(redemptionName, r);
            TwitchEventBotMinecraftFabricMod.LOGGER.info("Linked " + redemptionId + " to channel point " + redemptionName);

        }
    }

    public static boolean HandleRedemption(ChannelPointsRedemptionEvent event) {

        ChannelPointsRedemption redemption = event.getRedemption();
        if (redemption == null || redemption.getReward().getTitle().isEmpty()) {
            return false;
        }

        Redemption r = redemptionNames.get(redemption.getReward().getTitle());
        if (r == null) {
            return false;
        }

        return r.getHandler().handleRedemption(event);
    }

}
