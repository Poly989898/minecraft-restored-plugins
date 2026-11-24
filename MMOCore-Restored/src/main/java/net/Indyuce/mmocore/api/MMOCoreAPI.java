package net.Indyuce.mmocore.api;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.SkillResult;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.guild.GuildModule;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.PartyModule;
import net.Indyuce.mmocore.skill.CastableSkill;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MMOCoreAPI {
   private final JavaPlugin plugin;

   public MMOCoreAPI(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   public PlayerData getPlayerData(OfflinePlayer player) {
      return PlayerData.get(player);
   }

   public boolean isInSameParty(@NotNull Player player, @NotNull Player other) {
      AbstractParty party = MMOCore.plugin.partyModule.getParty(PlayerData.get(player));
      return party != null && party.hasMember(other);
   }

   public SkillResult cast(PlayerData playerData, String skillId, int level) {
      return this.cast(playerData, MMOCore.plugin.skillManager.getSkillOrThrow(skillId), level);
   }

   public SkillResult cast(PlayerData playerData, ClassSkill skill) {
      PlayerMetadata casterMeta = playerData.getMMOPlayerData().getStatMap().cache(EquipmentSlot.MAIN_HAND);
      TriggerMetadata triggerMeta = new TriggerMetadata(casterMeta, null, null);
      return new CastableSkill(skill, playerData).cast(triggerMeta);
   }

   public SkillResult cast(PlayerData playerData, RegisteredSkill skill, int level) {
      PlayerMetadata casterMeta = playerData.getMMOPlayerData().getStatMap().cache(EquipmentSlot.MAIN_HAND);
      TriggerMetadata triggerMeta = new TriggerMetadata(casterMeta, null, null);
      return new CastableSkill(new ClassSkill(skill, 0, 0), level).cast(triggerMeta);
   }

   public SkillResult cast(PlayerData playerData, SkillHandler<?> skill, int level) {
      PlayerMetadata casterMeta = playerData.getMMOPlayerData().getStatMap().cache(EquipmentSlot.MAIN_HAND);
      TriggerMetadata triggerMeta = new TriggerMetadata(casterMeta, null, null);
      RegisteredSkill registered = Objects.requireNonNull(
         MMOCore.plugin.skillManager.getSkill(skill.getId()), "Could not find registered skill with such handler"
      );
      return new CastableSkill(new ClassSkill(registered, 0, 0), level).cast(triggerMeta);
   }

   public void setPartyModule(@NotNull PartyModule module) {
      Validate.notNull(module, "Party module cannot be null");
      PartyModule current = MMOCore.plugin.partyModule;
      if (current != null && current instanceof Listener) {
         HandlerList.unregisterAll((Listener)MMOCore.plugin.partyModule);
      }

      MMOCore.plugin.partyModule = module;
   }

   public void setGuildModule(@NotNull GuildModule module) {
      Validate.notNull(module, "Guild module cannot be null");
      GuildModule current = MMOCore.plugin.guildModule;
      if (current != null && current instanceof Listener) {
         HandlerList.unregisterAll((Listener)current);
      }

      MMOCore.plugin.guildModule = module;
   }
}
