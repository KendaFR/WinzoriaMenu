package fr.kenda.winzoriamenu;

import fr.kenda.winzoriamenu.managers.FileManager;
import fr.kenda.winzoriamenu.managers.Manager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WinzoriaMenu extends JavaPlugin {

    private static WinzoriaMenu instance;
    private Manager manager;

    @Override
    public void onEnable() {
        instance = this;
        manager = new Manager();
        manager.register();
    }

    @Override
    public void onDisable() {

    }

    public static WinzoriaMenu getInstance() {
        return instance;
    }

    public <T> T getManager(Class<T> clazz) {
        return clazz.cast(manager.getManager(clazz));
    }
}
