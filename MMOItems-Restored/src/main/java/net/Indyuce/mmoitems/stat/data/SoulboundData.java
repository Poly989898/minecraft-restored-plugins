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

   public SoulboundData(Player player, int count) {
      this(player.getUniqueId(), player.getName(), count);
   }

   public SoulboundData(UUID uuid, String text, int count) {
      this.uuid = uuid;
      this.name = text;
      this.level = count;
   }

   public SoulboundData(JsonObject jsonObject) {
      this.uuid = UUID.fromString(jsonObject.get("UUID").getAsString());
      this.name = jsonObject.get("Name").getAsString();
      this.level = jsonObject.get("Level").getAsInt();
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
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("Level", this.level);
      jsonObject.addProperty("Name", this.name);
      jsonObject.addProperty("UUID", this.uuid.toString());
      return jsonObject;
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
