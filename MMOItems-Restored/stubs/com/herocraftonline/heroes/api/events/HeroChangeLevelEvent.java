package com.herocraftonline.heroes.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.herocraftonline.heroes.characters.Hero;

public class HeroChangeLevelEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public Hero getHero() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
