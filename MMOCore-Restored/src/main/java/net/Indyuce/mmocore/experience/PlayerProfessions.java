package net.Indyuce.mmocore.experience;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.version.VParticle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.api.event.PlayerExperienceGainEvent;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.loot.chest.particle.SmallParticleEffect;
import net.Indyuce.mmocore.party.AbstractParty;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerProfessions {
   private final Map<String, Double> exp = new HashMap<>();
   private final Map<String, Integer> level = new HashMap<>();
   private final PlayerData playerData;

   public PlayerProfessions(PlayerData playerData) {
      this.playerData = playerData;
   }

   public PlayerProfessions load(ConfigurationSection config) {
      for (String key : config.getKeys(false)) {
         if (MMOCore.plugin.professionManager.has(key)) {
            this.exp.put(key, config.getDouble(key + ".exp"));
            this.level.put(key, config.getInt(key + ".level"));
         }
      }

      if (config.contains("times-claimed")) {
         for (String key : config.getConfigurationSection("times-claimed").getKeys(true)) {
            this.playerData.getItemClaims().put("profession." + key, config.getInt("times-claimed." + key));
         }
      }

      return this;
   }

   public void save(ConfigurationSection config) {
      for (String id : this.exp.keySet()) {
         config.set(id + ".exp", this.exp.get(id));
      }

      for (String id : this.level.keySet()) {
         config.set(id + ".level", this.level.get(id));
      }
   }

   public String toJsonString() {
      JsonObject json = new JsonObject();

      for (Profession profession : MMOCore.plugin.professionManager.getAll()) {
         JsonObject object = new JsonObject();
         object.addProperty("exp", this.getExperience(profession));
         object.addProperty("level", this.getLevel(profession));
         json.add(profession.getId(), object);
      }

      return json.toString();
   }

   public void load(String json) {
      JsonObject obj = (JsonObject)MythicLib.plugin.getGson().fromJson(json, JsonObject.class);

      for (Entry<String, JsonElement> entry : obj.entrySet()) {
         if (MMOCore.plugin.professionManager.has(entry.getKey())) {
            JsonObject value = entry.getValue().getAsJsonObject();
            this.exp.put(entry.getKey(), value.get("exp").getAsDouble());
            this.level.put(entry.getKey(), value.get("level").getAsInt());
         }
      }

      if (obj.has("timesClaimed")) {
         for (Entry<String, JsonElement> entry : obj.getAsJsonObject("timesClaimed").entrySet()) {
            this.playerData.getItemClaims().put("profession." + entry.getKey(), entry.getValue().getAsInt());
         }
      }
   }

   public PlayerData getPlayerData() {
      return this.playerData;
   }

   public int getLevel(String profession) {
      return Math.max(1, this.level.getOrDefault(profession, 1));
   }

   public int getLevel(Profession profession) {
      return this.getLevel(profession.getId());
   }

   public double getExperience(String id) {
      return this.exp.getOrDefault(id, 0.0);
   }

   public double getExperience(Profession profession) {
      return this.getExperience(profession.getId());
   }

   public long getLevelUpExperience(Profession profession) {
      return profession.getExpCurve().getExperience(this.getLevel(profession) + 1);
   }

   public long getLevelUpExperience(String id) {
      return MMOCore.plugin.professionManager.has(id) ? MMOCore.plugin.professionManager.get(id).getExpCurve().getExperience(this.getLevel(id) + 1) : 0L;
   }

   public void setLevel(Profession profession, int value) {
      this.level.put(profession.getId(), value);
   }

   public void takeLevels(Profession profession, int value) {
      int current = this.level.getOrDefault(profession.getId(), 1);
      this.level.put(profession.getId(), Math.max(1, current - value));
   }

   public void setExperience(Profession profession, double value) {
      this.exp.put(profession.getId(), value);
   }

   public void giveLevels(Profession profession, int value, EXPSource source) {
      long equivalentExp = 0L;
      int level = this.getLevel(profession);

      while (value-- > 0) {
         equivalentExp += profession.getExpCurve().getExperience(level + value + 1);
      }

      this.giveExperience(profession, equivalentExp, source);
   }

   public boolean hasReachedMaxLevel(Profession profession) {
      return profession.hasMaxLevel() && this.getLevel(profession) >= profession.getMaxLevel();
   }

   public void giveExperience(@NotNull Profession profession, double value, @NotNull EXPSource source) {
      this.giveExperience(profession, value, source, null, true);
   }

   public void giveExperience(@NotNull Profession profession, double value, @NotNull EXPSource source, @Nullable Location hologramLocation, boolean splitExp) {
      Validate.isTrue(this.playerData.isOnline(), "Cannot give experience to offline player");
      if (value <= 0.0) {
         this.exp.put(profession.getId(), Math.max(0.0, this.exp.getOrDefault(profession.getId(), 0.0) + value));
      } else if (this.hasReachedMaxLevel(profession)) {
         this.setExperience(profession, 0.0);
      } else {
         AbstractParty party;
         if (splitExp && (party = this.playerData.getParty()) != null && MMOCore.plugin.configManager.splitProfessionExp) {
            List<PlayerData> onlineMembers = party.getOnlineMembers();
            value /= onlineMembers.size();

            for (PlayerData member : onlineMembers) {
               if (!member.equals(this.playerData)) {
                  member.getCollectionSkills().giveExperience(profession, value, source, null, false);
               }
            }
         }

         value *= (1.0 + this.playerData.getStats().getStat("ADDITIONAL_EXPERIENCE_" + UtilityMethods.enumName(profession.getId())) / 100.0)
            * MMOCore.plugin.boosterManager.getMultiplier(profession);
         PlayerExperienceGainEvent event = new PlayerExperienceGainEvent(this.playerData, profession, value, source);
         Bukkit.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            if (hologramLocation != null && profession.getOption(Profession.ProfessionOption.EXP_HOLOGRAMS)) {
               MMOCoreUtils.displayIndicator(
                  hologramLocation.add(0.5, 1.5, 0.5),
                  ConfigMessage.fromKey("exp-hologram", "exp", MythicLib.plugin.getMMOConfig().decimal.format(event.getExperience())).asLine()
               );
            }

            this.exp.put(profession.getId(), Math.max(0.0, this.exp.getOrDefault(profession.getId(), 0.0) + event.getExperience()));
            int oldLevel = this.getLevel(profession);
            boolean check = false;

            double needed;
            double exp;
            int level;
            while ((exp = this.exp.get(profession.getId())) >= (needed = profession.getExpCurve().getExperience((level = this.getLevel(profession)) + 1))) {
               if (this.hasReachedMaxLevel(profession)) {
                  this.setExperience(profession, 0.0);
                  check = true;
                  break;
               }

               this.exp.put(profession.getId(), exp - needed);
               this.level.put(profession.getId(), level + 1);
               check = true;
               this.playerData.giveExperience(profession.getExperience().calculate(level), null);
               profession.updateAdvancement(this.playerData, level);
            }

            if (check) {
               Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(this.playerData, profession, oldLevel, level));
               new SmallParticleEffect(this.playerData.getPlayer(), VParticle.INSTANT_EFFECT.get());
               ConfigMessage.fromKey("profession-level-up")
                  .addPlaceholders("level", String.valueOf(level), "profession", profession.getName())
                  .send(this.playerData.getPlayer());
               MMOCore.plugin.soundManager.getSound(SoundEvent.LEVEL_UP).playTo(this.playerData.getPlayer());
               this.playerData.getStats().updateStats();
            }

            StringBuilder bar = new StringBuilder(ChatColor.BOLD.toString());
            int chars = (int)(exp / needed * 20.0);

            for (int j = 0; j < 20; j++) {
               bar.append(j == chars ? ChatColor.WHITE.toString() + ChatColor.BOLD : "").append("|");
            }

            if (this.playerData.isOnline()) {
               ConfigMessage.fromKey(
                     "exp-notification",
                     "profession",
                     profession.getName(),
                     "progress",
                     bar.toString(),
                     "ratio",
                     MythicLib.plugin.getMMOConfig().decimal.format(exp / needed * 100.0)
                  )
                  .send(this.playerData.getPlayer());
            }
         }
      }
   }
}
