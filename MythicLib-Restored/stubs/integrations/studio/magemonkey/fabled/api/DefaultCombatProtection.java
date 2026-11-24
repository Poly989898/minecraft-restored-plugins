package studio.magemonkey.fabled.api;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
public class DefaultCombatProtection { public static boolean isFakeDamageEvent(EntityDamageByEntityEvent event) { return false; } }
