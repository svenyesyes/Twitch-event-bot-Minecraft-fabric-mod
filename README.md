# Twitch Event Bot — Minecraft Fabric Mod

A server-side Fabric mod that integrates Minecraft with Twitch chat and Channel Point redemptions.  
This mod lets streamers create interactive streams where viewers directly influence the server.

`This is an template/example. In it's current state, this mod deliveres very limited features.`

This is the **Fabric version**.  
For the **Spigot/Paper plugin**, click here:  
https://github.com/svenyesyes/twitch-event-bot-minecraft-plugin

---

## Features

### Twitch Chat Integration
- The bot joins your Twitch chat
- Supports custom Twitch chat commands (`!examplecommand`)
- Server → Twitch chat messages supported
- Dynamic placeholders:
  - `{players}`
  - `{players.online}`

### Channel Points Integration
- Trigger server actions via Channel Point rewards
- Includes a full **whitelist redemption handler**

### Works on Fabric 1.21.10+
- Built using modern Fabric loader + Fabric API  
- Fully remapped & safely packaged with Twitch4J embedded

---

## Installation

1. Download the latest release:  
   https://github.com/svenyesyes/twitch-event-bot-minecraft-fabric-mod/releases

2. Place the JAR in:
```arduino
/mods/
```

3. Launch the server once to generate:
```arduino
config/twitch-event-bot/config.yml
```
4. Stop the server and edit the config.

## Obtaining a Twitch OAuth Token

Use the official Twitch Chat OAuth generator:

https://id.twitch.tv/oauth2/authorize?client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost&response_type=token&scope=chat:read+chat:edit+channel:read:redemptions

Copy the token (`access_token=...`) and paste it into your config:

```yaml
oauth-token: "your_oauth_token_here"
```
## Configuration
Located at:

```arduino
config/twitch-event-bot/config.yml
```
### Example:
```yaml
oauth-token: ""
channel: ""

ttv_chat:
  prefix: "!"
  commands:
    online: "Online players: {players.online}"
    players: "Players online: {players}"

redemption_names:
  whitelist: "Add to whitelist"
```
Redemption names should be set to the redemption title on Twitch.
For example, if my channel point redemption for Whitelist has the name "get whitelisted":
```yaml
redemption_names:
  whitelist: "get whitelisted"
```

## Custom Channel Rewards (Redemption Handlers)
Handlers live in:

```arduino
lol.sven.twitchEventBotMinecraftFabricMod.twitch.redemptionHandlers
```
Create your own by extending:
```java
public abstract class RedemptionHandler {
    public abstract boolean handleRedemption(ChannelPointsRedemptionEvent event);
}
```
You can make your own interactions:
- Spawn mobs
- Give items
- Run commands
- TP players
- Trigger events
- Anything server-side

## Embedded Dependencies
This mod includes a fully shaded & relocated Twitch4J build:
- No conflicts with other mods
- No external libs required

## Related Projects
**Spigot/Paper** plugin version:
https://github.com/svenyesyes/twitch-event-bot-minecraft-plugin
