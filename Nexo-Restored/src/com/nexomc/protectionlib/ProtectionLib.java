package com.nexomc.protectionlib;

import com.bekvon.bukkit.residence.Residence;
import com.massivecraft.factions.FactionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.crashcraft.crashclaim.CrashClaim;
import net.william278.huskclaims.BukkitHuskClaims;
import net.william278.husktowns.BukkitHuskTowns;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import p1xel.nobuildplus.NoBuildPlus;
import world.bentobox.bentobox.BentoBox;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001\u001eB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0007J\u0018\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0007J\u0018\u0010\u0013\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0007J\u0018\u0010\u0014\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0007J\u0018\u0010\u0015\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0007J3\u0010\u0016\u001a\u00020\u000b\"\n\b\u0000\u0010\u0017\u0018\u0001*\u00020\u00072\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\r2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u0002H\u00170\u001cH\u0082\bJ\b\u0010\u001d\u001a\u00020\tH\u0002R\u001c\u0010\u0004\u001a\u0010\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u00020\t8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u001f"},
   d2 = {"Lcom/nexomc/protectionlib/ProtectionLib;", "", "<init>", "()V", "compatibilities", "", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "Lorg/bukkit/plugin/Plugin;", "debug", "", "init", "", "plugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "canBuild", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "handleCompatibility", "T", "pluginName", "", "mainPlugin", "constructor", "Lcom/nexomc/protectionlib/ProtectionLib$CompatibilityConstructor;", "checkFactionsCompat", "CompatibilityConstructor", "protectionlib"}
)
public final class ProtectionLib {
   @NotNull
   public static final ProtectionLib INSTANCE = new ProtectionLib();
   @NotNull
   private static final Set<ProtectionCompatibility<? extends Plugin>> compatibilities = (Set)(new ObjectOpenHashSet());
   @JvmField
   public static boolean debug;

   private ProtectionLib() {
   }

   @JvmStatic
   public static final void init(@NotNull JavaPlugin plugin) {
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      ProtectionLib this_$iv = INSTANCE;
      String pluginName$iv = "WorldGuard";
      CompatibilityConstructor constructor$iv = ProtectionLib$init$1.INSTANCE;
      ProtectionLib $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label463: {
         Object $this$handleCompatibility_u24lambda_u2412$iv;
         try {
            int var7 = 0;
            Plugin var134 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var134 instanceof WorldGuardPlugin)) {
               var134 = null;
            }

            var134 = (Plugin)((WorldGuardPlugin)var134);
            if (var134 == null) {
               break label463;
            }

            Plugin plugin$iv = var134;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label463;
            }

            Set var136 = compatibilities;
            ProtectionCompatibility var10001 = constructor$iv.create(plugin, plugin$iv);
            if (var10001 != null) {
               var136.add(var10001);
            }

            $this$handleCompatibility_u24lambda_u2412$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var20) {
            $this$handleCompatibility_u24lambda_u2412$iv = Result.constructor-impl(ResultKt.createFailure(var20));
         }

         Throwable var137 = Result.exceptionOrNull-impl($this$handleCompatibility_u24lambda_u2412$iv);
         if (var137 != null) {
            Throwable $this$handleCompatibility_u24lambda_u2412$iv = var137;
            int var110 = 0;
            if (debug) {
               $this$handleCompatibility_u24lambda_u2412$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "Towny";
      constructor$iv = ProtectionLib$init$2.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label465: {
         Object $this$handleCompatibility_u24lambda_u2412$iv;
         try {
            int it$iv = 0;
            Plugin var140 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var140 instanceof Plugin)) {
               var140 = null;
            }

            if (var140 == null) {
               break label465;
            }

            Plugin plugin$iv = var140;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label465;
            }

            Set var141 = compatibilities;
            ProtectionCompatibility var201 = constructor$iv.create(plugin, plugin$iv);
            if (var201 != null) {
               var141.add(var201);
            }

            $this$handleCompatibility_u24lambda_u2412$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var19) {
            $this$handleCompatibility_u24lambda_u2412$iv = Result.constructor-impl(ResultKt.createFailure(var19));
         }

         Throwable var142 = Result.exceptionOrNull-impl($this$handleCompatibility_u24lambda_u2412$iv);
         if (var142 != null) {
            Throwable $this$handleCompatibility_u24lambda_u2412$iv = var142;
            int var112 = 0;
            if (debug) {
               $this$handleCompatibility_u24lambda_u2412$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "Factions";
      constructor$iv = ProtectionLib$init$3.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label467: {
         Object $this$handleCompatibility_u24lambda_u2412$iv;
         try {
            int var100 = 0;
            Plugin var145 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var145 instanceof FactionsPlugin)) {
               var145 = null;
            }

            var145 = (Plugin)((FactionsPlugin)var145);
            if (var145 == null) {
               break label467;
            }

            Plugin plugin$iv = var145;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label467;
            }

            Set var147 = compatibilities;
            ProtectionCompatibility var202 = constructor$iv.create(plugin, plugin$iv);
            if (var202 != null) {
               var147.add(var202);
            }

            $this$handleCompatibility_u24lambda_u2412$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var18) {
            $this$handleCompatibility_u24lambda_u2412$iv = Result.constructor-impl(ResultKt.createFailure(var18));
         }

         Throwable var148 = Result.exceptionOrNull-impl($this$handleCompatibility_u24lambda_u2412$iv);
         if (var148 != null) {
            Throwable $this$handleCompatibility_u24lambda_u2412$iv = var148;
            int var114 = 0;
            if (debug) {
               $this$handleCompatibility_u24lambda_u2412$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "Lands";
      constructor$iv = ProtectionLib$init$4.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label469: {
         Object it$iv;
         try {
            int it$iv = 0;
            Plugin var151 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var151 instanceof Plugin)) {
               var151 = null;
            }

            if (var151 == null) {
               break label469;
            }

            Plugin plugin$iv = var151;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label469;
            }

            Set var152 = compatibilities;
            ProtectionCompatibility var203 = constructor$iv.create(plugin, plugin$iv);
            if (var203 != null) {
               var152.add(var203);
            }

            it$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var17) {
            it$iv = Result.constructor-impl(ResultKt.createFailure(var17));
         }

         Throwable var153 = Result.exceptionOrNull-impl(it$iv);
         if (var153 != null) {
            Throwable it$iv = var153;
            int var116 = 0;
            if (debug) {
               it$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "PlotSquared";
      constructor$iv = ProtectionLib$init$5.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label471: {
         Object it$iv;
         try {
            int var102 = 0;
            Plugin var156 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var156 instanceof Plugin)) {
               var156 = null;
            }

            if (var156 == null) {
               break label471;
            }

            Plugin plugin$iv = var156;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label471;
            }

            Set var157 = compatibilities;
            ProtectionCompatibility var204 = constructor$iv.create(plugin, plugin$iv);
            if (var204 != null) {
               var157.add(var204);
            }

            it$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var16) {
            it$iv = Result.constructor-impl(ResultKt.createFailure(var16));
         }

         Throwable var158 = Result.exceptionOrNull-impl(it$iv);
         if (var158 != null) {
            Throwable it$iv = var158;
            int var118 = 0;
            if (debug) {
               it$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "CrashClaim";
      constructor$iv = ProtectionLib$init$6.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label473: {
         Object it$iv;
         try {
            int it$iv = 0;
            Plugin var161 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var161 instanceof CrashClaim)) {
               var161 = null;
            }

            var161 = (Plugin)((CrashClaim)var161);
            if (var161 == null) {
               break label473;
            }

            Plugin plugin$iv = var161;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label473;
            }

            Set var163 = compatibilities;
            ProtectionCompatibility var205 = constructor$iv.create(plugin, plugin$iv);
            if (var205 != null) {
               var163.add(var205);
            }

            it$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var15) {
            it$iv = Result.constructor-impl(ResultKt.createFailure(var15));
         }

         Throwable var164 = Result.exceptionOrNull-impl(it$iv);
         if (var164 != null) {
            Throwable it$iv = var164;
            int var120 = 0;
            if (debug) {
               it$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "GriefPrevention";
      constructor$iv = ProtectionLib$init$7.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label475: {
         Object it$iv;
         try {
            int var104 = 0;
            Plugin var167 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var167 instanceof GriefPrevention)) {
               var167 = null;
            }

            var167 = (Plugin)((GriefPrevention)var167);
            if (var167 == null) {
               break label475;
            }

            Plugin plugin$iv = var167;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label475;
            }

            Set var169 = compatibilities;
            ProtectionCompatibility var206 = constructor$iv.create(plugin, plugin$iv);
            if (var206 != null) {
               var169.add(var206);
            }

            it$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var14) {
            it$iv = Result.constructor-impl(ResultKt.createFailure(var14));
         }

         Throwable var170 = Result.exceptionOrNull-impl(it$iv);
         if (var170 != null) {
            Throwable it$iv = var170;
            int var122 = 0;
            if (debug) {
               it$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "HuskClaims";
      constructor$iv = ProtectionLib$init$8.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label477: {
         Object it$iv;
         try {
            int it$iv = 0;
            Plugin var173 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var173 instanceof BukkitHuskClaims)) {
               var173 = null;
            }

            var173 = (Plugin)((BukkitHuskClaims)var173);
            if (var173 == null) {
               break label477;
            }

            Plugin plugin$iv = var173;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label477;
            }

            Set var175 = compatibilities;
            ProtectionCompatibility var207 = constructor$iv.create(plugin, plugin$iv);
            if (var207 != null) {
               var175.add(var207);
            }

            it$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var13) {
            it$iv = Result.constructor-impl(ResultKt.createFailure(var13));
         }

         Throwable var176 = Result.exceptionOrNull-impl(it$iv);
         if (var176 != null) {
            Throwable it$iv = var176;
            int var124 = 0;
            if (debug) {
               it$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "BentoBox";
      constructor$iv = ProtectionLib$init$9.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label479: {
         Object it$iv;
         try {
            int var106 = 0;
            Plugin var179 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var179 instanceof BentoBox)) {
               var179 = null;
            }

            var179 = (Plugin)((BentoBox)var179);
            if (var179 == null) {
               break label479;
            }

            Plugin plugin$iv = var179;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label479;
            }

            Set var181 = compatibilities;
            ProtectionCompatibility var208 = constructor$iv.create(plugin, plugin$iv);
            if (var208 != null) {
               var181.add(var208);
            }

            it$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var12) {
            it$iv = Result.constructor-impl(ResultKt.createFailure(var12));
         }

         Throwable var182 = Result.exceptionOrNull-impl(it$iv);
         if (var182 != null) {
            Throwable it$iv = var182;
            int var126 = 0;
            if (debug) {
               it$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "HuskTowns";
      constructor$iv = ProtectionLib$init$10.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label481: {
         Object it$iv;
         try {
            int it$iv = 0;
            Plugin var185 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var185 instanceof BukkitHuskTowns)) {
               var185 = null;
            }

            var185 = (Plugin)((BukkitHuskTowns)var185);
            if (var185 == null) {
               break label481;
            }

            Plugin plugin$iv = var185;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label481;
            }

            Set var187 = compatibilities;
            ProtectionCompatibility var209 = constructor$iv.create(plugin, plugin$iv);
            if (var209 != null) {
               var187.add(var209);
            }

            it$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var11) {
            it$iv = Result.constructor-impl(ResultKt.createFailure(var11));
         }

         Throwable var188 = Result.exceptionOrNull-impl(it$iv);
         if (var188 != null) {
            Throwable it$iv = var188;
            int var128 = 0;
            if (debug) {
               it$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "Residence";
      constructor$iv = ProtectionLib$init$11.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      label483: {
         Object it$iv;
         try {
            int var108 = 0;
            Plugin var191 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
            if (!(var191 instanceof Residence)) {
               var191 = null;
            }

            var191 = (Plugin)((Residence)var191);
            if (var191 == null) {
               break label483;
            }

            Plugin plugin$iv = var191;
            if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
               break label483;
            }

            Set var193 = compatibilities;
            ProtectionCompatibility var210 = constructor$iv.create(plugin, plugin$iv);
            if (var210 != null) {
               var193.add(var210);
            }

            it$iv = Result.constructor-impl(Unit.INSTANCE);
         } catch (Throwable var10) {
            it$iv = Result.constructor-impl(ResultKt.createFailure(var10));
         }

         Throwable var194 = Result.exceptionOrNull-impl(it$iv);
         if (var194 != null) {
            Throwable it$iv = var194;
            int var130 = 0;
            if (debug) {
               it$iv.printStackTrace();
            }
         }
      }

      this_$iv = INSTANCE;
      pluginName$iv = "NoBuildPlus";
      constructor$iv = ProtectionLib$init$12.INSTANCE;
      $this$handleCompatibility_u24lambda_u2412$iv = this_$iv;

      Object it$iv;
      try {
         int it$iv = 0;
         Plugin var197 = Bukkit.getPluginManager().getPlugin(pluginName$iv);
         if (!(var197 instanceof NoBuildPlus)) {
            var197 = null;
         }

         var197 = (Plugin)((NoBuildPlus)var197);
         if (var197 == null) {
            return;
         }

         Plugin plugin$iv = var197;
         if (Intrinsics.areEqual(pluginName$iv, "Factions") && !$this$handleCompatibility_u24lambda_u2412$iv.checkFactionsCompat()) {
            return;
         }

         Set var199 = compatibilities;
         ProtectionCompatibility var211 = constructor$iv.create(plugin, plugin$iv);
         if (var211 != null) {
            var199.add(var211);
         }

         it$iv = Result.constructor-impl(Unit.INSTANCE);
      } catch (Throwable var9) {
         it$iv = Result.constructor-impl(ResultKt.createFailure(var9));
      }

      Throwable var200 = Result.exceptionOrNull-impl(it$iv);
      if (var200 != null) {
         Throwable it$iv = var200;
         int var132 = 0;
         if (debug) {
            it$iv.printStackTrace();
         }
      }

   }

   @JvmStatic
   public static final boolean canBuild(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      ProtectionLib var2 = INSTANCE;

      Object $this$canBuild_u24lambda_u241;
      try {
         int var4 = 0;
         Iterable $this$all$iv = (Iterable)compatibilities;
         boolean var17;
         if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
            var17 = true;
         } else {
            Iterator var7 = $this$all$iv.iterator();

            while(true) {
               if (!var7.hasNext()) {
                  var17 = true;
                  break;
               }

               Object element$iv = var7.next();
               ProtectionCompatibility it = (ProtectionCompatibility)element$iv;
               int var10 = 0;
               if (!it.canBuild(player, target)) {
                  var17 = false;
                  break;
               }
            }
         }

         $this$canBuild_u24lambda_u241 = Result.constructor-impl(var17);
      } catch (Throwable var11) {
         $this$canBuild_u24lambda_u241 = Result.constructor-impl(ResultKt.createFailure(var11));
      }

      Object var12 = $this$canBuild_u24lambda_u241;
      Throwable var18 = Result.exceptionOrNull-impl($this$canBuild_u24lambda_u241);
      if (var18 != null) {
         Throwable it = var18;
         int var15 = 0;
         if (debug) {
            it.printStackTrace();
         }
      }

      Boolean it = true;
      return (Boolean)(Result.isFailure-impl(var12) ? it : var12);
   }

   @JvmStatic
   public static final boolean canBreak(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      ProtectionLib var2 = INSTANCE;

      Object $this$canBreak_u24lambda_u244;
      try {
         int var4 = 0;
         Iterable $this$all$iv = (Iterable)compatibilities;
         boolean var17;
         if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
            var17 = true;
         } else {
            Iterator var7 = $this$all$iv.iterator();

            while(true) {
               if (!var7.hasNext()) {
                  var17 = true;
                  break;
               }

               Object element$iv = var7.next();
               ProtectionCompatibility it = (ProtectionCompatibility)element$iv;
               int var10 = 0;
               if (!it.canBreak(player, target)) {
                  var17 = false;
                  break;
               }
            }
         }

         $this$canBreak_u24lambda_u244 = Result.constructor-impl(var17);
      } catch (Throwable var11) {
         $this$canBreak_u24lambda_u244 = Result.constructor-impl(ResultKt.createFailure(var11));
      }

      Object var12 = $this$canBreak_u24lambda_u244;
      Throwable var18 = Result.exceptionOrNull-impl($this$canBreak_u24lambda_u244);
      if (var18 != null) {
         Throwable it = var18;
         int var15 = 0;
         if (debug) {
            it.printStackTrace();
         }
      }

      Boolean it = true;
      return (Boolean)(Result.isFailure-impl(var12) ? it : var12);
   }

   @JvmStatic
   public static final boolean canInteract(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      ProtectionLib var2 = INSTANCE;

      Object $this$canInteract_u24lambda_u247;
      try {
         int var4 = 0;
         Iterable $this$all$iv = (Iterable)compatibilities;
         boolean var17;
         if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
            var17 = true;
         } else {
            Iterator var7 = $this$all$iv.iterator();

            while(true) {
               if (!var7.hasNext()) {
                  var17 = true;
                  break;
               }

               Object element$iv = var7.next();
               ProtectionCompatibility it = (ProtectionCompatibility)element$iv;
               int var10 = 0;
               if (!it.canInteract(player, target)) {
                  var17 = false;
                  break;
               }
            }
         }

         $this$canInteract_u24lambda_u247 = Result.constructor-impl(var17);
      } catch (Throwable var11) {
         $this$canInteract_u24lambda_u247 = Result.constructor-impl(ResultKt.createFailure(var11));
      }

      Object var12 = $this$canInteract_u24lambda_u247;
      Throwable var18 = Result.exceptionOrNull-impl($this$canInteract_u24lambda_u247);
      if (var18 != null) {
         Throwable it = var18;
         int var15 = 0;
         if (debug) {
            it.printStackTrace();
         }
      }

      Boolean it = true;
      return (Boolean)(Result.isFailure-impl(var12) ? it : var12);
   }

   @JvmStatic
   public static final boolean canUse(@NotNull Player player, @NotNull Location target) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(target, "target");
      ProtectionLib var2 = INSTANCE;

      Object $this$canUse_u24lambda_u2410;
      try {
         int var4 = 0;
         Iterable $this$all$iv = (Iterable)compatibilities;
         boolean var17;
         if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
            var17 = true;
         } else {
            Iterator var7 = $this$all$iv.iterator();

            while(true) {
               if (!var7.hasNext()) {
                  var17 = true;
                  break;
               }

               Object element$iv = var7.next();
               ProtectionCompatibility it = (ProtectionCompatibility)element$iv;
               int var10 = 0;
               if (!it.canUse(player, target)) {
                  var17 = false;
                  break;
               }
            }
         }

         $this$canUse_u24lambda_u2410 = Result.constructor-impl(var17);
      } catch (Throwable var11) {
         $this$canUse_u24lambda_u2410 = Result.constructor-impl(ResultKt.createFailure(var11));
      }

      Object var12 = $this$canUse_u24lambda_u2410;
      Throwable var18 = Result.exceptionOrNull-impl($this$canUse_u24lambda_u2410);
      if (var18 != null) {
         Throwable it = var18;
         int var15 = 0;
         if (debug) {
            it.printStackTrace();
         }
      }

      Boolean it = true;
      return (Boolean)(Result.isFailure-impl(var12) ? it : var12);
   }

   private final boolean checkFactionsCompat() {
      ProtectionLib var1 = this;

      Object $this$checkFactionsCompat_u24lambda_u2414;
      try {
         ProtectionLib $this$checkFactionsCompat_u24lambda_u2414 = var1;
         int var3 = 0;
         $this$checkFactionsCompat_u24lambda_u2414 = Result.constructor-impl(Class.forName("com.massivecraft.factions.perms.PermissibleActions"));
      } catch (Throwable var5) {
         $this$checkFactionsCompat_u24lambda_u2414 = Result.constructor-impl(ResultKt.createFailure(var5));
      }

      if (Result.exceptionOrNull-impl($this$checkFactionsCompat_u24lambda_u2414) != null) {
         int var4 = 0;
         Bukkit.getLogger().warning("It seems a Factions plugin is installed, but it is not FactionsUUID.");
         Bukkit.getLogger().warning("ProtectionLib will not be able to handle Factions protection.");
      }

      return Result.isSuccess-impl($this$checkFactionsCompat_u24lambda_u2414);
   }

   @Metadata(
      mv = {2, 0, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bâ\u0080\u0001\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0003J%\u0010\u0004\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00028\u0000H&¢\u0006\u0002\u0010\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/protectionlib/ProtectionLib$CompatibilityConstructor;", "T", "Lorg/bukkit/plugin/Plugin;", "", "create", "Lcom/nexomc/protectionlib/ProtectionCompatibility;", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "(Lorg/bukkit/plugin/java/JavaPlugin;Lorg/bukkit/plugin/Plugin;)Lcom/nexomc/protectionlib/ProtectionCompatibility;", "protectionlib"}
   )
   private interface CompatibilityConstructor<T extends Plugin> {
      @Nullable
      ProtectionCompatibility<T> create(@NotNull JavaPlugin var1, @NotNull T var2);
   }
}
