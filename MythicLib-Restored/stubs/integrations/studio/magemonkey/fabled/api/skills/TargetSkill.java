package studio.magemonkey.fabled.api.skills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
public interface TargetSkill extends Skill { boolean cast(Player player, LivingEntity target, int level, boolean ally); }
