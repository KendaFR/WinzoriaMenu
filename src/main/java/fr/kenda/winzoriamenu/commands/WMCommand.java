package fr.kenda.winzoriamenu.commands;

import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Map;

public class WMCommand extends Command {
    private final CommandExecutor executor;

    public WMCommand(String name, CommandExecutor executor) {
        super(name);
        this.executor = executor;
    }

    public static void registerCommand(String name, CommandExecutor executor) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(name, new WMCommand(name, executor));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getConsoleSender().sendMessage(Messages.transformColor("&cUne erreur s'est produite lors de la création de la commande."));
        }
    }

    public static void unregisterCommand(String name) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            knownCommands.remove(name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getConsoleSender().sendMessage(Messages.transformColor("&cUne erreur s'est produite lors de la suppression de la commande."));
        }
    }


    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return this.executor.onCommand(sender, this, commandLabel, args);
    }
}
