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
import java.util.List;
import java.util.Map;

public class GUIManager implements IManager {

    private final Map<String, YamlConfiguration> guis = new HashMap<>();
    private final Map<String, List<String>> aliasesMap = new HashMap<>();
    private final WinzoriaMenu instance = WinzoriaMenu.getInstance();

    @Override
    public void register() {
        final String dataFolder = instance.getDataFolder().toString();
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        final String prefix = Messages.getPrefix();
        final FileConfiguration config = instance.getConfig();

        config.getConfigurationSection("menus").getKeys(false).forEach(key -> {
            String fileName = config.getString("menus." + key + ".file");
            List<String> aliases = config.getStringList("menus." + key + ".aliases");
            File file = new File(dataFolder + "/menus/" + fileName);

            if (!file.exists()) {
                console.sendMessage(prefix + Messages.transformColor("&cLe fichier menus/" + fileName + " n'existe pas."));
                return;
            }

            YamlConfiguration menu = YamlConfiguration.loadConfiguration(file);
            guis.put(key, menu);
            aliasesMap.put(key, aliases);
            registerCommand(key, aliases);
            console.sendMessage(prefix + Messages.transformColor("&8&oChargement du fichier: " + fileName));
        });
    }

    private void printMessage(ConsoleCommandSender console, String prefix) {
        console.sendMessage(Messages.transformColor("&8&m========== [ " + prefix + "&8&m] =========="));
    }

    private void registerCommand(String key, List<String> aliases) {
        WMCommand.registerCommand(key, (sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                Bukkit.getConsoleSender().sendMessage(Messages.transformColor("&cSeul un joueur peut faire cette commande."));
                return false;
            }

            Player player = (Player) sender;
            YamlConfiguration config = guis.get(key);
            CustomGUI gui = new CustomGUI(player, config);
            gui.create();
            return true;
        });

        for (String alias : aliases) {
            WMCommand.registerCommand(alias, (sender, command, label, args) -> {
                if (!(sender instanceof Player)) {
                    Bukkit.getConsoleSender().sendMessage(Messages.transformColor("&cSeul un joueur peut faire cette commande."));
                    return false;
                }

                Player player = (Player) sender;
                YamlConfiguration config = guis.get(key);
                CustomGUI gui = new CustomGUI(player, config);
                gui.create();
                return true;
            });
        }
    }

    public void unregisterCommands() {
        guis.keySet().forEach(key -> {
            WMCommand.unregisterCommand(key);
            aliasesMap.get(key).forEach(WMCommand::unregisterCommand);
        });
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
