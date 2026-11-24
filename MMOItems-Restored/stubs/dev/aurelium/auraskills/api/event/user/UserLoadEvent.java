package dev.aurelium.auraskills.api.event.user;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import dev.aurelium.auraskills.api.user.SkillsUser;

public class UserLoadEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public Player getPlayer() { return null; }
    public SkillsUser getUser() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
