package io.lumine.mythic.core.skills.mechanics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "sendtoast",
   version = "2.8",
   aliases = {"advancementmessage", "advmessage", "toastmessage", "toastmsg"},
   description = "Sends a message to the target player as an advancement"
)
public class SendToastMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
   protected PlaceholderString message;
   private String icon;
   private String iconnbt;
   private String frame;
   private JsonElement components;

   public SendToastMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.target_creative = true;

      try {
         this.message = PlaceholderString.of(mlc.getString(new String[]{"message", "msg", "m"}));
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'message' attribute is required.");
         this.message = PlaceholderString.of("INCORRECTLY CONFIGURED. SEE CONSOLE ON RELOAD.");
         e.printStackTrace();
      }

      String iconConfig = mlc.getString(new String[]{"icon", "i"}, "diamond_sword");
      ItemStack item = BukkitAdapter.adapt(this.getPlugin().getBootstrap().createItem(iconConfig));
      this.icon = "minecraft:" + item.getType().toString().toLowerCase();
      this.iconnbt = mlc.getString(new String[]{"iconnbt", "nbt"}, null);
      this.frame = mlc.getString(new String[]{"frame", "f"}, "challenge");
      if (this.iconnbt == null && ServerVersion.isAfterOrEq(MinecraftVersions.v1_21)) {
         Map<String, Object> componentMap = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getComponentData(item);
         this.components = GsonProvider.standard().toJsonTree(componentMap);
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         NamespacedKey id = new NamespacedKey(MythicBukkit.inst(), "temp-" + UUID.randomUUID().toString());
         String advJSON = this.build(data, target);
         Bukkit.getUnsafe().loadAdvancement(id, advJSON);
         Player player = (Player)target.asPlayer().getBukkitEntity();
         Advancement advancement = Bukkit.getAdvancement(id);
         Schedulers.runOnRegion(player, () -> {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
               for (String criteria : progress.getRemainingCriteria()) {
                  progress.awardCriteria(criteria);
               }
            }

            Schedulers.sync().runLater(() -> Schedulers.runOnRegion(player, () -> {
               if (progress.isDone()) {
                  for (String criteriax : progress.getAwardedCriteria()) {
                     progress.revokeCriteria(criteriax);
                  }
               }

               Bukkit.getUnsafe().removeAdvancement(id);
            }), 20L);
         });
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   private String build(SkillMetadata data, AbstractEntity target) {
      JsonObject json = new JsonObject();
      JsonObject icon = new JsonObject();
      JsonObject display = new JsonObject();
      JsonObject criteria = new JsonObject();
      JsonObject trigger = new JsonObject();
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
         icon.addProperty("id", this.icon);
         if (this.iconnbt != null) {
            icon.addProperty("components", this.iconnbt);
         } else if (this.components != null) {
            icon.add("components", this.components);
         }
      } else {
         icon.addProperty("item", this.icon);
         if (this.iconnbt != null) {
            icon.addProperty("nbt", this.iconnbt);
         }
      }

      display.add("icon", icon);
      display.addProperty("title", this.message.get(data, target));
      display.addProperty("description", "MythicMobs Toast Message");
      display.addProperty("background", "minecraft:textures/gui/advancements/backgrounds/adventure.png");
      display.addProperty("frame", this.frame);
      display.addProperty("announce_to_chat", false);
      display.addProperty("show_toast", true);
      display.addProperty("hidden", true);
      trigger.addProperty("trigger", "minecraft:impossible");
      criteria.add("impossible", trigger);
      json.add("criteria", criteria);
      json.add("display", display);
      return gson.toJson(json);
   }
}
