package com.alessiodp.parties.api.events.bukkit.player;

import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BukkitPartiesPlayerPostJoinEvent extends Event {
    public Party getParty() { throw new UnsupportedOperationException("compile-only stub"); }
    public PartyPlayer getPartyPlayer() { throw new UnsupportedOperationException("compile-only stub"); }
    @Override public HandlerList getHandlers() { throw new UnsupportedOperationException("compile-only stub"); }
    public static HandlerList getHandlerList() { throw new UnsupportedOperationException("compile-only stub"); }
}
