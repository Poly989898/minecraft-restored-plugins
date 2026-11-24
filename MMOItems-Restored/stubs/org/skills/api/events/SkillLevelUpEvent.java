package org.skills.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

public class SkillLevelUpEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public Player getPlayer() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
