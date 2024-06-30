package fr.kenda.winzoriamenu.managers;

import java.util.LinkedHashMap;
import java.util.Map;

public class Manager implements IManager {

    private final Map<Class<?>, IManager> managers = new LinkedHashMap<>();

    @Override
    public final void register() {
        managers.put(FileManager.class, new FileManager());
        managers.put(GUIManager.class, new GUIManager());
        managers.put(CommandManager.class, new CommandManager());

        managers.forEach((aClass, iManager) -> iManager.register());
    }

    public <T> T getManager(Class<T> clazz) {
        return clazz.cast(managers.get(clazz));
    }
}
