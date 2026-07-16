# PlayerTracer
<p align="center">
    <img src="https://github.com/Adrigamer2950/PlayerTracer/blob/master/logo_200x.jpg?raw=true" alt="Logo" />
</p>

Learn what are your players up to

> ⚠️ This plugin is still **in development**, so it's **not** recommended to use in production servers
> unless you know what are you doing. This means that **anything can change any time without
> warning**, which can lead to **data loss**, configuration overwriting, etc. Whatever happens during
> this phase, is **your responsibility**.

# Info
This plugin logs player actions to let server admins view past actions.
It also lets plugin developers registers their own logs, which can lead
to a more detailed list of actions a player is doing, and has done.

> ❗ You need Java 17+ to use this plugin. If you are using a lower version consider upgrading

> ❗ This plugin is **not** compatible with CraftBukkit, Bukkit or Spigot servers. Please use Paper or its forks

> ❗ This plugin has active support for Minecraft 1.17+. So, if you are using a lower version, you will not receive **any** support

# Features (check TODO.md for not implemented features)
- Store logs in a database (MySQL/MariaDB, SQLite, H2)
- Search logs by player name, action and date
- View logs in chat
- View logs in a nice GUI

# "Why should I use this plugin?"
This plugin is useful for server administrators / moderators who want to keep track of anything that a player does.
For example, if a player is being reported for being toxic in chat, you can retrieve their chat logs in order
to verify that information or just to collect evidence against them.

# Commands
- `/playertracer` - Main command. Has the following aliases: `/pt`, `/ptracer`, `/trace`, `/tracer`
- `/playertracer help` - Shows the help message
- `/playertracer search <query>` - Retrieves logs based on the query provided. Refer to the command's tab-completion to see the available options
- `/playertracer page <page>` - Switches between pages of the last logs retrieved with `/playertracer search`
- `/playertracer actionlist` - Lists all available actions that can be logged
- `/playertracer tp <world> <x> <y> <z>` - Teleports you to the location of the log that you clicked in chat after using `/playertracer search`
- `/playertracer viewmode <gui|chat>` - Switches between GUI and Chat view modes. Chat will be used by default
- `/playertracer purge` - Not implemented yet. Will be used to purge logs from the database by time, log type or player
- `/playertracer reload` - Reloads the plugin configuration and messages

# Permissions
- `playertracer.admin` - Grants access to all commands and features of the plugin
- `playertracer.search` - Grants access to the `/playertracer search` command
- `playertracer.teleport` - Grants access to the `/playertracer tp` command
- There's still some permissions yet to be implemented
