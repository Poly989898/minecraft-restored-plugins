package io.lumine.mythic.lib.api.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.handler.StatHandler;
import io.lumine.mythic.lib.api.stat.provider.PlayerStatProvider;
import io.lumine.mythic.lib.player.PlayerMetadata;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class StatMap implements PlayerStatProvider {
   private final MMOPlayerData data;
   private final Map<String, StatInstance> stats = new ConcurrentHashMap<>();

   public StatMap(MMOPlayerData player) {
      this.data = player;
   }

   @NotNull
   @Override
   public MMOPlayerData getData() {
      return this.data;
   }

   @Deprecated
   public MMOPlayerData getPlayerData() {
      return this.data;
   }

   @Override
   public double getStat(String stat) {
      return this.getInstance(stat).getFinal();
   }

   @NotNull
   public StatInstance getInstance(String stat) {
      return this.stats.computeIfAbsent(stat, statId -> new StatInstance(this, statId));
   }

   @NotNull
   public Collection<StatInstance> getInstances() {
      return this.stats.values();
   }

   public void updateAll() {
      for (StatHandler handler : MythicLib.plugin.getStats().getHandlers()) {
         StatInstance ins = this.stats.get(handler.getStat());
         if (ins != null) {
            ins.update();
         }
      }
   }

   public void flushCache() {
      this.stats.values().forEach(StatInstance::flushCache);
   }

   @NotNull
   @Override
   public PlayerMetadata cache(@NotNull EquipmentSlot castHand) {
      return new PlayerMetadata(this, castHand);
   }

   @Deprecated
   public void update(String stat) {
      StatInstance ins = this.stats.get(stat);
      if (ins != null) {
         ins.update();
      }
   }
}
