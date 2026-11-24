package com.Zrips.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
public class CMI {
    public static CMI getInstance() { return null; }
    public HologramManager getHologramManager() { return null; }
    public static class HologramManager {
        public void addHologram(CMIHologram holo) {}
        public void removeHolo(CMIHologram holo) {}
    }
}
