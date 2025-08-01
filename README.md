# PlayerTracer
<p align="center">
    <img src="https://github.com/Adrigamer2950/PlayerTracer/blob/master/logo_200x.jpg?raw=true" alt="Logo" />
</p>

Learn what are your players up to

> [!WARNING]
> This plugin is still **in development**, so it's **not** recommended to use in production servers
> unless you know what are you doing. This means that **anything can change any time without
> warning**, which can lead to **data loss**, configuration overwriting, etc. Whatever happens during
> this phase, is **your responsibility**.

# Info
This plugin logs player actions to let server admins view past actions. 
It also lets plugin developers registers their own logs, which can lead
to a more detailed list of actions a player is doing, and has done.

> [!CAUTION]
> You need Java 17+ to use this plugin. If you are using a lower version consider upgrading

> [!CAUTION]
> This plugin is **not** compatible with CraftBukkit, Bukkit or Spigot servers. Please use Paper or its forks

> [!CAUTION]
> This plugin has active support for Minecraft 1.17+. So, if you are using a lower version, you will not receive **any** support

# TODOs
- [x] Store logs in a database (MySQL/MariaDB, SQLite, H2)
- [ ] In-Game Log viewer (GUI & Command)
- [ ] API
- [ ] Modules (extensions which could be used to add more logs
  without the needing of a separate plugin. Similar to how PlaceholderAPI works)
