package me.matamor.ge.shared.economy;

import com.google.common.cache.*;
import me.matamor.ge.shared.EconomyPlugin;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleEconomy implements Economy {

    private final LoadingCache<UUID, EconomyEntry> entries;

    private final EconomyPlugin plugin;
    private final double limit;

    public SimpleEconomy(final EconomyPlugin plugin, double limit) {
        this.plugin = plugin;
        this.limit = limit;
        this.entries = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<UUID, EconomyEntry>() {
                @Override
                public void onRemoval(RemovalNotification<UUID, EconomyEntry> notification) {
                    if (notification.getCause() == RemovalCause.EXPLICIT) {
                        try {
                            plugin.getEconomyDatabase().save(notification.getValue());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).build(new CacheLoader<UUID, EconomyEntry>() {
                @Override
                public EconomyEntry load(UUID key) throws Exception {
                    EconomyEntry economyEntry = plugin.getEconomyDatabase().load(key);
                    return (economyEntry == null ? new SimpleEconomyEntry(plugin.getEconomy(), key) : economyEntry);
                }
            });
    }

    @Override
    public EconomyPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public double getLimit() {
        return limit;
    }

    @Override
    public EconomyEntry load(UUID uuid) {
        return this.entries.getUnchecked(uuid);
    }

    @Override
    public EconomyEntry getEntry(UUID uuid) {
        return this.entries.getIfPresent(uuid);
    }

    @Override
    public void remove(UUID uuid) {
        this.entries.invalidate(uuid);
    }

    @Override
    public void unload(UUID uuid) {
        this.entries.invalidate(uuid);
    }

    @Override
    public void unloadAll() {
        this.entries.invalidateAll();
    }
}
