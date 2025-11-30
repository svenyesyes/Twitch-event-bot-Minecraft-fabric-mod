package lol.sven.twitchEventBotMinecraftFabricMod.config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class ConfigManager {

    private static Map<String, Object> config;

    private static final Path CONFIG_PATH =
            Path.of("config", "twitch-event-bot", "config.yml");

    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());

                InputStream in = ConfigManager.class.getResourceAsStream("/config.yml");
                if (in == null) throw new FileNotFoundException("Missing default config.yml in resources!");

                Files.copy(in, CONFIG_PATH);
            }

            // YAML parsen
            Yaml yaml = new Yaml();
            config = yaml.load(new FileReader(CONFIG_PATH.toFile()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to load Twitch Bot config", e);
        }
    }

    public static String getString(String path) {
        String[] parts = path.split("\\.");
        Object current = config;

        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(part);
            if (current == null) return null;
        }

        return current.toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(String key) {
        Object o = config.get(key);
        if (o instanceof Map<?, ?> m)
            return (Map<String, Object>) m;

        return null;
    }
}
