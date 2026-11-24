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

   public MMOItemsAPI(JavaPlugin var1) {
      this.plugin = var1;
   }

   public void registerSkill(RegisteredSkill var1) {
      MMOItems.plugin.getSkills().registerSkill(var1);
   }

   public RegisteredSkill getSkillById(String var1) {
      return Objects.requireNonNull(MMOItems.plugin.getSkills().getSkill(var1), "Could not find skill with ID '" + var1 + "'");
   }

   public PlayerData getPlayerData(Player var1) {
      return PlayerData.get(var1);
   }

   public RPGPlayer getRPGInfo(Player var1) {
      return PlayerData.get(var1).getRPG();
   }

   public SkillResult castSkill(
      Player var1, RegisteredSkill var2, @NotNull Map<String, Double> var3, @Nullable LivingEntity var4, @Nullable AttackMetadata var5
   ) {
      AbilityData var6 = new AbilityData(var2, TriggerType.CAST);
      var3.forEach((var1x, var2x) -> var6.setModifier(var1x, var2x));
      PlayerMetadata var7 = MMOPlayerData.get(var1).getStatMap().cache(EquipmentSlot.MAIN_HAND);
      return var6.cast(new TriggerMetadata(var7, var4, var5));
   }
}
