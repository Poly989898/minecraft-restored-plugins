package net.Indyuce.mmocore.manager.data.sql;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.sql.SQLDataSynchronizer;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.api.player.profess.SavedClassInformation;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.guild.provided.Guild;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skilltree.SkillTreeNode;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.Nullable;

public class MMOCoreDataSynchronizer extends SQLDataSynchronizer<PlayerData> {
   public MMOCoreDataSynchronizer(SQLDataHandler handler, PlayerData data) {
      super("mmocore_playerdata", "uuid", handler.getDataSource(), data);
   }

   public void loadData(ResultSet result) throws SQLException {
      ((PlayerData)this.getData()).resetTriggerStats();
      ((PlayerData)this.getData()).setClassPoints(result.getInt("class_points"));
      ((PlayerData)this.getData()).setSkillPoints(result.getInt("skill_points"));
      ((PlayerData)this.getData()).setSkillReallocationPoints(result.getInt("skill_reallocation_points"));
      ((PlayerData)this.getData()).setSkillTreeReallocationPoints(result.getInt("skill_tree_reallocation_points"));
      ((PlayerData)this.getData()).setAttributePoints(result.getInt("attribute_points"));
      ((PlayerData)this.getData()).setAttributeReallocationPoints(result.getInt("attribute_realloc_points"));
      ((PlayerData)this.getData()).setLevel(result.getInt("level"));
      ((PlayerData)this.getData()).setExperience(result.getDouble("experience"));
      if (!this.isEmpty(result.getString("class"))) {
         ((PlayerData)this.getData()).setClass(MMOCore.plugin.classManager.get(result.getString("class")));
      }

      if (!this.isEmpty(result.getString("times_claimed"))) {
         JsonObject json = (JsonObject)MythicLib.plugin.getGson().fromJson(result.getString("times_claimed"), JsonObject.class);
         json.entrySet()
            .forEach(entryx -> ((PlayerData)this.getData()).getItemClaims().put((String)entryx.getKey(), ((JsonElement)entryx.getValue()).getAsInt()));
      }

      if (!this.isEmpty(result.getString("skill_tree_points"))) {
         JsonObject json = (JsonObject)MythicLib.plugin.getGson().fromJson(result.getString("skill_tree_points"), JsonObject.class);

         for (SkillTree skillTree : MMOCore.plugin.skillTreeManager.getAll()) {
            ((PlayerData)this.getData()).setSkillTreePoints(skillTree.getId(), json.has(skillTree.getId()) ? json.get(skillTree.getId()).getAsInt() : 0);
         }

         ((PlayerData)this.getData()).setSkillTreePoints("global", json.has("global") ? json.get("global").getAsInt() : 0);
      }

      if (!this.isEmpty(result.getString("skill_tree_levels"))) {
         JsonObject json = (JsonObject)MythicLib.plugin.getGson().fromJson(result.getString("skill_tree_levels"), JsonObject.class);

         for (SkillTreeNode skillTreeNode : MMOCore.plugin.skillTreeManager.getAllNodes()) {
            ((PlayerData)this.getData()).setNodeLevel(skillTreeNode, json.has(skillTreeNode.getFullId()) ? json.get(skillTreeNode.getFullId()).getAsInt() : 0);
         }
      }

      Set<String> unlockedItems = new HashSet<>();
      if (!this.isEmpty(result.getString("unlocked_items"))) {
         for (JsonElement item : (JsonArray)MythicLib.plugin.getGson().fromJson(result.getString("unlocked_items"), JsonArray.class)) {
            unlockedItems.add(item.getAsString());
         }
      }

      ((PlayerData)this.getData()).setUnlockedItems(unlockedItems);
      if (!this.isEmpty(result.getString("guild"))) {
         Guild guild = MMOCore.plugin.nativeGuildManager.getGuild(result.getString("guild"));
         if (guild != null && guild.hasMember(((PlayerData)this.getData()).getUniqueId())) {
            ((PlayerData)this.getData()).setGuild(guild);
         }
      }

      if (!this.isEmpty(result.getString("attributes"))) {
         ((PlayerData)this.getData()).getAttributes().load(result.getString("attributes"));
      }

      if (((PlayerData)this.getData()).isOnline()) {
         MMOCore.plugin.attributeManager.getAll().forEach(attribute -> ((PlayerData)this.getData()).getAttributes().getInstance(attribute).updateStats());
      }

      if (!this.isEmpty(result.getString("professions"))) {
         ((PlayerData)this.getData()).getCollectionSkills().load(result.getString("professions"));
      }

      if (!this.isEmpty(result.getString("quests"))) {
         ((PlayerData)this.getData()).getQuestData().load(result.getString("quests"));
      }

      ((PlayerData)this.getData()).getQuestData().updateBossBar();
      if (!this.isEmpty(result.getString("waypoints"))) {
         ((PlayerData)this.getData()).getWaypoints().addAll(MMOCoreUtils.jsonArrayToList(result.getString("waypoints")));
      }

      if (!this.isEmpty(result.getString("friends"))) {
         MMOCoreUtils.jsonArrayToList(result.getString("friends")).forEach(str -> ((PlayerData)this.getData()).getFriends().add(UUID.fromString(str)));
      }

      if (!this.isEmpty(result.getString("skills"))) {
         JsonObject object = (JsonObject)MythicLib.plugin.getGson().fromJson(result.getString("skills"), JsonObject.class);

         for (Entry<String, JsonElement> entry : object.entrySet()) {
            ((PlayerData)this.getData()).setSkillLevel(entry.getKey(), entry.getValue().getAsInt());
         }
      }

      if (!this.isEmpty(result.getString("bound_skills"))) {
         JsonObject object = (JsonObject)MythicLib.plugin.getGson().fromJson(result.getString("bound_skills"), JsonObject.class);

         for (Entry<String, JsonElement> entry : object.entrySet()) {
            ClassSkill skill = ((PlayerData)this.getData()).getProfess().getSkill(entry.getValue().getAsString());
            if (skill != null) {
               ((PlayerData)this.getData()).bindSkill(Integer.parseInt(entry.getKey()), skill);
            }
         }
      }

      if (!this.isEmpty(result.getString("class_info"))) {
         JsonObject object = (JsonObject)MythicLib.plugin.getGson().fromJson(result.getString("class_info"), JsonObject.class);

         for (Entry<String, JsonElement> entry : object.entrySet()) {
            try {
               PlayerClass profess = MMOCore.plugin.classManager.get(entry.getKey());
               Validate.notNull(profess, "Could not find class '" + entry.getKey() + "'");
               ((PlayerData)this.getData()).applyClassInfo(profess, new SavedClassInformation(entry.getValue().getAsJsonObject()));
            } catch (IllegalArgumentException exception) {
               MMOCore.log(Level.WARNING, "Could not load class info '" + entry.getKey() + "': " + exception.getMessage());
            }
         }
      }

      ((PlayerData)this.getData()).setHealth(result.getDouble("health"));
      ((PlayerData)this.getData()).setMana(result.getDouble("mana"));
      ((PlayerData)this.getData()).setStamina(result.getDouble("stamina"));
      ((PlayerData)this.getData()).setStellium(result.getDouble("stellium"));
      UtilityMethods.debug(
         MMOCore.plugin,
         "SQL",
         String.format("{ class: %s, level: %d }", ((PlayerData)this.getData()).getProfess().getId(), ((PlayerData)this.getData()).getLevel())
      );
   }

   private boolean isEmpty(@Nullable String str) {
      return str == null || str.isEmpty() || str.equalsIgnoreCase("null") || str.equals("{}") || str.equals("[]");
   }

   public void loadEmptyData() {
      MMOCore.plugin.playerDataManager.getDefaultData().apply((PlayerData)this.getData());
      UtilityMethods.debug(MMOCore.plugin, "SQL", "Loaded DEFAULT data for: '" + ((PlayerData)this.getData()).getUniqueId() + "' as no saved data was found.");
   }
}
