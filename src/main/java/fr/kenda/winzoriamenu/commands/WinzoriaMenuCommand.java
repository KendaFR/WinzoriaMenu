package fr.kenda.winzoriamenu.commands;

import fr.kenda.winzoriamenu.WinzoriaMenu;
import fr.kenda.winzoriamenu.gui.CustomGUI;
import fr.kenda.winzoriamenu.managers.GUIManager;
import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class WinzoriaMenuCommand implements CommandExecutor {

    private final GUIManager guiManager;

    public WinzoriaMenuCommand() {
        this.guiManager = WinzoriaMenu.getInstance().getManager(GUIManager.class);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(Messages.transformColor("&8&m========== [ " + Messages.getPrefix() + "&8&m] =========="));
            commandSender.sendMessage(Messages.transformColor("&cVersion: " + WinzoriaMenu.getInstance().getDescription().getVersion()));
            commandSender.sendMessage(Messages.transformColor("&cAuteur: " + WinzoriaMenu.getInstance().getDescription().getAuthors()));
            commandSender.sendMessage(Messages.transformColor("&8&m========== [ " + Messages.getPrefix() + "&8&m] =========="));
            return true;
        }

        if (args.length > 3) {
            sendHelp(commandSender);
            return true;
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("open")) {
                String menu = args[1];
                String target = args[2];

                if (!guiManager.isMenuExist(menu)) {
                    commandSender.sendMessage(Messages.transformColor("&cLe menu " + menu + " n'existe pas."));
                    return false;
                }
                Player t = Bukkit.getPlayer(target);
                if (t == null) {
                    commandSender.sendMessage(Messages.transformColor("&cLe joueur " + target + " n'est pas connecté."));
                    return false;
                }

                return true;
            }
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "list":
                sendList(commandSender);
                return true;
            case "help":
                sendHelp(commandSender);
                return true;
            case "reload":
                guiManager.reloadGUI();
                commandSender.sendMessage(Messages.getPrefix() + Messages.transformColor("&a" + guiManager.getGuis().size() + " GUIs chargés."));
                return true;
            default:
                if (guiManager.isMenuExist(action)) {
                    openMenu(commandSender, action);
                    return true;
                }
                return false;
        }
    }

    private void sendList(CommandSender sender) {
        sender.sendMessage(Messages.transformColor("&8&m========== [ " + Messages.getPrefix() + "&8&m] =========="));
        for (String guiName : guiManager.getGuis().keySet()) {
            sender.sendMessage(Messages.transformColor("&e" + guiName));
        }
        sender.sendMessage(Messages.transformColor("&8&m========== [ " + Messages.getPrefix() + "&8&m] =========="));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Messages.transformColor("&8&m========== [ " + Messages.getPrefix() + "&8&m] =========="));
        sender.sendMessage(Messages.transformColor("&c/wm help: &7Afficher la liste d'aide"));
        sender.sendMessage(Messages.transformColor("&c/wm list: &7Afficher la liste des menus"));
        sender.sendMessage(Messages.transformColor("&c/wm <menu>: &7Ouvrir le menu"));
        sender.sendMessage(Messages.transformColor("&c/wm open <menu> <player>: &7Ouvrir le menu pour un joueur"));
        sender.sendMessage(Messages.transformColor("&8&m========== [ " + Messages.getPrefix() + "&8&m] =========="));
    }

    private void openMenu(CommandSender sender, String menuName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return;
        }
        Player p = (Player) sender;
        YamlConfiguration config = guiManager.getGuis().get(menuName);
        CustomGUI gui = new CustomGUI(p, config);
        gui.create();
    }
}
