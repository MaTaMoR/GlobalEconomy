package me.matamor.ge.shared.economy;

import me.matamor.ge.shared.EconomyPlugin;

import java.sql.SQLException;
import java.util.UUID;

public interface Economy {

    EconomyPlugin getPlugin();

    double getLimit();

    EconomyEntry load(UUID uuid) throws SQLException;

    EconomyEntry getEntry(UUID uuid);

    void remove(UUID uuid);

    void unload(UUID uuid);

    void unloadAll();

}
