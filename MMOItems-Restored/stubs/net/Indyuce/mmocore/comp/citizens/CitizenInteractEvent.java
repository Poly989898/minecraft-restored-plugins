package net.Indyuce.mmocore.comp.citizens;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import net.citizensnpcs.api.npc.NPC;

public class CitizenInteractEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public Player getPlayer() { return null; }
    public NPC getNPC() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
