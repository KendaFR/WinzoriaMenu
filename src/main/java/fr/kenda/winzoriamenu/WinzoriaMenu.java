package fr.kenda.winzoriamenu;

import fr.kenda.winzoriamenu.managers.GUIManager;
import fr.kenda.winzoriamenu.managers.Manager;
import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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

        /*if (manager.getManager(GUIManager.class).getGuis().isEmpty())
            Bukkit.getConsoleSender().sendMessage(Messages.getPrefix() + Messages.transformColor("&cLes GUI ont été créés, mais pas chargés. Merci de faire /wm reload pour actualiser la liste."));
        */

        GUIManager guiManager = manager.getManager(GUIManager.class);
        if (guiManager.getGuis().isEmpty())
            guiManager.reloadGUI();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
            getLogger().log(Level.SEVERE, "Le plugin placeholderapi n'as pas été trouver. Certaines fonctionnalités pourraient entraîner des erreurs.");

        //check de release
    }

    @Override
    public void onDisable() {
        manager.getManager(GUIManager.class).unregisterCommands();
    }

    public <T> T getManager(Class<T> clazz) {
        return clazz.cast(manager.getManager(clazz));
    }
}
