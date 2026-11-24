package us.eunoians.mcrpg.api.events.mcrpg;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.eunoians.mcrpg.players.McRPGPlayer;

public class McRPGPlayerLevelChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public McRPGPlayer getMcRPGPlayer() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
