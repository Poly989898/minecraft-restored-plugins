package net.Indyuce.mmocore.api.quest;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.util.Closeable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class PlayerQuests implements Closeable {
   private final PlayerData playerData;
   private final Map<String, Long> finished = new HashMap<>();
   @Nullable
   private final BossBar bossbar;
   private final NamespacedKey bossbarNamespacedKey;
   private QuestProgress current;

   public PlayerQuests(PlayerData playerData) {
      this.playerData = playerData;
      if (!MMOCore.plugin.configManager.disableQuestBossBar && playerData.isOnline()) {
         this.bossbarNamespacedKey = new NamespacedKey(MMOCore.plugin, "mmocore_quest_progress_" + playerData.getUniqueId().toString());
         this.bossbar = Bukkit.createBossBar(this.bossbarNamespacedKey, "", BarColor.PURPLE, BarStyle.SEGMENTED_20, new BarFlag[0]);
         this.bossbar.addPlayer(playerData.getPlayer());
         this.bossbar.setVisible(false);
      } else {
         this.bossbarNamespacedKey = null;
         this.bossbar = null;
      }
   }

   public PlayerQuests load(ConfigurationSection config) {
      if (config.contains("current")) {
         try {
            this.current = MMOCore.plugin
               .questManager
               .get(config.getString("current.id"))
               .generateNewProgress(this.playerData, config.getInt("current.objective"));
         } catch (Exception e) {
            this.playerData.log(Level.WARNING, "Couldn't load current quest progress (ID '" + config.getString("current.id") + "')");
         }
      }

      if (config.contains("finished")) {
         for (String key : config.getConfigurationSection("finished").getKeys(false)) {
            this.finished.put(key, config.getLong("finished." + key));
         }
      }

      return this;
   }

   public void save(ConfigurationSection config) {
      if (this.current != null) {
         config.set("current.id", this.current.getQuest().getId());
         config.set("current.objective", this.current.getObjectiveNumber());
      } else {
         config.set("current", null);
      }

      for (String key : this.finished.keySet()) {
         config.set("finished." + key, this.finished.get(key));
      }
   }

   public String toJsonString() {
      JsonObject json = new JsonObject();
      if (this.current != null) {
         JsonObject cur = new JsonObject();
         cur.addProperty("id", this.current.getQuest().getId());
         cur.addProperty("objective", this.current.getObjectiveNumber());
         json.add("current", cur);
      }

      JsonObject fin = new JsonObject();

      for (String key : this.finished.keySet()) {
         fin.addProperty(key, this.finished.get(key));
      }

      if (this.finished.size() != 0) {
         json.add("finished", fin);
      }

      return json.toString();
   }

   public void load(String json) {
      JsonObject jo = (JsonObject)MythicLib.plugin.getGson().fromJson(json, JsonObject.class);
      if (jo.has("current")) {
         JsonObject cur = jo.getAsJsonObject("current");

         try {
            this.current = MMOCore.plugin.questManager.get(cur.get("id").getAsString()).generateNewProgress(this.playerData, cur.get("objective").getAsInt());
         } catch (Exception e) {
            this.playerData.log(Level.WARNING, "Couldn't load current quest progress (ID '" + cur.get("id").getAsString() + "')");
         }
      }

      if (jo.has("finished")) {
         for (Entry<String, JsonElement> entry : jo.getAsJsonObject("finished").entrySet()) {
            this.finished.put(entry.getKey(), entry.getValue().getAsLong());
         }
      }
   }

   public QuestProgress getCurrent() {
      return this.current;
   }

   public boolean hasCurrent() {
      return this.current != null;
   }

   public Set<String> getFinishedQuests() {
      return this.finished.keySet();
   }

   public boolean hasCurrent(Quest quest) {
      return this.hasCurrent() && this.current.getQuest().equals(quest);
   }

   public boolean hasFinished(Quest quest) {
      return this.finished.containsKey(quest.getId());
   }

   public void finishCurrent() {
      this.finished.put(this.current.getQuest().getId(), System.currentTimeMillis());
      this.start(null);
   }

   public void resetFinishedQuests() {
      this.finished.clear();
   }

   public Date getFinishDate(Quest quest) {
      return new Date(this.finished.get(quest.getId()));
   }

   public void start(Quest quest) {
      this.closeCurrentQuest();
      this.current = quest == null ? null : quest.generateNewProgress(this.playerData);
      this.updateBossBar();
   }

   public void closeCurrentQuest() {
      if (this.current != null) {
         this.current.getProgress().close();
         this.current = null;
      }
   }

   public void close() {
      if (this.bossbar != null) {
         this.bossbar.removeAll();
         Bukkit.removeBossBar(this.bossbarNamespacedKey);
      }

      this.closeCurrentQuest();
   }

   public boolean checkCooldownAvailability(Quest quest) {
      return this.finished.get(quest.getId()) + quest.getDelayMillis() < System.currentTimeMillis();
   }

   public long getDelayFeft(Quest quest) {
      return Math.max(this.finished.get(quest.getId()) + quest.getDelayMillis() - System.currentTimeMillis(), 0L);
   }

   public boolean checkParentAvailability(Quest quest) {
      for (Quest parent : quest.getParents()) {
         if (!this.hasFinished(parent)) {
            return false;
         }
      }

      return true;
   }

   public void updateBossBar() {
      if (this.bossbar != null) {
         if (this.hasCurrent() && this.current.getProgress().getObjective().hasLore()) {
            this.bossbar.setVisible(true);
            this.bossbar.setColor(this.current.getProgress().getObjective().getBarColor());
            this.bossbar.setTitle(this.current.getFormattedLore());
            this.bossbar.setProgress((double)this.current.getObjectiveNumber() / this.current.getQuest().getObjectives().size());
         } else {
            this.bossbar.setVisible(false);
         }
      }
   }
}
