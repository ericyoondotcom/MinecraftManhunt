# MinecraftManhunt
[View on Bukkit](https://dev.bukkit.org/projects/dream-style-manhunt) • 
[View on SpigotMC](https://www.spigotmc.org/resources/dream-style-manhunt.83309/) •
[View on PaperMC](https://papermc.io/forums/t/dream-style-manhunt/5072)

> **Having issues?** Check the [FAQ Issues page](https://github.com/yummypasta/MinecraftManhunt/issues?q=label%3AFAQ), or report a problem on the [Issues tab](https://github.com/yummypasta/MinecraftManhunt/issues/new/choose)!

---

Made by Eric ([yoonicode.com](http://yoonicode.com/?utm_source=github&utm_medium=web&utm_campaign=manhunt-github))<br>
Github: [yummypasta](https://github.com/yummypasta)<br>
Minecraft: [i18n](https://namemc.com/profile/i18n.4)

---

Inspired by [Dream](https://youtube.com/dreamtraps) on YouTube


## How It Works
Three teams: Hunters, Runners, and Spectators
Hunters try to kill the runners before they beat the enderdragon. Runners must beat the enderdragon before they get killed once!

## Features
- Compass tracking: Right-click with your compass to choose who to track, then the compass will point to that runner!
- Portal tracking: If a runner is in the nether, the compass will track their last-used portal!
- Teams assignment: Automatically assigns in-game `/team` to distinguish between players with colors!
- Discord integration: Optionally integrate with Discord for extra features!
  - Auto role assignment: Automatically assign Discord roles according to teams
  - Music player: Automatically (or manually) play music in your Discord voice channel that mirrors the action happening!

## Instructions For Use
- Move the .jar from the [Releases tab](https://github.com/yummypasta/MinecraftManhunt/releases/) to your plugins folder.
- Make sure your config file is up to date and has all the required options.
- Assign roles with `/speedrunner`, `/hunter`, and `/spectator`.
- If you want automatic music, type `/music auto` now.
- Type `/start`!
- After the headstart period is over, hunters should be able to start tracking runners by selecting someone to track by right-clicking with their compass.

> **Note**: teams are not persistent between server sessions— if you shut down the server you'll have to re-assign teams and type `/start` again. Have players put their items in chests before typing `/start` if you're doing this, since the command clears inventories.

### Discord Setup Instructions
- Create a Discord app from the [Developer Portal](https://discord.com/developers/applications).
- Add a Bot under the bots tab.
- Take note of your **Client ID** (in the General Information tab) and your **Token** (under the Bot tab).
- Go to the following link, replacing `123YourClientID456` with your client ID: `https://discord.com/oauth2/authorize?scope=bot&client_id=123YourClientID456&permissions=8`
- Select the Discord server to add your bot to.
- Add your Client ID and Token to the respective fields in the config file.
- Go to your Discord server, and add the following values to the config file, in accordance with the table above (you can `Right Click > Copy ID` if you turn on Developer Mode in Discord settings!)
  - the ID of your server
  - the ID of the voice channel you want your music to play in
  - the ID of the hunter, runner, and spectator Discord roles you want to be automatically assigned
> **Note**: For auto-role-assignment to work, each Discord user's nickname for your Server must be set to their Minecraft username.

## Commands
- `/speedrunner <username>`: Assign speedrunner role
- `/hunter <username>`: Assign hunter role
- `/spectator <username>`: Assign spectator role
- `/start`: Start the match
- `/end`: End the match
- `/compass`: Give yourself a compass
- `/music`: Controls the Discord music
  - `/music list`: Gets a list of available tracks to play.
  - `/music <trackname>`: Plays a specific track by nickname and turns off auto-music.
  - `/music stop`: Stops all music playing and turns off auto-music.
  - `/music auto`: Turns on auto-music, which plays different tracks based on game events and hunter/runner distance.
  - `/music forceupdate`: Forces the music to update to match the current hunter/runner distance. Use `/music stop` first if a special event track is playing.
- `/setheadstart <duration>`: Sets the headstart duration, in seconds. 
## Configuration Options
Edit the `plugins/MinecraftManhunt/config.yml` file with the following options:
  
  Key|Description|Type|Required?
  --|--|--|--
  headStartDuration | How long the hunters should get blindness and slowness when the match starts. | int | Required
  compassEnabledInNether | Set to true to allow the compass to work in the nether. | boolean | Optional, defaults to `true`
  setRunnersToSpecOnDeath | Set to true to set runners' gamemodes to spectator when they die. | boolean | Optional, defaults to `true`
  sendUsageData | Set to true to send anonymized, aggregated usage data to help improve the plugin. | boolean | Optional, defaults to `false`
  huntersColor | The color to give to the `hunters` team. | string | Optional
  runnersColor | The color to give to the `runners` team. | string | Optional
  spectatorsColor | The color to give to the `spectators` team. | string | Optional
  clearRunnerInvOnStart | Set to true to clear the runner's inventory and experience when the game starts. | boolean | Optional, defaults to `false`
  clearHunterInvOnStart | Set to true to clear the hunter's inventory and experience when the game starts. | boolean | Optional, defaults to `false`
  clearItemDropsOnStart | Set to true to clear item drops when the game starts. | boolean | Optional, defaults to `false`
  setTimeToZero | Set to true to set the time to 0 when the game starts. | boolean | Optional, defaults to `true`
  preGameWorldBorder | Set to true to enforce a world border for pre-game. | boolean | Optional, defaults to `false`
  preGameBorderSize | States how big the pre-game world border would be, if enabled. | int | Optional, defaults to `100`, ignored when *preGameWorldBorder* is `false`
  enableDiscord | Set to true to turn on Discord integration. Read below for more information. | boolean | Required
  discordToken | Enter the token of your Discord bot here. | string | Required if `enableDiscord` is `true`
  ip | The Discord status message portion. Will display as `Playing {value}` so it's recommended that you make this your server's IP. | string | Optional
  parseDiscordCommands | Set to true if you want music commands to be run by sending a message in Discord text channels. This allows anyone in your Discord server to run music commands, however. | Optional, defaults to `false`
  discordServerId | The ID of your Discord server that the bot is on. | string | Required if `enableDiscord` is `true`
  musicChannelId | The ID of the voice channel that the bot should play music on. | string | Required if `enableDiscord` is `true`
  hunterRoleId | The ID of the role to assign to Hunters. | string | Optional
  runnnerRoleId | The ID of the role to assign to Runners. | string | Optional
  spectatorRoleId | The ID of the role to assign to Spectators. | string | Optional
  uuid | Randomized id that is automatically assigned if `sendUsageData` is enabled. **Please do not touch this field.** | string | Do not set manually

> Note: if any of the role IDs are missing or invalid, no roles will be assigned.

## Permissions
Permission|Description|Recommended level
--|--|--
`minecraftmanhunt.hunter` | Allow `/hunter` command | everyone
`minecraftmanhunt.speedrunner` | Allow `/speedrunner` command | everyone
`minecraftmanhunt.spectator` | Allow `/spectator` command | everyone
`minecraftmanhunt.clearteams` | Allow `/clearteams` command | operators
`minecraftmanhunt.start` | Allow `/start` command | operators
`minecraftmanhunt.end` | Allow `/end` command | operators
`minecraftmanhunt.compass` | Allow `/compass` command | everyone
`minecraftmanhunt.music` | Allow `/music` command. Note that music commands can also be typed in Discord if `processDiscordCommands` in config is set to `true`. | everyone
`minecraftmanhunt.config` | Allow config-changing commands, such as `/setheadstart`. | operators

## Troubleshooting
- `online-mode` in server.properties must be set to `true` as of version 1.2.4.
- If you are using Discord integration, make sure your server's system clock is set correctly.
- Check the [FAQ Issues page](https://github.com/yummypasta/MinecraftManhunt/issues?q=label%3AFAQ), or report a problem on the [Issues tab](https://github.com/yummypasta/MinecraftManhunt/issues/new/choose)!

## Advanced: Developing
- This project uses Maven. To build, run the `package` script.
- Pull requests and Issues are welcome!

## Made With
- [Bukkit/Spigot plugin library](https://bukkit.org/)
- [JDA: Java Discord Library](https://github.com/DV8FromTheWorld/JDA)
- [LavaPlayer: Audio Player for Discord](https://github.com/sedmelluq/lavaplayer)
