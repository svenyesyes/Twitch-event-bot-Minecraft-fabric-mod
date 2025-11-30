package lol.sven.twitchEventBotMinecraftFabricMod.twitch;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lol.sven.twitchEventBotMinecraftFabricMod.TwitchEventBotMinecraftFabricMod;
import lol.sven.twitchEventBotMinecraftFabricMod.config.ConfigManager;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

public class TwitchChat {

    static MinecraftServer server = TwitchEventBotMinecraftFabricMod.getServer();

    private static Map<String, Object> commands;
    private static String prefix;

    public static void registerCommands() {
        prefix = ConfigManager.getString("ttv_chat.prefix");
        if (prefix == null) prefix = "!";

        // Haal de submap "ttv_chat.commands"
        Map<String, Object> ttvChat = ConfigManager.getMap("ttv_chat");
        if (ttvChat == null) {
            commands = Map.of();
            TwitchEventBotMinecraftFabricMod.LOGGER.warn("[TTV Chat] No ttv_chat section found!");
            return;
        }

        Map<String, Object> cmds = (Map<String, Object>) ttvChat.get("commands");

        if (cmds == null) {
            commands = Map.of();
            TwitchEventBotMinecraftFabricMod.LOGGER.warn("[TTV Chat] No commands found!");
            return;
        }

        commands = cmds;
        TwitchEventBotMinecraftFabricMod.LOGGER.info("[TTV Chat] Loaded " + commands.size() + " Twitch commands.");
    }


    public static void handleChatMessage(ChannelMessageEvent event) {
        String msg = event.getMessage().toLowerCase();

        if (!msg.startsWith(prefix.toLowerCase())) return;

        String raw = msg.substring(prefix.length());
        String cmd = raw.split(" ")[0];

        if (!commands.containsKey(cmd)) return;

        String response = dynamicText(String.valueOf(commands.get(cmd)));

        TwitchEventBotMinecraftFabricMod
                .getTwitchBot()
                .sendChat(response);
    }

    private static String dynamicText(String raw) {

        MinecraftServer server = TwitchEventBotMinecraftFabricMod.getServer();
        if (server == null) return raw;

        var list = server.getPlayerManager().getPlayerList();

        int onlineCount = list.size();

        String players = list.stream()
                .map(p -> p.getName().getString())
                .collect(java.util.stream.Collectors.joining(", "));

        return raw
                .replace("{players}", players)
                .replace("{players.online}", String.valueOf(onlineCount));
    }

}
