package net.Indyuce.mmoitems.api;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemsAPI {
   private final JavaPlugin plugin;

   public MMOItemsAPI(JavaPlugin javaPlugin) {
      this.plugin = javaPlugin;
   }

   public void registerSkill(RegisteredSkill registeredSkill) {
      MMOItems.plugin.getSkills().registerSkill(registeredSkill);
   }

   public RegisteredSkill getSkillById(String text) {
      return Objects.requireNonNull(MMOItems.plugin.getSkills().getSkill(text), "Could not find skill with ID '" + text + "'");
   }

   public PlayerData getPlayerData(Player player) {
      return PlayerData.get(player);
   }

   public RPGPlayer getRPGInfo(Player player) {
      return PlayerData.get(player).getRPG();
   }

   public SkillResult castSkill(
      Player player, RegisteredSkill registeredSkill, @NotNull Map<String, Double> map, @Nullable LivingEntity entity, @Nullable AttackMetadata attackMetadata
   ) {
      AbilityData abilityData = new AbilityData(registeredSkill, TriggerType.CAST);
      map.forEach((modifier, value) -> abilityData.setModifier(modifier, value));
      PlayerMetadata playerMetadata = MMOPlayerData.get(player).getStatMap().cache(EquipmentSlot.MAIN_HAND);
      return abilityData.cast(new TriggerMetadata(playerMetadata, entity, attackMetadata));
   }
}
