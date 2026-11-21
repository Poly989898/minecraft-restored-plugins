package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class AbstractItemBlockStateComponent implements AbstractItemComponent {
   private final Map<String, String> states = new HashMap<>();

   public AbstractItemBlockStateComponent(MythicItem mythicItem, MythicConfig config) {
      for (String line : config.getStringList("BlockStates")) {
         String[] split = line.split(" ");
         if (split.length >= 2) {
            String key = split[0];
            String value = split[1];
            this.states.put(key, value);
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyBlockStateComponent(dropMetadata, this);
   }
}
