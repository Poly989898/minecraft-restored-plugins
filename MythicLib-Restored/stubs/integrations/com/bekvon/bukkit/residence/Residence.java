package com.bekvon.bukkit.residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Location;
import org.bukkit.entity.Player;
public class Residence {
    public static Residence getInstance() { return null; }
    public Manager getResidenceManager() { return null; }
    public static class Manager {
        public ClaimedResidence getByLoc(Location loc) { return null; }
        public ClaimedResidence getByLoc(Player player) { return null; }
    }
}
