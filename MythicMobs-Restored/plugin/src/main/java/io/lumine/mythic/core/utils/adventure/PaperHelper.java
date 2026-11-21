package io.lumine.mythic.core.utils.adventure;

import io.lumine.mythic.bukkit.utils.text.Text;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class PaperHelper {
   public static Component parse(String string) {
      return PaperComponents.gsonSerializer().deserialize((String)GsonComponentSerializer.gson().serialize(Text.parse(string)));
   }
}
