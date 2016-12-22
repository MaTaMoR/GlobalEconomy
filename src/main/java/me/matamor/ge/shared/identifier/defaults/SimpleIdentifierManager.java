package me.matamor.ge.shared.identifier.defaults;

import com.google.common.cache.*;
import me.matamor.ge.shared.EconomyPlugin;
import me.matamor.ge.shared.identifier.IdentifierManager;
import me.matamor.ge.shared.identifier.exception.ExceptionReason;
import me.matamor.ge.shared.identifier.exception.IdentifierException;
import me.matamor.ge.shared.utils.Callback;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SimpleIdentifierManager implements IdentifierManager {

    private final EconomyPlugin plugin;
    private final LoadingCache<String, UUID> entries;

    public SimpleIdentifierManager(final EconomyPlugin plugin) {
        this.plugin = plugin;
        this.entries = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, UUID>() {
                @Override
                public void onRemoval(RemovalNotification<String, UUID> notification) {
                    if (notification.getCause() == RemovalCause.EXPLICIT) {
                        //I specifically save it like this because this will be the cause when invalidating all the keys on disable
                        //So i can't register and async task when the plugin is disabled

                        try {
                            plugin.getIdentifierDatabase().save(notification.getKey(), notification.getValue());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if (notification.getCause() == RemovalCause.REPLACED) {
                        //If it was replaced then remove the actual entry from the database, because if it was replaced it means the plugin found a newer UUID
                        //Or a different UUID for that Name

                        plugin.getIdentifierDatabase().removeAsync(notification.getKey(), notification.getValue(), null);
                    }
                }
            }).build(new CacheLoader<String, UUID>() {
                @Override
                public UUID load(String name) throws Exception {
                    UUID uuid = plugin.getIdentifierDatabase().load(name);
                    if (uuid == null) {
                        throw new IdentifierException(ExceptionReason.NOT_FOUND, "No UUID found with the Name " + name);
                    }

                    return uuid;
                }
            });
    }

    @Override
    public EconomyPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public UUID load(String name) throws IdentifierException {
        try {
            return this.entries.get(name);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof IdentifierException) {
                throw (IdentifierException) e.getCause();
            } else {
                throw new IdentifierException(ExceptionReason.OTHER, "There was an exception while loading the Identifier", e);
            }
        }
    }

    @Override
    public void load(final String name, final Callback<UUID> callback) {
        this.plugin.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.done(load(name), null);
                } catch (IdentifierException e) {
                    callback.done(null, e);
                }
            }
        });
    }

    @Override
    public UUID getUUID(String name) {
        return this.entries.getIfPresent(name);
    }

    @Override
    public void insert(String name, UUID uuid) {
        UUID actualUUID = this.entries.getIfPresent(name);
        if (actualUUID != null && actualUUID.equals(uuid)) return; //UUID already exists

        this.entries.put(name, uuid);
        if (actualUUID != null) { //The actual UUID will be replaced and removed from the database, save this one
            this.plugin.getIdentifierDatabase().saveAsync(name, uuid, null);
        }
    }

    @Override
    public void unload(String name) {
        this.entries.invalidate(name);
    }

    @Override
    public void unloadAll() {
        this.entries.invalidateAll();
    }
}
