package io.lumine.mythic.bukkit.compatibility;

import com.kirelcodes.miniaturepets.MiniaturePets;
import com.kirelcodes.miniaturepets.config.Config;
import com.kirelcodes.miniaturepets.mob.Mob;
import com.kirelcodes.miniaturepets.mob.MobContainer;
import com.kirelcodes.miniaturepets.utils.APIUtils;
import com.kirelcodes.miniaturepets.utils.EntityAttributes;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import java.io.File;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MPetCompat {
   public MPetCompat.MiniaturePetType getModel(MythicConfig config) {
      return new MPetCompat.MiniaturePetType(config);
   }

   public static class MiniaturePetType {
      private MPetCompat.MythicMPet pet;
      private MobContainer container;
      private String display;
      private boolean showName;
      private boolean valid = false;

      public MiniaturePetType(MythicConfig mc) {
         String id = mc.getString("Model.Id", null);
         double speed = mc.getDouble("Options.MovementSpeed", 0.17);
         this.showName = mc.getBoolean("Model.ShowName", false);
         this.showName = mc.getBoolean("MiniaturePet.ShowName", this.showName);
         this.showName = mc.getBoolean("MPet.ShowName", this.showName);
         this.display = mc.getColorString("DisplayName", "");
         this.display = mc.getColorString("Display", this.display);
         if (id == null) {
            id = mc.getString("MPet.Id", null);
            if (id == null) {
               id = mc.getString("MiniaturePet.Id", null);
            }
         }

         String path = MiniaturePets.getInstance().getDataFolder().getAbsolutePath() + "/pets/" + id + ".mpet";
         File file = new File(path);
         if (!file.exists()) {
            MythicLogger.errorCompatibility("MiniaturePets", "Could not find file for pet type '" + id + "' at '" + path + "'");
         } else {
            this.container = APIUtils.loadContainer(file);
            this.container.setAnchor(EntityType.PIG);
            this.container.setSpeed(speed);
            this.valid = true;
         }
      }

      public boolean isValid() {
         return this.valid;
      }

      public void applyModel(AbstractEntity entity) {
         if (entity.isLiving()) {
            this.pet = new MPetCompat.MythicMPet(BukkitAdapter.adapt(entity.getLocation()), this.container, entity);
            ((LivingEntity)entity.getBukkitEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
            if (this.showName) {
               this.pet.setCustomName(this.display);
            }
         }
      }

      public void removeModel() {
         if (this.pet != null) {
            this.pet.remove();
            this.pet = null;
         }
      }
   }

   private static class MythicMPet extends Mob {
      public MythicMPet(@NonNull Location loc, MobContainer type, AbstractEntity entity) {
         super(loc, type);
         if (loc == null) {
            throw new NullPointerException("loc is marked non-null but is null");
         } else {
            LivingEntity oldNav = this.navigator;
            this.navigator = (LivingEntity)entity.getBukkitEntity();
            this.navigator.setCanPickupItems(false);

            try {
               EntityAttributes.miniatureId.setValue(this.navigator, this.getID());
            } catch (Exception e) {
               Bukkit.getLogger().warning("Setting of pet attributes failed for... some reason! Type: " + type.getAnchor());
               if (Config.isDebug()) {
                  e.printStackTrace();
               }
            }

            Schedulers.sync().runLater(() -> {
               if (this.navigator != null && this.navigator.isValid()) {
                  this.navigator.setFallDistance(0.0F);
                  this.navigator.teleport(loc);
                  if (this.getStructure() != null) {
                     this.getStructure().run();
                  }
               }
            }, 2L);
            oldNav.remove();
         }
      }
   }
}
