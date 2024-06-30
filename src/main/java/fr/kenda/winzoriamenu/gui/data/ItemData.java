package fr.kenda.winzoriamenu.gui.data;

import fr.kenda.winzoriamenu.utils.Messages;
import org.bukkit.Material;

import java.util.List;

public class ItemData {

    private final String name;
    private final Material material;
    private final List<Integer> slots;
    private final int uniqueSlot;
    private final int amount;
    private final List<String> rightClickCommands;
    private final List<String> leftClickCommands;
    private final List<String> lores;

    public ItemData(String name, Material material, List<Integer> slots, int amount, List<String> rightClickCommands, List<String> leftClickCommands, List<String> lores) {
        this.name = Messages.transformColor(name.replace("%prefix%", Messages.getPrefix()));
        this.material = material;
        this.slots = slots;
        this.uniqueSlot = -1;
        this.amount = amount;
        this.rightClickCommands = rightClickCommands;
        this.leftClickCommands = leftClickCommands;
        this.lores = lores;

        lores.replaceAll(s -> Messages.transformColor(s.replace("%prefix%", Messages.getPrefix())));
    }

    public ItemData(String name, Material material, int uniqueSlot, int amount, List<String> rightClickCommands, List<String> leftClickCommands, List<String> lores) {
        this.name = Messages.transformColor(name.replace("%prefix%", Messages.getPrefix()));
        this.material = material;
        this.slots = null;
        this.uniqueSlot = uniqueSlot;
        this.amount = amount;
        this.rightClickCommands = rightClickCommands;
        this.leftClickCommands = leftClickCommands;
        this.lores = lores;

        lores.replaceAll(s -> Messages.transformColor(s.replace("%prefix%", Messages.getPrefix())));
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
}
