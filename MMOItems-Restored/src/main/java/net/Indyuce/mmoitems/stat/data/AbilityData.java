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

   public AbilityData(@NotNull JsonObject var1) {
      super(MMOUtils.backwardsCompatibleTriggerType(var1.get("CastMode").getAsString()));
      this.ability = MMOItems.plugin.getSkills().getSkill(var1.get("Id").getAsString());
      JsonObject var2 = var1.getAsJsonObject("Modifiers");
      var2.entrySet().forEach(var1x -> this.setModifier((String)var1x.getKey(), ((JsonElement)var1x.getValue()).getAsDouble()));
   }

   public AbilityData(@NotNull ConfigurationSection var1) {
      super(MMOUtils.backwardsCompatibleTriggerType(UtilityMethods.enumName(Objects.requireNonNull(var1.getString("mode"), "Ability is missing mode"))));
      Validate.isTrue(var1.contains("type"), "Ability is missing type");
      String var2 = UtilityMethods.enumName(var1.getString("type"));
      Validate.isTrue(MMOItems.plugin.getSkills().hasSkill(var2), "Could not find ability called '" + var2 + "'");
      this.ability = MMOItems.plugin.getSkills().getSkill(var2);

      for (String var4 : var1.getKeys(false)) {
         if (!var4.equalsIgnoreCase("mode") && !var4.equalsIgnoreCase("type") && this.ability.getHandler().getModifiers().contains(var4)) {
            this.modifiers.put(var4, var1.getDouble(var4));
         }
      }
   }

   public AbilityData(RegisteredSkill var1, TriggerType var2) {
      super(var2);
      this.ability = var1;
   }

   public RegisteredSkill getAbility() {
      return this.ability;
   }

   public Set<String> getModifiers() {
      return this.modifiers.keySet();
   }

   public void setModifier(String var1, double var2) {
      this.modifiers.put(var1, var2);
   }

   public boolean hasModifier(String var1) {
      return this.modifiers.containsKey(var1);
   }

   public boolean getResult(SkillMetadata var1) {
      PlayerData var2 = PlayerData.get(var1.getCaster().getPlayer());
      RPGPlayer var3 = var2.getRPG();
      Player var4 = var1.getCaster().getPlayer();
      if (!var1.getCaster().getData().getCooldownMap().isOnCooldown(this)) {
         if (MMOItems.plugin.getConfig().getBoolean("permissions.abilities")
            && !var4.hasPermission("mmoitems.ability." + this.getHandler().getLowerCaseId())
            && !var4.hasPermission("mmoitems.bypass.ability")) {
            return false;
         } else if (this.hasModifier("mana") && var3.getMana() < var1.getParameter("mana")) {
            Message.NOT_ENOUGH_MANA.format(ChatColor.RED).send(var4);
            return false;
         } else if (this.hasModifier("stamina") && var3.getStamina() < var1.getParameter("stamina")) {
            Message.NOT_ENOUGH_STAMINA.format(ChatColor.RED).send(var4);
            return false;
         } else {
            return true;
         }
      } else {
         CooldownInfo var5 = var2.getMMOPlayerData().getCooldownMap().getInfo(this);
         if (!this.getTrigger().isSilent()) {
            StringBuilder var6 = new StringBuilder(ChatColor.YELLOW + "");
            double var7 = (double)(var5.getInitialCooldown() - var5.getRemaining()) / var5.getInitialCooldown() * 10.0;
            String var9 = MMOItems.plugin.getConfig().getString("cooldown-progress-bar-char");

            for (int var10 = 0; var10 < 10; var10++) {
               var6.append(var7 >= var10 ? ChatColor.GREEN : ChatColor.WHITE).append(var9);
            }

            Message.SPELL_ON_COOLDOWN
               .format(
                  ChatColor.RED,
                  "#left#",
                  MythicLib.plugin.getMMOConfig().decimal.format(var5.getRemaining() / 1000.0),
                  "#progress#",
                  var6.toString(),
                  "#s#",
                  var5.getRemaining() > 1999L ? "s" : ""
               )
               .send(var4);
         }

         return false;
      }
   }

   public void whenCast(SkillMetadata var1) {
      PlayerData var2 = PlayerData.get(var1.getCaster().getPlayer());
      RPGPlayer var3 = var2.getRPG();
      if (this.hasModifier("mana")) {
         var3.giveMana(-var1.getParameter("mana"));
      }

      if (this.hasModifier("stamina")) {
         var3.giveStamina(-var1.getParameter("stamina"));
      }

      double var4 = var1.getParameter("cooldown") * (1.0 - Math.min(0.8, var1.getCaster().getStat("COOLDOWN_REDUCTION") / 100.0));
      if (var4 > 0.0) {
         var1.getCaster().getData().getCooldownMap().applyCooldown(this, var4);
      }
   }

   public SkillHandler getHandler() {
      return this.ability.getHandler();
   }

   public double getParameter(String var1) {
      return this.modifiers.getOrDefault(var1, this.ability.getDefaultModifier(var1));
   }

   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("Id", this.ability.getHandler().getId());
      var1.addProperty("CastMode", this.getTrigger().name());
      JsonObject var2 = new JsonObject();
      this.modifiers.keySet().forEach(var2x -> var2.addProperty(var2x, this.getParameter(var2x)));
      var1.add("Modifiers", var2);
      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         AbilityData var2 = (AbilityData)var1;
         return this.ability.equals(var2.ability) && this.getTrigger().equals(var2.getTrigger()) && this.modifiers.equals(var2.modifiers);
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
