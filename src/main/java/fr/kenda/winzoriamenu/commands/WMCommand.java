package fr.kenda.winzoriamenu.commands;

import com.avaje.ebeaninternal.server.core.Message;
import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;

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

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return this.executor.onCommand(sender, this, commandLabel, args);
    }
}
