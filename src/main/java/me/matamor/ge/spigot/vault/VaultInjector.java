package me.matamor.ge.spigot.vault;

import me.matamor.ge.shared.utils.Validate;
import me.matamor.ge.spigot.SpigotEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

public class VaultInjector {

    private final SpigotEconomy spigotEconomy;

    public VaultInjector(SpigotEconomy spigotEconomy) {
        this.spigotEconomy = spigotEconomy;
    }

    public String getCurrentBalanceAccount() {
        return spigotEconomy.getConfig().getString("Configuration.CurrentBalanceAccount", "CurrentBalanceAccount");
    }

    public void inject() {
        Validate.isFalse(isInjected(), "VaultEconomy is already injected");
        spigotEconomy.getServer().getServicesManager().register(Economy.class, new VaultEconomy(spigotEconomy, getCurrentBalanceAccount()), spigotEconomy, ServicePriority.Highest);
    }

    public boolean isInjected() {
        RegisteredServiceProvider<Economy> serviceProvider = spigotEconomy.getServer().getServicesManager().getRegistration(Economy.class);
        return serviceProvider != null && serviceProvider.getProvider() != null && serviceProvider.getProvider() instanceof VaultEconomy;
    }
}
