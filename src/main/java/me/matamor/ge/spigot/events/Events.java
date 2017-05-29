package me.matamor.ge.spigot.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.matamor.ge.shared.EconomyPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.logging.Level;

@AllArgsConstructor
public class Events implements Listener {

    @Getter
    private final EconomyPlugin plugin;

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        try {
            this.plugin.getEconomy().load(player.getUniqueId());
        } catch (SQLException e) {
            event.disallow(Result.KICK_OTHER, "Couldn't load your Economy!");
            this.plugin.getLogger().log(Level.SEVERE, "There was an error while loading Economy data!", e);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.plugin.getEconomy().unload(player.getUniqueId());
    }
}
