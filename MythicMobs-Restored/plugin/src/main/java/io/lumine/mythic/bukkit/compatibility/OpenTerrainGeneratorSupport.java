package io.lumine.mythic.bukkit.compatibility;

import com.pg85.otg.bukkit.BukkitWorld;
import com.pg85.otg.bukkit.OTGPlugin;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.events.Subscription;
import io.lumine.mythic.core.skills.SkillCondition;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class OpenTerrainGeneratorSupport implements Listener {
   private OTGPlugin otg = (OTGPlugin)Bukkit.getPluginManager().getPlugin("OpenTerrainGenerator");
   private Subscription loader = Events.subscribe(MythicConditionLoadEvent.class).handler(event -> {
      String conditionName = event.getConditionName();
      switch (conditionName) {
         case "OTGBIOME":
         case "OPENTERRAINGENERATORBIOME":
         case "TCBIOME":
         case "TERRAINCONTROLBIOME":
            event.register(new OpenTerrainGeneratorSupport.OTGBiomeCondition(event.getConditionName(), event.getConfig()));
      }
   });

   public String getBiome(AbstractLocation location) {
      String worldName = location.getWorld().getName();
      String biome;
      if (this.otg.worlds.containsKey(worldName)) {
         biome = ((BukkitWorld)this.otg.worlds.get(worldName)).getBiome(location.getBlockX(), location.getBlockZ()).getName();
      } else {
         biome = location.getBiome().toString();
      }

      return biome;
   }

   public class OTGBiomeCondition extends SkillCondition implements ILocationCondition {
      private Set<String> biomes = new HashSet<>();
      private boolean strict = false;

      public OTGBiomeCondition(String line, MythicLineConfig mlc) {
         super(line);
         String b = mlc.getString(new String[]{"biome", "b"}, "PLAINS", this.conditionVar).toUpperCase();
         this.strict = mlc.getBoolean(new String[]{"strict", "s"}, false);

         for (String s : b.split(",")) {
            this.biomes.add(s);
         }
      }

      @Override
      public boolean check(AbstractLocation location) {
         if (!MythicBukkit.inst().getCompatibility().getOpenTerrainGenerator().isPresent()) {
            return false;
         } else {
            String biome = OpenTerrainGeneratorSupport.this.getBiome(location).toUpperCase();
            if (this.strict) {
               return this.biomes.contains(location.getBiome().toString());
            } else {
               for (String b : this.biomes) {
                  if (biome.contains(b)) {
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }
}
