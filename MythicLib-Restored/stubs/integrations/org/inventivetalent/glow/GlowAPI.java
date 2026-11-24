package org.inventivetalent.glow;
import java.util.Collection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
public class GlowAPI {
    public enum Color { BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, PURPLE, YELLOW, WHITE, NONE }
    public static void setGlowing(Entity entity, Color color, Collection<? extends Player> receivers) {}
}
