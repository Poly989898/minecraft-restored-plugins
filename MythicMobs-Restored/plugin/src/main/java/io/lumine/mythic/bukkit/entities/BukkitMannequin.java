package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile.SkinPatch;
import io.papermc.paper.datacomponent.item.ResolvableProfile.SkinPatchBuilder;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.MainHand;
import org.bukkit.profile.PlayerTextures.SkinModel;

@Getter
public class BukkitMannequin extends BukkitEntityType {
   private boolean immovable = false;
   private boolean hideDescription = false;
   private Component description;
   private MainHand mainHand;
   private Pose pose;
   private ResolvableProfile profile;

   @Override
   public EntityType getBukkitType() {
      return EntityType.MANNEQUIN;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.immovable = mc.getBoolean("MannequinOptions.Immovable", false);
      this.hideDescription = mc.getBoolean("MannequinOptions.HideDescription", false);
      String description = mc.getString("MannequinOptions.Description", null);
      if (description != null) {
         this.description = Text.parse(description);
      }

      String mainHand = mc.getString("MannequinOptions.MainHand", null);
      if (mainHand != null) {
         try {
            this.mainHand = MainHand.valueOf(mainHand);
         } catch (Throwable e) {
         }
      }

      String pose = mc.getString("MannequinOptions.Pose", null);
      if (pose != null) {
         try {
            this.pose = Pose.valueOf(pose);
         } catch (Throwable ex) {
         }
      }

      if (mc.isSet("MannequinOptions.Player")) {
         String playerName = mc.getString("MannequinOptions.Player", "Ashijin");
         this.profile = ResolvableProfile.resolvableProfile().name(playerName).build();
      } else {
         SkinPatchBuilder skinBuilder = SkinPatch.skinPatch();
         String skin = mc.getString("MannequinOptions.Skin", null);
         if (skin != null) {
            if (!skin.contains(":")) {
               skin = "minecraft:" + skin;
            }

            skinBuilder.body(Key.key(skin));
         }

         String cape = mc.getString("MannequinOptions.Cape", null);
         if (cape != null) {
            if (!cape.contains(":")) {
               cape = "minecraft:" + cape;
            }

            skinBuilder.cape(Key.key(cape));
         }

         String elytra = mc.getString("MannequinOptions.Elytra", null);
         if (elytra != null) {
            if (!elytra.contains(":")) {
               elytra = "minecraft:" + elytra;
            }

            skinBuilder.elytra(Key.key(elytra));
         }

         String skinModel = mc.getString("MannequinOptions.Model", "CLASSIC");
         if (skinModel != null) {
            try {
               skinBuilder.model(SkinModel.valueOf(skinModel.toUpperCase()));
            } catch (Throwable ex2) {
            }
         }

         this.profile = ResolvableProfile.resolvableProfile().skinPatch(skinBuilder.build()).build();
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Mannequin mannequin = (Mannequin)entity;
      mannequin.setImmovable(this.immovable);
      if (this.description != null) {
         mannequin.setDescription(this.description);
      }

      if (this.pose != null) {
         mannequin.setPose(this.pose);
      }

      if (this.mainHand != null) {
         mannequin.setMainHand(this.mainHand);
      }

      mannequin.setProfile(this.profile);
      return mannequin;
   }
}
