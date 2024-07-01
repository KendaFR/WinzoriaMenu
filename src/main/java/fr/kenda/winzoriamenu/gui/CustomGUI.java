package fr.kenda.winzoriamenu.gui;

import fr.kenda.winzoriamenu.WinzoriaMenu;
import fr.kenda.winzoriamenu.gui.data.ItemData;
import fr.kenda.winzoriamenu.managers.GUIManager;
import fr.kenda.winzoriamenu.utils.ItemBuilder;
import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomGUI implements Listener {

    private final YamlConfiguration configuration;
    private final String title;
    private final int size;
    private final List<ItemData> itemData;
    private final Player owner;
    private Inventory inventory = null;

    public CustomGUI(Player owner, YamlConfiguration fileConfig) {
        itemData = new ArrayList<>();
        configuration = fileConfig;
        this.owner = owner;
        title = Messages.transformColor(fileConfig.getString("menu_title"));

        size = fileConfig.getInt("size") * 9;

        final ConfigurationSection items = fileConfig.getConfigurationSection("items");
        for (String key : items.getKeys(false)) {
            createItemData(key);
        }

        Bukkit.getPluginManager().registerEvents(this, WinzoriaMenu.getInstance());
    }

    private void createItemData(String key) {
        final Material material = Material.valueOf(configuration.getString("items." + key + ".material"));
        final int amount = configuration.getInt("items." + key + ".amount");
        final int data = configuration.getInt("items." + key + ".data");
        final List<Integer> slots = configuration.getIntegerList("items." + key + ".slots");
        final String displayName = configuration.getString("items." + key + ".display_name");
        final List<String> lores = configuration.getStringList("items." + key + ".lores");
        final List<String> right_click = configuration.getStringList("items." + key + ".right_click_commands");
        final List<String> left_click = configuration.getStringList("items." + key + ".left_click_commands");

        ItemData id;
        if (slots.isEmpty()) {
            final int slot = configuration.getInt("items." + key + ".slots");
            id = new ItemData(owner, displayName, material, slot, amount, data, right_click, left_click, lores);
        } else
            id = new ItemData(owner, displayName, material, slots, amount, data, right_click, left_click, lores);

        final ConfigurationSection cs = configuration.getConfigurationSection("items." + key + ".view_requirement.requirements");
        if (cs != null) {
            for (String csKey : cs.getKeys(false)) {
                String type = "items." + key + ".view_requirement.requirements." + csKey + ".type";
                String requirement = "items." + key + ".view_requirement.requirements." + csKey + ".requirement";
                id.addRequirement(configuration.getString(type), configuration.getString(requirement));
            }
        }
        if (id.canShowItem())
            itemData.add(id);
    }

    public void create() {
        inventory = Bukkit.createInventory(owner, size, title);
        owner.openInventory(inventory);

        openMenu();
    }

    public void openMenu() {
        inventory.setContents(menu());
    }

    private ItemStack[] menu() {
        ItemStack[] content = new ItemStack[size];

        for (ItemData item : itemData) {
            List<String> lores = item.getLores().stream()
                    .map(s -> Messages.transformColor(s.replace("%player%", owner.getName())))
                    .collect(Collectors.toList());

            ItemBuilder itemBuilder = new ItemBuilder(item.getMaterial(), item.getAmount(), item.getData())
                    .setName(item.getName())
                    .setLore(lores);

            if (item.getSlots() == null) {
                content[item.getUniqueSlot()] = itemBuilder.toItemStack();
            } else {
                for (int slot : item.getSlots()) {
                    content[slot] = itemBuilder.toItemStack();
                }
            }
        }
        return content;
    }


    public void close() {
        owner.closeInventory();
    }

    private ItemData getItemFromMaterial(Material mat) {
        for (ItemData data : itemData) {
            if (data.getMaterial() == mat) return data;
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        ItemStack current = e.getCurrentItem();

        if (current == null) return;

        if (!(e.getInventory().equals(this.inventory) && e.getRawSlot() < this.inventory.getSize())) {
            return;
        }

        e.setCancelled(true);

        Material mat = current.getType();
        ItemData item = getItemFromMaterial(mat);
        ClickType clickType = e.getClick();
        if (clickType == ClickType.RIGHT || clickType == ClickType.LEFT)
            performAction(item, clickType);

    }

    private void performAction(ItemData data, ClickType clickType) {
        List<String> commands = null;

        switch (clickType) {
            case RIGHT:
                commands = data.getRightClickCommands();
                break;
            case LEFT:
                commands = data.getLeftClickCommands();
                break;
        }

        if (commands != null) {
            for (String command : commands) {
                executeCommand(command);
            }
        }
    }


    private void executeCommand(String command) {
        String prefix = command.split(" ")[0];
        String cmd = "";
        if (command.contains(" "))
            cmd = command.substring(command.indexOf(" ") + 1).replace("%player%", owner.getName());

        switch (prefix) {
            case "[close]":
                close();
                break;
            case "[console]":
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                break;
            case "[player]":
                Bukkit.dispatchCommand(owner, cmd);
                break;
            case "[openguimenu]":
                close();
                CustomGUI gui = new CustomGUI(owner, WinzoriaMenu.getInstance().getManager(GUIManager.class).getGuis().get(cmd));
                gui.create();
                break;
        }
    }
}
