package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.gson.JsonObject;
import java.util.UUID;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoulboundData implements StatData {
   private final UUID uuid;
   private final String name;
   private final int level;

   public SoulboundData(Player var1, int var2) {
      this(var1.getUniqueId(), var1.getName(), var2);
   }

   public SoulboundData(UUID var1, String var2, int var3) {
      this.uuid = var1;
      this.name = var2;
      this.level = var3;
   }

   public SoulboundData(JsonObject var1) {
      this.uuid = UUID.fromString(var1.get("UUID").getAsString());
      this.name = var1.get("Name").getAsString();
      this.level = var1.get("Level").getAsInt();
   }

   public UUID getUniqueId() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }

   public int getLevel() {
      return this.level;
   }

   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("Level", this.level);
      var1.addProperty("Name", this.name);
      var1.addProperty("UUID", this.uuid.toString());
      return var1;
   }

   @NotNull
   public StatData clone() {
      return this;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }
}
