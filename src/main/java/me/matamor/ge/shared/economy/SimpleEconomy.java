package me.matamor.ge.shared.economy;

import lombok.Getter;
import me.matamor.ge.shared.EconomyPlugin;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SimpleEconomy implements Economy {

    private final Map<UUID, EconomyEntry> entries = new ConcurrentHashMap<>();

    @Getter
    private final EconomyPlugin plugin;

    @Getter
    private final double limit;

    public SimpleEconomy(final EconomyPlugin plugin, double limit) {
        this.plugin = plugin;
        this.limit = limit;
    }

    @Override
    public EconomyEntry load(UUID uuid) throws SQLException {
        EconomyEntry economyEntry = this.entries.get(uuid);

        if (economyEntry == null) {
            economyEntry = this.plugin.getEconomyDatabase().load(uuid);

            if (economyEntry == null) {
                economyEntry = new SimpleEconomyEntry(this, uuid);
            }

            this.entries.put(uuid, economyEntry);
        }

        return economyEntry;
    }

    @Override
    public EconomyEntry getEntry(UUID uuid) {
        return this.entries.get(uuid);
    }

    @Override
    public void remove(UUID uuid) {
        this.entries.remove(uuid);
    }

    @Override
    public void unload(UUID uuid) {
        EconomyEntry economyEntry = this.entries.remove(uuid);
        if (economyEntry != null) {
            try {
                this.plugin.getEconomyDatabase().save(economyEntry);
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "There was an error while saving Economy data!", e);
            }
        }
    }

    @Override
    public void unloadAll() {
        this.entries.values().forEach(e -> {
            try {
                this.plugin.getEconomyDatabase().save(e);
            } catch (SQLException e1) {
                this.plugin.getLogger().log(Level.SEVERE, "There was an error while saving Economy data!", e);
            }
        });

        this.entries.clear();
    }
}
