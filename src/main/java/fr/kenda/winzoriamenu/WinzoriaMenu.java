package fr.kenda.winzoriamenu;

import fr.kenda.winzoriamenu.managers.GUIManager;
import fr.kenda.winzoriamenu.managers.Manager;
import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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

        if (manager.getManager(GUIManager.class).getGuis().isEmpty())
            Bukkit.getConsoleSender().sendMessage(Messages.getPrefix() + Messages.transformColor("&cLes GUI ont été créés, mais pas chargés. Merci de faire /wm reload pour actualiser la liste."));


        //check de release
    }

    @Override
    public void onDisable() {

    }

    public <T> T getManager(Class<T> clazz) {
        return clazz.cast(manager.getManager(clazz));
    }
}
