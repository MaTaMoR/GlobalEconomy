package me.matamor.ge.spigot.vault;

import me.matamor.ge.shared.economy.EconomyEntry;
import me.matamor.ge.shared.identifier.exception.ExceptionReason;
import me.matamor.ge.shared.identifier.exception.IdentifierException;
import me.matamor.ge.spigot.SpigotEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VaultEconomy implements Economy {

    private final DecimalFormat FORMAT = new DecimalFormat("##.#");
    private final String currentBalanceAccount;

    private final SpigotEconomy plugin;

    public VaultEconomy(SpigotEconomy plugin, String currentBalanceAccount) {
        this.plugin = plugin;
        this.currentBalanceAccount = currentBalanceAccount;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "MineSoundEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return FORMAT.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public boolean hasAccount(String name) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    @Override
    public boolean hasAccount(String name, String worldName) {
        return hasAccount(name);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String name) {
        EconomyEntry economyEntry = getEntry(name);
        return (economyEntry == null ? 0 : economyEntry.getBalance(currentBalanceAccount));
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        EconomyEntry economyEntry = getEntry(offlinePlayer);
        return (economyEntry == null ? 0 : economyEntry.getBalance(currentBalanceAccount));
    }

    @Override
    public double getBalance(String name, String worldName) {
        return getBalance(name);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String worldName) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String name, double balance) {
        return getBalance(name) >= balance;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double balance) {
        return getBalance(offlinePlayer) >= balance;
    }

    @Override
    public boolean has(String name, String worldName, double balance) {
        return getBalance(name) >= balance;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String worldName, double balance) {
        return getBalance(offlinePlayer) >= balance;
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");

        EconomyEntry economyEntry = getEntry(name);
        if(economyEntry == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "User doesn't exist");
        } else {
            double balance = economyEntry.getBalance(currentBalanceAccount);
            economyEntry.addBalance(currentBalanceAccount, amount);

            return new EconomyResponse(balance, amount, ResponseType.SUCCESS, null);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");

        EconomyEntry economyEntry = getEntry(offlinePlayer);
        if(economyEntry == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "User is not online!");
        } else {
            double balance = economyEntry.getBalance(currentBalanceAccount);
            economyEntry.addBalance(currentBalanceAccount, amount);

            return new EconomyResponse(balance, amount, ResponseType.SUCCESS, null);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, String worldName, double amount) {
        return withdrawPlayer(name, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String name, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");

        EconomyEntry economyEntry = getEntry(name);
        if(economyEntry == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "User is not online");
        } else {
            double balance = economyEntry.getBalance(currentBalanceAccount);
            economyEntry.addBalance(currentBalanceAccount, amount);

            return new EconomyResponse(balance, amount, ResponseType.SUCCESS, null);
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");

        EconomyEntry economyEntry = getEntry(offlinePlayer);
        if(economyEntry == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "User is not online");
        } else {
            double balance = economyEntry.getBalance(currentBalanceAccount);
            economyEntry.addBalance(currentBalanceAccount, amount);

            return new EconomyResponse(balance, amount, ResponseType.SUCCESS, null);
        }
    }

    @Override
    public EconomyResponse depositPlayer(String name, String worldName, double amount) {
        return depositPlayer(name, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "MineSoundEconomy does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String name) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String name, String worldName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String worldName) {
        return true;
    }

    private EconomyEntry getEntry(String name) {
        try {
            return this.plugin.getEconomy().getEntry(this.plugin.getIdentifierManager().load(name));
        } catch (IdentifierException e) {
            if (e.getReason() == ExceptionReason.OTHER) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private EconomyEntry getEntry(OfflinePlayer offlinePlayer) {
        return this.plugin.getEconomy().getEntry(offlinePlayer.getUniqueId());
    }
}
