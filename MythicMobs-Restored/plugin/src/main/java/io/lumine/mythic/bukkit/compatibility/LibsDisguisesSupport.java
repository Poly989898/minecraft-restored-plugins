package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.config.GenericConfig;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.Arrays;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.LibsDisguises;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import me.libraryaddict.disguise.utilities.DisguiseUtilities;
import me.libraryaddict.disguise.utilities.parser.DisguiseParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class LibsDisguisesSupport {
   public LibsDisguisesSupport.LDVersion version = LibsDisguisesSupport.LDVersion.v1_1_1;
   public boolean enabled = false;
   public LibsDisguises ld = (LibsDisguises)Bukkit.getPluginManager().getPlugin("LibsDisguises");

   public LibsDisguisesSupport() {
      this.setCompatibility();
   }

   private void setCompatibility() {
      try {
         Integer[] v = Arrays.stream(this.ld.getDescription().getVersion().replaceAll("[^0-9.]", "").split("\\."))
            .map(Integer::parseInt)
            .toArray(Integer[]::new);
         LibsDisguisesSupport.LDVersion[] versions = LibsDisguisesSupport.LDVersion.values();
         int i = versions.length - 1;

         whileLoop:
         while (i >= 0) {
            Integer[] v1 = Arrays.stream(versions[i].name().replace("v", "").split("_")).map(Integer::parseInt).toArray(Integer[]::new);

            for (int a = 0; a < Math.min(v1.length, v.length); a++) {
               if (v[a] < v1[a]) {
                  i--;
                  continue whileLoop;
               }
            }

            this.version = versions[i];
            break;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public boolean isCompatible(LibsDisguisesSupport.LDVersion version) {
      return version.ordinal() >= this.version.ordinal();
   }

   public void disguiseNextEntity(String disguise) {
      Disguise fd = this.getParsedDisguise(disguise);

      try {
         DisguiseAPI.disguiseNextEntity(fd);
      } catch (Exception e) {
         MythicLogger.errorCompatibility("LibsDisguises", "Error applying Disguise: disguise may be configured incorrectly.");
         MythicLogger.handleMinorError(e);
      }
   }

   public void preDisguise(Entity entity, String disguise) {
      Disguise fd = this.getParsedDisguise(disguise);

      try {
         DisguiseUtilities.addFutureDisguise(entity.getEntityId(), (TargetedDisguise)fd);
      } catch (Exception e) {
         MythicLogger.errorCompatibility("LibsDisguises", "Error applying Disguise: disguise may be configured incorrectly.");
         MythicLogger.handleMinorError(e);
      }
   }

   public void setDisguise(ActiveMob am, String disguise) {
      Entity e = am.getEntity().getBukkitEntity();
      Disguise fd = this.getParsedDisguise(disguise);
      Schedulers.sync().run(() -> {
         try {
            DisguiseAPI.disguiseToAll(e, fd);
         } catch (Exception var3x) {
            MythicLogger.errorCompatibility("LibsDisguises", "Error applying Disguise: disguise may be configured incorrectly.");
            MythicLogger.handleMinorError(var3x);
         }
      });
   }

   public void setDisguise(AbstractEntity entity, MythicLineConfig mc) {
      Entity e = BukkitAdapter.adapt(entity);
      String disguise = mc.getString(new String[]{"type", "disguise", "d", "config", "conf", "c"}, "player Xikage");
      Disguise fd = this.getParsedDisguise(disguise);

      try {
         DisguiseAPI.disguiseToAll(e, fd);
      } catch (Throwable ex) {
         MythicLogger.errorCompatibility("LibsDisguises", "Error applying disguise '" + disguise + "': disguise may be configured incorrectly.");
         ex.printStackTrace();
      }
   }

   public void removeDisguise(AbstractEntity entity) {
      Schedulers.sync().run(() -> DisguiseAPI.undisguiseToAll(entity.getBukkitEntity()));
   }

   public Disguise getDisguise(String disguise) {
      try {
         return DisguiseParser.parseDisguise(disguise);
      } catch (Throwable e) {
         MythicLogger.errorCompatibility("LibsDisguises", "Invalid disguise supplied");
         e.printStackTrace();
         return null;
      }
   }

   public void modifyDisguise(AbstractEntity target, String disguiseString) {
      try {
         Entity entity = target.getBukkitEntity();
         Disguise disguise = DisguiseAPI.getDisguise(entity);
         if (disguise != null) {
            DisguiseParser.modifyDisguise(disguise, entity, disguiseString);
         }
      } catch (Throwable e) {
         MythicLogger.errorCompatibility("LibsDisguises", "Failed to modify disguise: Invalid arguments supplied: " + disguiseString);
         e.printStackTrace();
      }
   }

   public Disguise getParsedDisguise(String disguiseConfig) {
      Disguise d = null;

      try {
         d = DisguiseParser.parseDisguise(disguiseConfig);
      } catch (Throwable e) {
         if (disguiseConfig != null && disguiseConfig.startsWith("MemorySection")) {
            MythicLogger.errorCompatibility(
               "LibsDisguises", "Disguise '" + disguiseConfig + "' is out of date! Please update this mob to the new disguise syntax."
            );
         } else {
            MythicLogger.errorCompatibility("LibsDisguises", "Disguise '" + disguiseConfig + "' failed to parse");
            e.printStackTrace();
         }
      } finally {
         if (d == null) {
            MythicLogger.errorCompatibility("LibsDisguises", "Disguise '" + disguiseConfig + "' not found");
         }
      }

      return d;
   }

   public void setDisguiseName(AbstractEntity target, String name) {
      Disguise disguise = DisguiseAPI.getDisguise(target.getBukkitEntity());
      if (disguise != null) {
         if (disguise.isPlayerDisguise() && this.isCompatible(LibsDisguisesSupport.LDVersion.v9_9_3)) {
            if (!((PlayerDisguise)disguise).getName().equals(" ")) {
               ((PlayerDisguise)disguise).setName(name);
            }
         } else if (disguise.getWatcher().getCustomName() != null) {
            disguise.getWatcher().setCustomName(name);
         }
      }
   }

   public void modifyDisguise(AbstractEntity target, GenericConfig mc) {
      Entity entity = BukkitAdapter.adapt(target);
      Disguise d = DisguiseAPI.getDisguise(entity);
      if (d != null) {
         FlagWatcher fw = d.getWatcher();
         if (mc.getBoolean("Burning", false)) {
            fw.setBurning(true);
         } else {
            fw.setBurning(false);
         }

         if (mc.getBoolean("Invisible", false)) {
            fw.setInvisible(true);
         } else {
            fw.setInvisible(false);
         }

         if (mc.getBoolean("Glowing", false)) {
            fw.setGlowing(true);
         } else {
            fw.setGlowing(false);
         }

         if (mc.getBoolean("Gliding", false)) {
            fw.setFlyingWithElytra(true);
         } else {
            fw.setFlyingWithElytra(false);
         }

         if (mc.getBoolean("Blocking", false)) {
            fw.setRightClicking(true);
         } else {
            fw.setRightClicking(false);
         }

         if (mc.getBoolean("Sneaking", false)) {
            fw.setSneaking(true);
         } else {
            fw.setSneaking(false);
         }

         if (mc.getBoolean("Sprinting", false)) {
            fw.setSprinting(true);
         } else {
            fw.setSprinting(false);
         }

         if (!mc.getBoolean("ModifyBoundingBox", true)) {
            d.setModifyBoundingBox(false);
         } else {
            d.setModifyBoundingBox(true);
         }

         d = d.setWatcher(fw);
         DisguiseAPI.disguiseToAll(entity, d);
      }
   }

   static enum LDVersion {
      v1_1_1,
      v9_9_3;
   }
}
