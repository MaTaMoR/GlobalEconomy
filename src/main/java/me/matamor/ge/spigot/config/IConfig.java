package me.matamor.ge.spigot.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class IConfig extends YamlConfiguration {

    private final Plugin parent;
    private String fileName;
    private File file;

    public IConfig(Plugin plugin, File file) {
        this(plugin, file.getName(), file);
    }

    public IConfig(Plugin plugin, String fileName) {
        this(plugin, fileName, new File(plugin.getDataFolder(), fileName));
    }

    public IConfig(Plugin plugin, String fileName, String subFolder) {
        this(plugin, fileName, new File(plugin.getDataFolder() + File.separator + subFolder, fileName));
    }

    private IConfig(Plugin parent, String fileName, File file) {
        this.parent = parent;
        this.fileName = fileName;
        this.file = file;

        create();
    }

    public Plugin getPlugin() {
        return parent;
    }

    @Override
    public String getName() {
        return fileName;
    }

    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            parent.getLogger().log(Level.SEVERE, "Error saving config file " + fileName + "!", e);
        }
    }

    public void reload() {
        create();
    }

    private void create() {
        try {
            if (file.exists()) {
                load(file);
            } else {
                if (parent.getResource(fileName) != null) {
                    load(new InputStreamReader(parent.getResource(fileName)));
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            parent.getLogger().log(Level.SEVERE, "Error creating config file " + fileName + "!", e);
        }
    }

    public boolean exists() {
        return file.exists();
    }

    public boolean delete() {
        return exists() && file.delete();
    }

    public String getColored(String path) {
        String text = getString(path);
        return text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
    }

    public void setLocation(String path, Location location) {
        ConfigurationSection section = getConfigurationSection(path);
        if (section == null) section = createSection(path);

        setLocation(section, location);
    }

    public void setLocation(ConfigurationSection section, Location location) {
        if (section == null) return;

        section.set(section + ".world", location.getWorld());
        section.set(section + ".x", location.getX());
        section.set(section + ".y", location.getY());
        section.set(section + ".z", location.getZ());
        section.set(section + ".yaw", location.getYaw());
        section.set(section + ".pitch", location.getPitch());
    }

    public Location getLocation(String path) {
        ConfigurationSection section = getConfigurationSection(path);
        if (section == null) section = createSection(path);

        return getLocation(section);
    }

    public Location getLocation(ConfigurationSection section) {
        if (section == null) return null;

        return new Location(Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                section.getInt("yaw"),
                section.getInt("pitch"));
    }
}
