package me.leothepro555.skills.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.OfflinePlayer;

public class SkillLevelUpEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public OfflinePlayer getPlayer() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
