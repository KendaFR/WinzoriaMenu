package fr.kenda.winzoriamenu.managers;

import fr.kenda.winzoriamenu.WinzoriaMenu;
import fr.kenda.winzoriamenu.gui.CustomGUI;
import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class GUIManager implements IManager {

    private final HashMap<String, CustomGUI> guis = new HashMap<>();
    private final WinzoriaMenu instance = WinzoriaMenu.getInstance();

    @Override
    public void register() {
        final String dataFolder = instance.getDataFolder().toString();
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        final String prefix = Messages.getPrefix();
        final FileConfiguration config = instance.getConfig();

        console.sendMessage(Messages.transformColor("&8&m========== [ " + Messages.getPrefix() + "&8&m] =========="));

        for (String key : config.getConfigurationSection("menus").getKeys(false)) {

            String fileName = config.getString("menus." + key);
            File file = new File(dataFolder + "/menus/" + fileName);

            if (!file.exists()) {
                console.sendMessage(prefix + Messages.transformColor("&cLe fichier menus/" + fileName + " n'existe pas."));
                continue;
            }

            YamlConfiguration menu = YamlConfiguration.loadConfiguration(file);
            guis.put(key, new CustomGUI(menu));
            console.sendMessage(prefix + Messages.transformColor("&8&oChargement du fichier: " + fileName));
        }
        console.sendMessage(Messages.transformColor("&8&m========== [ " + Messages.getPrefix() + "&8&m] =========="));
    }

    public void reloadGUI() {
        guis.clear();
        instance.reloadConfig();
        register();
    }

    public HashMap<String, CustomGUI> getGuis() {
        return guis;
    }

    public boolean isMenuExist(String key) {
        return guis.get(key) != null;
    }
}
