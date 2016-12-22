package me.matamor.ge.shared.identifier;

import me.matamor.ge.shared.EconomyPlugin;
import me.matamor.ge.shared.identifier.exception.IdentifierException;
import me.matamor.ge.shared.utils.Callback;

import java.util.UUID;

public interface IdentifierManager {

    EconomyPlugin getPlugin();

    UUID load(String name) throws IdentifierException;

    void load(String name, Callback<UUID> callback) throws IdentifierException;

    UUID getUUID(String name);

    void insert(String name, UUID uuid);

    void unload(String name);

    void unloadAll();

}
