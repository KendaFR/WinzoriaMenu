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
    private final byte data;
    private final List<String> rightClickCommands;
    private final List<String> leftClickCommands;
    private final List<String> lores;
    private final HashMap<String, List<String>> requirements;

    public ItemData(Player owner, String name, Material material, List<Integer> slots, int amount, int data, List<String> rightClickCommands, List<String> leftClickCommands, List<String> lores) {
        this.name = Messages.transformColor(name.replace("%prefix%", Messages.getPrefix()));
        this.material = material;
        this.slots = slots;
        this.uniqueSlot = -1;
        this.amount = amount;
        this.data = (byte) data;
        this.rightClickCommands = rightClickCommands;
        this.leftClickCommands = leftClickCommands;
        this.lores = lores;
        this.requirements = new HashMap<>();
        this.owner = owner;

        lores.replaceAll(s -> Messages.transformColor(s.replace("%prefix%", Messages.getPrefix())));
    }

    public ItemData(Player owner, String name, Material material, int uniqueSlot, int amount, int data, List<String> rightClickCommands, List<String> leftClickCommands, List<String> lores) {
        this.name = Messages.transformColor(name.replace("%prefix%", Messages.getPrefix()));
        this.material = material;
        this.slots = null;
        this.uniqueSlot = uniqueSlot;
        this.data = (byte) data;
        this.amount = amount;
        this.rightClickCommands = rightClickCommands;
        this.leftClickCommands = leftClickCommands;
        this.lores = lores;
        this.requirements = new HashMap<>();
        this.owner = owner;

        lores.replaceAll(s -> Messages.transformColor(s.replace("%prefix%", Messages.getPrefix())));
    }

    public void addRequirement(String type, String requirement) {
        if (this.requirements.get(type) != null)
            this.requirements.get(type).add(requirement);
        else {
            this.requirements.put(type, new ArrayList<>());
            this.requirements.get(type).add(requirement);
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

}
