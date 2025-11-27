package com.customitems.core.handler.interactable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InteractableRegistry {

    private static final Map<String, InteractableHandler> handlers = new ConcurrentHashMap<>();

    static {
        register("test", new InteractableHandler(
                (player, item) -> {
                    player.sendMessage("Left click!");
                    return;
                },
                (player, item) -> {
                    player.sendMessage("Right click!");
                    return;
                }
        ));
    }

    public static void register(String id, InteractableHandler handler) {
        handlers.put(id, handler);
    }

    public static InteractableHandler getHandler(String id) {
        return handlers.get(id);
    }
}
