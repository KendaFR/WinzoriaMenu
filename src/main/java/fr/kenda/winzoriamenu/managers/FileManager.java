package fr.kenda.winzoriamenu.managers;

import fr.kenda.winzoriamenu.WinzoriaMenu;
import fr.kenda.winzoriamenu.utils.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class FileManager implements IManager {

    private final WinzoriaMenu instance = WinzoriaMenu.getInstance();
    private final HashMap<String, FileConfiguration> files = new HashMap<>();

    /**
     * Register and create all files
     */
    @Override
    public void register() {
        createFile("messages");
        if (Config.getBoolean("generate_test_file"))
            createFile("menus/test");

    }


    /**
     * Create file
     *
     * @param fileName String
     */
    public void createFile(String fileName) {
        final File file = new File(instance.getDataFolder(), fileName + ".yml");
        if (!file.exists()) {
            instance.saveResource(fileName + ".yml", false);
        }
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
        files.put(fileName, configFile);
    }


    /**
     * Get configuration from file
     *
     * @param fileName String
     * @return FileConfiguration
     */
    public FileConfiguration getConfigFrom(String fileName) {
        return files.get(fileName);
    }
}
