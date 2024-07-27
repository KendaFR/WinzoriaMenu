package fr.kenda.winzoriamenu;

import fr.kenda.winzoriamenu.managers.GUIManager;
import fr.kenda.winzoriamenu.managers.Manager;
import fr.kenda.winzoriamenu.managers.PluginVersionning;
import fr.kenda.winzoriamenu.utils.ClickableText;
import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.logging.Level;

public final class WinzoriaMenu extends JavaPlugin {


    private static WinzoriaMenu instance;
    private Manager manager;

    public static WinzoriaMenu getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getConsoleSender().sendMessage(Messages.getPrefix() + Messages.transformColor("&aPlugin démarré avec succès"));

        saveDefaultConfig();

        manager = new Manager();
        manager.register();

        GUIManager guiManager = manager.getManager(GUIManager.class);
        if (guiManager.getGuis().isEmpty())
            guiManager.reloadGUI();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
            getLogger().log(Level.SEVERE, "Le plugin placeholderapi n'as pas été trouver. Certaines fonctionnalités pourraient entraîner des erreurs.");

        new BukkitRunnable() {

            @Override
            public void run() {
                refreshUpdate();
            }

        }.runTaskTimer(this, 0L, 24 * 60 * 60 * 20L);
    }

    @Override
    public void onDisable() {
        manager.getManager(GUIManager.class).unregisterCommands();
    }

    public <T> T getManager(Class<T> clazz) {
        return clazz.cast(manager.getManager(clazz));
    }

    public void refreshUpdate() {
        //check de release
        try {
            Bukkit.getConsoleSender().sendMessage(Messages.getPrefix() + "§aRecherche de mise à jour...");
            PluginVersionning pluginVersionning = new PluginVersionning();
            if (!pluginVersionning.isLatestVersion()) {
                String prefix = Messages.getPrefix();
                String newVersionMessage = prefix + Messages.transformColor("&aUne nouvelle version du plugin est disponible.");
                String url = "https://github.com/KendaFR/WinzoriaMenu/releases";
                String clickableText = Messages.transformColor("&7&o" + url);

                Bukkit.getConsoleSender().sendMessage(newVersionMessage);
                Bukkit.getConsoleSender().sendMessage(prefix + "&a" + url);

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (player.isOp()) {
                        player.sendMessage(newVersionMessage);
                        ClickableText.makeTextOpenLink(clickableText, "Ouvrir le lien", url, player);
                    }
                }
            } else
                Bukkit.getConsoleSender().sendMessage(Messages.getPrefix() + "§aAucune nouvelle version trouvé.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
