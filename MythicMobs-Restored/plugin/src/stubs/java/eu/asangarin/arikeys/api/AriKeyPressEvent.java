package eu.asangarin.arikeys.api;

public class AriKeyPressEvent extends org.bukkit.event.Event {
    private static final org.bukkit.event.HandlerList HANDLERS = new org.bukkit.event.HandlerList();
    public org.bukkit.event.HandlerList getHandlers() { return HANDLERS; }
    public static org.bukkit.event.HandlerList getHandlerList() { return HANDLERS; }
    public org.bukkit.NamespacedKey getId() { return null; }
    public org.bukkit.entity.Player getPlayer() { return null; }
}
