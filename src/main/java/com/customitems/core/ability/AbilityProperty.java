package com.customitems.core.ability;

import com.customitems.core.ItemPlugin;
import com.customitems.core.property.*;
import com.customitems.core.service.Services;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbilityProperty extends AbstractProperty implements EventListener, LoreContributor {

    public static final PropertyType<AbilityProperty> TYPE = PropertyType.of(AbilityProperty.class, "ability")
            .json(json -> {
                AbilityRegistry registry = Services.get(AbilityRegistry.class);
                Bukkit.getLogger().severe(json.toString());
                Set<Ability> abilities = new HashSet<>();
                JsonArray array = json.getAsJsonArray();
                for(JsonElement element : array) {
                    String abilityType = element.getAsString();
                    Ability ability = registry.getAbility(abilityType);
                    if(ability == null) {
                        Bukkit.getLogger().warning("Ability " + abilityType + " not found");
                        continue;
                    }
                    abilities.add(ability);
                    //registry method
                }
                return new AbilityProperty(abilities);
            }).build();

    private final Set<Ability> abilities;
    private final Set<Class<? extends Event>> events;

    public AbilityProperty(Set<Ability> abilities) {
        this.abilities = abilities;
        events = new HashSet<>();
        abilities.forEach(ability -> {
            if(ability instanceof EventAbility eventAbility) {
                events.addAll(eventAbility.getEvents());
            }
        });
    }

    @Override
    public void handle(Event event) {
        if(!events.contains(event.getClass())) return;
        Player player = null;
        if(event instanceof PlayerEvent playerEvent) {
            player = playerEvent.getPlayer();
        }
        for(Ability ability : abilities) {
            AbilityContext context = AbilityContext.of(player, getItem(), event, ability);
            if(ability instanceof EventAbility eventAbility) {
                eventAbility.trigger(context);
            }
        }
    }

    @Override
    public Set<Class<? extends Event>> getEvents() {
        return events;
    }

    @Override
    public PropertyType<? extends Property> getType() {
        return TYPE;
    }

    @Override
    public PropertyPriority getPriority() {
        return PropertyPriority.FINAL;
    }

    @Override
    public int getLorePriority() {
        return 20;
    }

    @Override
    public void contributeLore(LoreVisitor visitor) {
        for(Ability ability : abilities) {
            visitor.visit("&cAbility: " + ability.getType());
        }
    }
}
