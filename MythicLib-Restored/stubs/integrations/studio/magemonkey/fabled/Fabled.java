package studio.magemonkey.fabled;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.Skill;
public class Fabled {
    public static Skill getSkill(String name) { return null; }
    public static PlayerData getData(Player player) { return null; }
    public static Settings getSettings() { return null; }
    public static class Settings { public boolean canAttack(Player player, LivingEntity target) { return true; } }
}
