package fr.kenda.winzoriamenu.gui.data;

import fr.kenda.winzoriamenu.utils.Messages;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemData {

    private final Player owner;
    private final Material material;
    private final List<Integer> slots;
    private final int uniqueSlot;
    private final int amount;

    //optional
    private String name;
    private String denyLeftClickMessage;
    private String denyRightClickMessage;
    private byte data;
    private List<String> rightClickCommands;
    private List<String> leftClickCommands;
    private List<String> lores;
    private List<String> enchantments;
    private HashMap<String, List<String>> requirements;
    private HashMap<String, List<String>> clickRequirements;

    public ItemData(Player owner, Material material, int uniqueSlot, List<Integer> slots, int amount) {
        this.owner = owner;
        this.material = material;
        this.slots = slots;
        this.uniqueSlot = uniqueSlot;
        this.amount = amount;
    }

    public void addRequirement(String type, String requirement) {
        if (requirement == null) return;

        if (requirements == null)
            requirements = new HashMap<>();

        String req = requirement;
        if (PlaceholderAPI.containsPlaceholders(req))
            req = PlaceholderAPI.setPlaceholders(owner, req);
        requirements.computeIfAbsent(type, k -> new ArrayList<>()).add(req);
    }

    public void addClickRequirement(String click, String requirement) {
        if (clickRequirements == null)
            clickRequirements = new HashMap<>();

        clickRequirements.computeIfAbsent(click, k -> new ArrayList<>()).add(PlaceholderAPI.setPlaceholders(owner, requirement));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty())
            this.name = null;
        else
            this.name = PlaceholderAPI.setPlaceholders(owner, Messages.transformColor(name.replace("{prefix}", Messages.getPrefix())));
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

    public void setRightClickCommands(final List<String> rightClickCommands) {
        this.rightClickCommands = rightClickCommands;
    }

    public List<String> getLeftClickCommands() {
        return leftClickCommands;
    }

    public void setLeftClickCommands(final List<String> leftClickCommands) {
        this.leftClickCommands = leftClickCommands;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(final List<String> enchantments) {
        this.enchantments = enchantments;
    }

    public List<String> getLores() {
        return lores;
    }

    public void setLores(List<String> lores) {
        lores.replaceAll(s -> PlaceholderAPI.setPlaceholders(owner, Messages.transformColor(s.replace("{prefix}", Messages.getPrefix()))));
        this.lores = lores;
    }

    public int getUniqueSlot() {
        return uniqueSlot;
    }

    public byte getData() {
        return data;
    }

    public void setData(final int data) {
        this.data = (byte) data;
    }

    public boolean canShowItem() {
        return requirements == null || requirements.entrySet().stream().noneMatch(this::invalidRequirement);
    }

    private boolean invalidRequirement(Map.Entry<String, List<String>> entry) {
        String type = entry.getKey().trim();
        List<String> requirement = entry.getValue();
        return ("!has permission".equals(type) && requirement.stream().anyMatch(r -> owner.hasPermission(r.trim()))) ||
                ("has permission".equals(type) && requirement.stream().noneMatch(r -> owner.hasPermission(r.trim())));
    }

    private boolean invalidClickRequirement(Map.Entry<String, List<String>> entry) {
        String key = entry.getKey().trim();
        if ("condition".equals(key)) {
            for (String require : entry.getValue()) {
                String[] splited = require.split("([!=<>]{1,2})");
                String operator = require.replaceAll("[^!=<>]", "").trim();
                String leftStr = splited[0].trim();
                String rightStr = splited[1].trim();

                if (isNumeric(leftStr) && isNumeric(rightStr)) {
                    float left = Float.parseFloat(leftStr);
                    float right = Float.parseFloat(rightStr);
                    if (!compareNumbers(left, right, operator)) {
                        return true;
                    }
                } else if (!compareStrings(leftStr, rightStr, operator)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compareNumbers(float left, float right, String operator) {
        switch (operator) {
            case ">=":
                return left >= right;
            case "<=":
                return left <= right;
            case ">":
                return left > right;
            case "<":
                return left < right;
            case "==":
                return left == right;
            case "!=":
                return left != right;
        }
        return false;
    }

    private boolean compareStrings(String leftStr, String rightStr, String operator) {
        switch (operator) {
            case "==":
                return leftStr.equalsIgnoreCase(rightStr);
            case "!=":
                return !leftStr.equalsIgnoreCase(rightStr);
        }
        return false;
    }

    private boolean isNumeric(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean canClick() {
        if (clickRequirements != null && !clickRequirements.isEmpty())
            return clickRequirements.entrySet().stream().noneMatch(this::invalidClickRequirement);
        return true;
    }

    public String getDenyLeftClickMessage() {
        return denyLeftClickMessage;
    }

    public void setDenyLeftClickMessage(String denyLeftClickMessage) {
        this.denyLeftClickMessage = denyLeftClickMessage;
    }

    public String getDenyRightClickMessage() {
        return denyRightClickMessage;
    }

    public void setDenyRightClickMessage(String denyRightClickMessage) {
        this.denyRightClickMessage = denyRightClickMessage;
    }
}
