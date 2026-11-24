package studio.magemonkey.fabled.api.event;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
public class SkillDamageEvent extends Event {
    public LivingEntity getDamager() { return null; }
    public LivingEntity getTarget() { return null; }
    public double getDamage() { return 0; }
    public HandlerList getHandlers() { return null; }
    public static HandlerList getHandlerList() { return null; }
}
