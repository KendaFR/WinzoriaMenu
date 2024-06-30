package fr.kenda.winzoriamenu.managers;

import fr.kenda.winzoriamenu.commands.WinzoriaMenuCommand;
import org.bukkit.Bukkit;

public class CommandManager implements IManager {

    @Override
    public void register() {
        Bukkit.getPluginCommand("wm").setExecutor(new WinzoriaMenuCommand());


    }
}

