package fr.kenda.winzoriamenu.utils;

import fr.kenda.winzoriamenu.WinzoriaMenu;
import fr.kenda.winzoriamenu.managers.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Messages {
    private static final WinzoriaMenu instance = WinzoriaMenu.getInstance();

    /**
     * Get prefix of plugin
     *
     * @return String
     */
    public static String getPrefix() {
        return transformColor(instance.getConfig().getString("prefix") + " ");
    }

    /**
     * get message from Message file
     *
     * @param path String
     * @param args String...
     * @return String
     */
    public static String getMessage(String path, String... args) {
        FileConfiguration config = instance.getManager(FileManager.class).getConfigFrom("messages");
        if (config == null)
            return transformColor("&c[Messages] File messages doesn't exist. Relaunch or restore file before execute this command.");


        String message = config.getString(path);
        if (message == null) return "[Messages] Path '" + path + "' not found in messages.yml";

        int size = args.length - 1;
        for (int i = 0; i < size; i += 2)
            message = message.replace(args[i], args[i + 1]);

        return transformColor(message);

    }

    /**
     * Transform message with color
     *
     * @param message String
     * @return String
     */
    public static String transformColor(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    public static List<String> getMessageList(String path) {
        FileConfiguration config = instance.getManager(FileManager.class).getConfigFrom("messages");
        List<String> list = config.getStringList(path);
        List<String> listColored = new ArrayList<>();
        for (String str : list)
            listColored.add(transformColor(str));
        return listColored;
    }
}
