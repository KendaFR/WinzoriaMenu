package fr.kenda.winzoriamenu.managers;

import java.util.HashMap;

public class Manager implements IManager{

    private final HashMap<Class<?>, IManager> managers = new HashMap<>();

    @Override
    public final void register() {
        managers.put(FileManager.class, new FileManager());

        managers.forEach((aClass, iManager) -> iManager.register());
    }

    public <T> T getManager(Class<T> clazz) {
        return clazz.cast(managers.get(clazz));
    }
}
