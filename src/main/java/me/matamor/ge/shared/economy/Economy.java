package me.matamor.ge.shared.economy;

import me.matamor.ge.shared.EconomyPlugin;

import java.util.UUID;

public interface Economy {

    EconomyPlugin getPlugin();

    double getLimit();

    EconomyEntry load(UUID uuid);

    EconomyEntry getEntry(UUID uuid);

    void remove(UUID uuid);

    void unload(UUID uuid);

    void unloadAll();

}
