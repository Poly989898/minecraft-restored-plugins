package com.nexomc.nexo.mechanics.misc.misc;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.Mechanic;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0010\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\r\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u000f\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000bR\u0011\u0010\u0011\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000bR\u0011\u0010\u0012\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000bR\u0011\u0010\u0014\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000bR\u0011\u0010\u0016\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000bR\u0011\u0010\u0018\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000b¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/misc/MiscMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/misc/misc/MiscMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/misc/MiscMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "cactusBreaks", "", "getCactusBreaks", "()Z", "isVanillaInteractionDisabled", "canStripLogs", "getCanStripLogs", "piglinsIgnoreWhenEquipped", "getPiglinsIgnoreWhenEquipped", "isCompostable", "preventItemFrames", "getPreventItemFrames", "preventBreaking", "getPreventBreaking", "preventRenaming", "getPreventRenaming", "isAllowedInVanillaRecipes", "com.nexomc:core"}
)
public final class MiscMechanic extends Mechanic {
   private final boolean cactusBreaks;
   private final boolean isVanillaInteractionDisabled;
   private final boolean canStripLogs;
   private final boolean piglinsIgnoreWhenEquipped;
   private final boolean isCompostable;
   private final boolean preventItemFrames;
   private final boolean preventBreaking;
   private final boolean preventRenaming;
   private final boolean isAllowedInVanillaRecipes;

   public MiscMechanic(@NotNull MiscMechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.cactusBreaks = section.getBoolean("breaks_from_cactus", true);
      this.isVanillaInteractionDisabled = section.getBoolean("disable_vanilla_interactions", false);
      this.canStripLogs = section.getBoolean("can_strip_logs", false);
      this.piglinsIgnoreWhenEquipped = section.getBoolean("piglins_ignore_when_equipped", false);
      this.isCompostable = section.getBoolean("compostable", false);
      this.preventItemFrames = section.getBoolean("prevent_item_frames", false);
      this.preventBreaking = section.getBoolean("prevent_breaking", false);
      this.preventRenaming = section.getBoolean("prevent_renaming", false);
      this.isAllowedInVanillaRecipes = section.getBoolean("allow_in_vanilla_recipes", Settings.DEFAULT_ALLOW_IN_VANILLA_RECIPES.toBool());
   }

   public final boolean getCactusBreaks() {
      return this.cactusBreaks;
   }

   public final boolean isVanillaInteractionDisabled() {
      return this.isVanillaInteractionDisabled;
   }

   public final boolean getCanStripLogs() {
      return this.canStripLogs;
   }

   public final boolean getPiglinsIgnoreWhenEquipped() {
      return this.piglinsIgnoreWhenEquipped;
   }

   public final boolean isCompostable() {
      return this.isCompostable;
   }

   public final boolean getPreventItemFrames() {
      return this.preventItemFrames;
   }

   public final boolean getPreventBreaking() {
      return this.preventBreaking;
   }

   public final boolean getPreventRenaming() {
      return this.preventRenaming;
   }

   public final boolean isAllowedInVanillaRecipes() {
      return this.isAllowedInVanillaRecipes;
   }
}
