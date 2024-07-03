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
import org.bukkit.enchantments.Enchantment;
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
import java.util.function.Consumer;
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
        String itemKey = "items." + key + ".";

        final String displayName = configuration.getString(itemKey + "display_name");
        final Material material = Material.valueOf(configuration.getString(itemKey + "material"));
        final int slot = configuration.getInt(itemKey + "slots");
        final List<Integer> slots = configuration.getIntegerList(itemKey + "slots");
        final int amount = configuration.getInt(itemKey + "amount");

        ItemData id = slots.isEmpty() ?
                new ItemData(owner, displayName, material, slot, null, amount)
                :
                new ItemData(owner, displayName, material, -1, slots, amount);

        final int data = configuration.getInt(itemKey + "data");
        if (data != 0)
            id.setData(data);

        setListIfNotEmpty(id::setLores, itemKey + "lores");
        setListIfNotEmpty(id::setEnchantments, itemKey + "enchantments");
        setListIfNotEmpty(id::setRightClickCommands, itemKey + "right_click_commands");
        setListIfNotEmpty(id::setLeftClickCommands, itemKey + "left_click_commands");

        final ConfigurationSection cs = configuration.getConfigurationSection(itemKey + "view_requirement.requirements");
        if (cs != null) {
            for (String csKey : cs.getKeys(false)) {
                String requirementKey = itemKey + "view_requirement.requirements." + csKey + ".";
                id.addRequirement(configuration.getString(requirementKey + "type"), configuration.getString(requirementKey + "requirement"));
            }
        }
        if (id.canShowItem())
            itemData.add(id);
    }

    private void setListIfNotEmpty(Consumer<List<String>> setter, String key) {
        final List<String> list = configuration.getStringList(key);
        if (!list.isEmpty())
            setter.accept(list);
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
            ItemBuilder itemBuilder;
            itemBuilder = item.getData() != 0 ?
                new ItemBuilder(item.getMaterial(), item.getAmount(), item.getData())
                    .setName(item.getName())
            :
                new ItemBuilder(item.getMaterial(), item.getAmount())
                        .setName(item.getName());

            if(item.getLores() != null) {
                List<String> lores = item.getLores().stream()
                        .map(s -> Messages.transformColor(s.replace("%player%", owner.getName())))
                        .collect(Collectors.toList());
                        itemBuilder.setLore(lores);
            }

            if(item.getEnchantments() != null) {
                for (String enchant : item.getEnchantments()) {
                    String[] enchantValue = enchant.split(";");
                    try {
                        Enchantment ench = Enchantment.getByName(enchantValue[0]);
                        itemBuilder.addUnsafeEnchantment(ench, Integer.parseInt(enchantValue[1]));
                    } catch (NullPointerException | NumberFormatException e) {
                        throw new RuntimeException("Une erreur s'est produite lors de la création de l'item " + item.getName() + ". L'enchantement ou la valeur n'est pas correct(e).", e);
                    }
                }
            }

            ItemStack itemStack = itemBuilder.toItemStack();
            if (item.getSlots() == null) {
                final int slot = item.getUniqueSlot();
                if(slot > size)
                    continue;
                content[slot] = itemStack;
            } else {
                for (int slot : item.getSlots()) {
                    if(slot > size) continue;
                    content[slot] = itemStack;
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
