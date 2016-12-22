package me.matamor.ge.spigot.events;

import me.matamor.ge.shared.EconomyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class Events implements Listener {

    private final EconomyPlugin plugin;

    public Events(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLign(AsyncPlayerPreLoginEvent event) {
        this.plugin.getIdentifierManager().insert(event.getName(), event.getUniqueId());


    }
}
