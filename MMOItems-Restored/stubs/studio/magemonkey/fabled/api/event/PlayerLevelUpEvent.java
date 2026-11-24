package studio.magemonkey.fabled.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import studio.magemonkey.fabled.api.player.PlayerData;

public class PlayerLevelUpEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public PlayerData getPlayerData() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
