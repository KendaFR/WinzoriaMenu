package fr.kenda.winzoriamenu.gui.data;

import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemData {

    private final Player owner;
    private final String name;
    private final Material material;
    private final List<Integer> slots;
    private final int uniqueSlot;
    private final int amount;

    //optional
    private byte data;
    private List<String> rightClickCommands;
    private List<String> leftClickCommands;
    private List<String> lores;
    private List<String> enchantments;
    private HashMap<String, List<String>> requirements;

    public ItemData(Player owner, String name, Material material, int uniqueSlot, List<Integer> slots, int amount) {
        this.owner = owner;
        this.name = Messages.transformColor(name.replace("%prefix%", Messages.getPrefix()));
        this.material = material;
        this.slots = slots;
        this.uniqueSlot = uniqueSlot;
        this.amount = amount;
    }

    public void addRequirement(String type, String requirement) {
        if(requirements == null)
            requirements = new HashMap<>();

        if (requirements.get(type) != null)
            requirements.get(type).add(requirement);
        else {
            requirements.put(type, new ArrayList<>());
            requirements.get(type).add(requirement);
        }
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public List<String> getRightClickCommands() {
        return rightClickCommands;
    }

    public List<String> getLeftClickCommands() {
        return leftClickCommands;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getEnchantments() {
        return enchantments;
    }

    public List<String> getLores() {
        return lores;
    }

    public int getUniqueSlot() {
        return uniqueSlot;
    }

    public byte getData() {
        return data;
    }

    public boolean canShowItem() {
        if(requirements == null) return true;

        for (Map.Entry<String, List<String>> entry : requirements.entrySet()) {
            String type = entry.getKey();
            List<String> permissions = entry.getValue();

            switch (type) {
                case "!has permission":
                    if (permissions.stream().anyMatch(owner::hasPermission)) {
                        return false;
                    }
                    break;
                case "has permission":
                    if (permissions.stream().anyMatch(permission -> !owner.hasPermission(permission))) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    public void setData(final int data) {
        this.data = (byte) data;
    }

    public void setRightClickCommands(final List<String> rightClickCommands) {
        this.rightClickCommands = rightClickCommands;
    }

    public void setLeftClickCommands(final List<String> leftClickCommands) {
        this.leftClickCommands = leftClickCommands;
    }

    public void setLores(List<String> lores) {
        lores.replaceAll(s -> Messages.transformColor(s.replace("%prefix%", Messages.getPrefix())));
        this.lores = lores;
    }

    public void setEnchantments(final List<String> enchantments) {
        this.enchantments = enchantments;
    }
}
