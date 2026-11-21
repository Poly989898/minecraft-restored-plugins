package io.lumine.mythic.core.spawning.spawners;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Effect;

public class SpawnerSlice {
   protected MythicSpawner spawner;
   protected AbstractLocation origin;
   protected int offset_x;
   protected int offset_y;
   protected int offset_z;
   public static List<SpawnerSlice> CutBuffer = new ArrayList<>();
   public static boolean cut = true;

   public static void CutSpawners(Collection<MythicSpawner> msl, AbstractLocation base) {
      CutBuffer.clear();

      for (MythicSpawner ms : msl) {
         CutBuffer.add(new SpawnerSlice(ms, base));
      }

      cut = true;
   }

   public static boolean PasteSpawners(AbstractLocation base) {
      if (CutBuffer.size() == 0) {
         return false;
      } else {
         if (cut) {
            for (SpawnerSlice ss : CutBuffer) {
               AbstractLocation l = ss.getRelativeLocation(base);
               if (MythicBukkit.inst().getSpawnerManager().moveSpawner(ss.getSpawner(), l)) {
                  BukkitAdapter.adapt(l).getWorld().playEffect(BukkitAdapter.adapt(l), Effect.MOBSPAWNER_FLAMES, 0);
                  BukkitAdapter.adapt(l).getWorld().playEffect(BukkitAdapter.adapt(l), Effect.EXTINGUISH, 0);
               }
            }
         }

         return true;
      }
   }

   public static boolean Undo() {
      if (CutBuffer.size() == 0) {
         return false;
      } else {
         if (cut) {
            for (SpawnerSlice ss : CutBuffer) {
               AbstractLocation l = ss.getOriginalLocation();
               if (MythicBukkit.inst().getSpawnerManager().moveSpawner(ss.getSpawner(), l)) {
                  BukkitAdapter.adapt(l.getWorld()).playEffect(BukkitAdapter.adapt(l), Effect.MOBSPAWNER_FLAMES, 0);
                  BukkitAdapter.adapt(l.getWorld()).playEffect(BukkitAdapter.adapt(l), Effect.EXTINGUISH, 0);
               }
            }
         }

         return true;
      }
   }

   public SpawnerSlice(MythicSpawner ms, AbstractLocation base) {
      this.spawner = ms;
      AbstractLocation l = ms.getLocation();
      this.offset_x = l.getBlockX() - base.getBlockX();
      this.offset_y = l.getBlockY() - base.getBlockY();
      this.offset_z = l.getBlockZ() - base.getBlockZ();
      this.origin = ms.getLocation();
   }

   public MythicSpawner getSpawner() {
      return this.spawner;
   }

   public AbstractLocation getOriginalLocation() {
      return this.origin;
   }

   public AbstractLocation getRelativeLocation(AbstractLocation base) {
      AbstractLocation l = new AbstractLocation(base.getWorld(), 0.0, 0.0, 0.0);
      l.setX(base.getBlockX() + this.offset_x);
      l.setY(base.getBlockY() + this.offset_y);
      l.setZ(base.getBlockZ() + this.offset_z);
      return l;
   }
}
