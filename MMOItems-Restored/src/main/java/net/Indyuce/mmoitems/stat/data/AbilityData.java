package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.player.cooldown.CooldownInfo;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AbilityData extends Skill {
   private final RegisteredSkill ability;
   @NotNull
   private final Map<String, Double> modifiers = new HashMap<>();

   public AbilityData(@NotNull JsonObject jsonObject) {
      super(MMOUtils.backwardsCompatibleTriggerType(jsonObject.get("CastMode").getAsString()));
      this.ability = MMOItems.plugin.getSkills().getSkill(jsonObject.get("Id").getAsString());
      JsonObject asJsonObject = jsonObject.getAsJsonObject("Modifiers");
      asJsonObject.entrySet().forEach(var1x -> this.setModifier((String)var1x.getKey(), ((JsonElement)var1x.getValue()).getAsDouble()));
   }

   public AbilityData(@NotNull ConfigurationSection section) {
      super(MMOUtils.backwardsCompatibleTriggerType(UtilityMethods.enumName(Objects.requireNonNull(section.getString("mode"), "Ability is missing mode"))));
      Validate.isTrue(section.contains("type"), "Ability is missing type");
      String text = UtilityMethods.enumName(section.getString("type"));
      Validate.isTrue(MMOItems.plugin.getSkills().hasSkill(text), "Could not find ability called '" + text + "'");
      this.ability = MMOItems.plugin.getSkills().getSkill(text);

      for (String key : section.getKeys(false)) {
         if (!key.equalsIgnoreCase("mode") && !key.equalsIgnoreCase("type") && this.ability.getHandler().getModifiers().contains(key)) {
            this.modifiers.put(key, section.getDouble(key));
         }
      }
   }

   public AbilityData(RegisteredSkill registeredSkill, TriggerType triggerType) {
      super(triggerType);
      this.ability = registeredSkill;
   }

   public RegisteredSkill getAbility() {
      return this.ability;
   }

   public Set<String> getModifiers() {
      return this.modifiers.keySet();
   }

   public void setModifier(String text, double value) {
      this.modifiers.put(text, value);
   }

   public boolean hasModifier(String text) {
      return this.modifiers.containsKey(text);
   }

   public boolean getResult(SkillMetadata skillMetadata) {
      PlayerData playerData = PlayerData.get(skillMetadata.getCaster().getPlayer());
      RPGPlayer rPG = playerData.getRPG();
      Player player = skillMetadata.getCaster().getPlayer();
      if (!skillMetadata.getCaster().getData().getCooldownMap().isOnCooldown(this)) {
         if (MMOItems.plugin.getConfig().getBoolean("permissions.abilities")
            && !player.hasPermission("mmoitems.ability." + this.getHandler().getLowerCaseId())
            && !player.hasPermission("mmoitems.bypass.ability")) {
            return false;
         } else if (this.hasModifier("mana") && rPG.getMana() < skillMetadata.getParameter("mana")) {
            Message.NOT_ENOUGH_MANA.format(ChatColor.RED).send(player);
            return false;
         } else if (this.hasModifier("stamina") && rPG.getStamina() < skillMetadata.getParameter("stamina")) {
            Message.NOT_ENOUGH_STAMINA.format(ChatColor.RED).send(player);
            return false;
         } else {
            return true;
         }
      } else {
         CooldownInfo info = playerData.getMMOPlayerData().getCooldownMap().getInfo(this);
         if (!this.getTrigger().isSilent()) {
            StringBuilder builder = new StringBuilder(ChatColor.YELLOW + "");
            double value = (double)(info.getInitialCooldown() - info.getRemaining()) / info.getInitialCooldown() * 10.0;
            String text = MMOItems.plugin.getConfig().getString("cooldown-progress-bar-char");

            for (int i = 0; i < 10; i++) {
               builder.append(value >= i ? ChatColor.GREEN : ChatColor.WHITE).append(text);
            }

            Message.SPELL_ON_COOLDOWN
               .format(
                  ChatColor.RED,
                  "#left#",
                  MythicLib.plugin.getMMOConfig().decimal.format(info.getRemaining() / 1000.0),
                  "#progress#",
                  builder.toString(),
                  "#s#",
                  info.getRemaining() > 1999L ? "s" : ""
               )
               .send(player);
         }

         return false;
      }
   }

   public void whenCast(SkillMetadata skillMetadata) {
      PlayerData playerData = PlayerData.get(skillMetadata.getCaster().getPlayer());
      RPGPlayer rPG = playerData.getRPG();
      if (this.hasModifier("mana")) {
         rPG.giveMana(-skillMetadata.getParameter("mana"));
      }

      if (this.hasModifier("stamina")) {
         rPG.giveStamina(-skillMetadata.getParameter("stamina"));
      }

      double value = skillMetadata.getParameter("cooldown") * (1.0 - Math.min(0.8, skillMetadata.getCaster().getStat("COOLDOWN_REDUCTION") / 100.0));
      if (value > 0.0) {
         skillMetadata.getCaster().getData().getCooldownMap().applyCooldown(this, value);
      }
   }

   public SkillHandler getHandler() {
      return this.ability.getHandler();
   }

   public double getParameter(String text) {
      return this.modifiers.getOrDefault(text, this.ability.getDefaultModifier(text));
   }

   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("Id", this.ability.getHandler().getId());
      jsonObject.addProperty("CastMode", this.getTrigger().name());
      JsonObject modifiersJson = new JsonObject();
      this.modifiers.keySet().forEach(var2x -> modifiersJson.addProperty(var2x, this.getParameter(var2x)));
      jsonObject.add("Modifiers", modifiersJson);
      return jsonObject;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         AbilityData abilityData = (AbilityData)obj;
         return this.ability.equals(abilityData.ability) && this.getTrigger().equals(abilityData.getTrigger()) && this.modifiers.equals(abilityData.modifiers);
      } else {
         return false;
      }
   }

   public String toString() {
      return "AbilityData{ability=" + this.ability + ", modifiers=" + this.modifiers + '}';
   }

   public int hashCode() {
      return Objects.hash(this.ability, this.modifiers);
   }
}
