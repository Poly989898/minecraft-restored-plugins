package it.ytnoos.lpx.check.checks.flood;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.RepeatedIntervalCounter;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import it.ytnoos.lpx.player.BukkitProtocolPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class FloodB extends PlayerCheck<PacketReceiveEvent> {
   private Map<PacketTypeCommon, List<FloodB.PacketLimit>> packetLimits;

   public FloodB(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.packetLimits = new HashMap<>();
      ConfigurationSection configurationSection = this.getConfig().getConfigurationSection(this.getPath("limits"));
      if (configurationSection != null) {
         for (String string : configurationSection.getKeys(false)) {
            ArrayList arrayList = new ArrayList();
            if (configurationSection.isList(string)) {
               for (Map map : configurationSection.getMapList(string)) {
                  arrayList.add(this.parsePacketLimit(map));
               }
            } else {
               ConfigurationSection configurationSection2 = configurationSection.getConfigurationSection(string);
               if (configurationSection2 != null) {
                  arrayList.add(this.parsePacketLimit(configurationSection2));
               }
            }

            if (!arrayList.isEmpty()) {
               this.registerLimits(string, arrayList);
            }
         }
      } else {
         for (String string2 : this.getConfig().getStringList(this.getPath("limits"))) {
            String[] strings = string2.split(",");
            int value = strings.length;
            if (value < 2) {
               ((BukkitProtocolPlayer)this.player).getLpxBukkit().getLogger().warning(() -> string2 + " is not a valid FloodB parameter!");
            } else {
               try {
                  int value2 = Integer.parseInt(strings[1]);
                  RepeatedIntervalCounter repeatedIntervalCounter;
                  if (value == 2) {
                     repeatedIntervalCounter = new RepeatedIntervalCounter(value2, 6, 3);
                  } else if (value == 3) {
                     repeatedIntervalCounter = new RepeatedIntervalCounter(value2, Integer.parseInt(strings[2]), 3, 1);
                  } else {
                     repeatedIntervalCounter = new RepeatedIntervalCounter(value2, Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]));
                  }

                  this.registerFallbackLimit(strings[0], new FloodB.PacketLimit(repeatedIntervalCounter, false, -1L, -1L, false));
               } catch (NumberFormatException numberFormatException) {
                  ((BukkitProtocolPlayer)this.player).getLpxBukkit().getLogger().severe("Second and third parameter must be numbers for FloodB!");
               }
            }
         }
      }
   }

   private FloodB.PacketLimit parsePacketLimit(ConfigurationSection configurationSection) {
      int value = configurationSection.getInt("max", 20);
      int value2 = configurationSection.getInt("interval", 500);
      int value3 = configurationSection.getInt("periods", 5);
      int value4 = configurationSection.getInt("warnings", 2);
      boolean flag = "drop".equalsIgnoreCase(configurationSection.getString("action", "flag"));
      long longValue = configurationSection.getLong("min-bytes", -1L);
      long longValue2 = configurationSection.getLong("max-bytes", -1L);
      boolean flag2 = configurationSection.getBoolean("allow-duplicates", false);
      return new FloodB.PacketLimit(new RepeatedIntervalCounter(value, value2, value3, value4), flag, longValue, longValue2, flag2);
   }

   private FloodB.PacketLimit parsePacketLimit(Map map) {
      int value = intValue(map, "max", 20);
      int value2 = intValue(map, "interval", 500);
      int value3 = intValue(map, "periods", 5);
      int value4 = intValue(map, "warnings", 2);
      Object object = map.get("action");
      boolean flag = "drop".equalsIgnoreCase(object != null ? String.valueOf(object) : "flag");
      long longValue = longValue(map, "min-bytes", -1L);
      long longValue2 = longValue(map, "max-bytes", -1L);
      boolean flag2 = booleanValue(map, "allow-duplicates", false);
      return new FloodB.PacketLimit(new RepeatedIntervalCounter(value, value2, value3, value4), flag, longValue, longValue2, flag2);
   }

   private static int intValue(Map map, Object object, int value) {
      return map.get(object) instanceof Number number ? number.intValue() : value;
   }

   private static long longValue(Map map, Object object, long longValue) {
      return map.get(object) instanceof Number number ? number.longValue() : longValue;
   }

   private static boolean booleanValue(Map map, Object object, boolean flag) {
      Object object2 = map.get(object);
      if (object2 instanceof Boolean boolean2) {
         return boolean2;
      } else {
         return object2 instanceof String string ? Boolean.parseBoolean(string) : flag;
      }
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, PacketReceiveEvent packetReceiveEvent) {
      if (protocolPlayer.getPrinterData().isUsingPrinter()) {
         return CheckResult.ALLOW;
      }

      FloodB.PacketProfile packetProfile = this.createProfile(protocolPlayer, packetReceiveEvent);
      List list = this.getLimits(packetProfile);
      if (list == null) {
         return CheckResult.ALLOW;
      }

      double number = this.getWeight(packetReceiveEvent.getPacketType(), protocolPlayer);
      boolean flag = false;
      long longValue = System.currentTimeMillis();

      for (Object var26_o : list) { FloodB.PacketLimit packetLimit = (FloodB.PacketLimit) var26_o;
         if (packetLimit.matchesSize(packetProfile.packetBytes()) && !packetLimit.isRepeated(packetProfile.packetBytes(), longValue)) {
            boolean flag2 = packetLimit.counter.increment(number);
            if (flag2) {
               this.raiseViolation(
                  packetReceiveEvent,
                  new FlagInfo[]{
                     FlagInfo.of("packet", packetProfile.packetType().getName()),
                     FlagInfo.of("max-counter", packetLimit.counter.getMaxCounter()),
                     FlagInfo.of("interval", packetLimit.counter.getInterval() + "ms"),
                     FlagInfo.of("max-periods", packetLimit.counter.getMaxPeriods()),
                     FlagInfo.of("max-warnings", packetLimit.counter.getMaxWarnings())
                  }
               );
            }

            if (packetLimit.drop && (flag2 || packetLimit.counter.isOverLimit())) {
               flag = true;
            }
         }
      }

      return flag ? CheckResult.DENY : CheckResult.ALLOW;
   }

   private List getLimits(FloodB.PacketProfile packetProfile) {
      return this.packetLimits.get(packetProfile.packetType());
   }

   private FloodB.PacketProfile createProfile(ProtocolPlayer protocolPlayer, PacketReceiveEvent packetReceiveEvent) {
      return new FloodB.PacketProfile(packetReceiveEvent.getPacketType(), this.getPacketSize(protocolPlayer));
   }

   private long getPacketSize(ProtocolPlayer protocolPlayer) {
      return protocolPlayer.getLastReceivedPacket() != null ? protocolPlayer.getLastReceivedPacket().getSize() : -1L;
   }

   private void registerLimits(String string, Object object) {
      String string2 = normalizeKey(string);
      Client client = this.parsePacketType(string2);
      if (client == null) {
         this.warnInvalidPacketKey(string);
      } else {
         this.packetLimits.put(client, (List<FloodB.PacketLimit>)object);
      }
   }

   private void registerFallbackLimit(String string, Object object) {
      String string2 = normalizeKey(string);
      Client client = this.parsePacketType(string2);
      if (client == null) {
         this.warnInvalidPacketKey(string);
      } else {
         this.packetLimits.computeIfAbsent(client, packetTypeCommon -> new ArrayList<>()).add((FloodB.PacketLimit)object);
      }
   }

   private Client parsePacketType(String string) {
      try {
         return Client.valueOf(string);
      } catch (IllegalArgumentException illegalArgumentException) {
         return null;
      }
   }

   private void warnInvalidPacketKey(String string) {
      ((BukkitProtocolPlayer)this.player).getLpxBukkit().getLogger().warning(() -> "Invalid FloodB packet key '" + string + "' in config, skipping it.");
   }

   private static String normalizeKey(String string) {
      return string.trim().toUpperCase(Locale.ROOT);
   }

   private double getWeight(PacketTypeCommon packetTypeCommon, ProtocolPlayer protocolPlayer) {
      return packetTypeCommon != Client.CLICK_WINDOW && packetTypeCommon != Client.CREATIVE_INVENTORY_ACTION ? 1.0 : protocolPlayer.itemFloodMultiplier();
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

   static final class PacketLimit {
      private final RepeatedIntervalCounter counter;
      private final boolean drop;
      private final long minBytes;
      private final long maxBytes;
      private final boolean detectRepeats;
      private long lastBytes = -9223372036854775808L;
      private long lastTime = -9223372036854775808L;

      private PacketLimit(RepeatedIntervalCounter repeatedIntervalCounter, boolean flag, long longValue, long longValue2, boolean flag2) {
         this.counter = repeatedIntervalCounter;
         this.drop = flag;
         this.minBytes = longValue;
         this.maxBytes = longValue2;
         this.detectRepeats = flag2;
      }

      public RepeatedIntervalCounter counter() {
         return this.counter;
      }

      public boolean drop() {
         return this.drop;
      }

      public boolean matchesSize(long longValue) {
         if (longValue < 0L) {
            return this.minBytes < 0L && this.maxBytes < 0L;
         } else {
            return this.minBytes >= 0L && longValue < this.minBytes ? false : this.maxBytes < 0L || longValue <= this.maxBytes;
         }
      }

      public boolean isRepeated(long longValue, long longValue2) {
         if (this.detectRepeats && longValue >= 0L) {
            boolean flag = this.lastBytes == longValue && longValue2 - this.lastTime <= this.counter.getInterval();
            this.lastBytes = longValue;
            this.lastTime = longValue2;
            return flag;
         } else {
            return false;
         }
      }

   }

   record PacketProfile(PacketTypeCommon packetType, long packetBytes) {
   }
}
