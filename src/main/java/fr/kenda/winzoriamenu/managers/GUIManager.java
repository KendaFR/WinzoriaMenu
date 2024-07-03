package fr.kenda.winzoriamenu.managers;

import fr.kenda.winzoriamenu.WinzoriaMenu;
import fr.kenda.winzoriamenu.commands.WMCommand;
import fr.kenda.winzoriamenu.gui.CustomGUI;
import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GUIManager implements IManager {

    private final Map<String, YamlConfiguration> guis = new HashMap<>();
    private final WinzoriaMenu instance = WinzoriaMenu.getInstance();

    @Override
    public void register() {
        final String dataFolder = instance.getDataFolder().toString();
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        final String prefix = Messages.getPrefix();
        final FileConfiguration config = instance.getConfig();

        //printMessage(console, prefix);

        config.getConfigurationSection("menus").getKeys(false).forEach(key -> {
            String fileName = config.getString("menus." + key);
            File file = new File(dataFolder + "/menus/" + fileName);

            if (!file.exists()) {
                console.sendMessage(prefix + Messages.transformColor("&cLe fichier menus/" + fileName + " n'existe pas."));
                return;
            }

            YamlConfiguration menu = YamlConfiguration.loadConfiguration(file);
            guis.put(key, menu);
            registerCommand(key);
            console.sendMessage(prefix + Messages.transformColor("&8&oChargement du fichier: " + fileName));
        });
    }

    private void printMessage(ConsoleCommandSender console, String prefix) {
        console.sendMessage(Messages.transformColor("&8&m========== [ " + prefix + "&8&m] =========="));
    }

    private void registerCommand(String cmd) {
        WMCommand.registerCommand(cmd, (sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                return false;
            }

            Player player = (Player) sender;
            YamlConfiguration config = guis.get(cmd);
            CustomGUI gui = new CustomGUI(player, config);
            gui.create();
            return true;
        });
    }

    public void unregisterCommands() {
        guis.keySet().forEach(WMCommand::unregisterCommand);
    }

    public void reloadGUI() {
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        final String prefix = Messages.getPrefix();

        printMessage(console, prefix);
        unregisterCommands();
        guis.clear();
        instance.reloadConfig();
        register();
        printMessage(console, prefix);
    }

    public Map<String, YamlConfiguration> getGuis() {
        return guis;
    }

    public boolean isMenuExist(String key) {
        return guis.containsKey(key);
    }
}
