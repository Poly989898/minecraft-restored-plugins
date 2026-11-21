package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.component.builtin.item.WrittenBookContent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.util.Filterable;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TranslatableComponent;

public class ItemH extends PlayerCheck<ItemStackDecodeEvent> {
   private int maxTranslate;
   private int maxKeybind;

   public ItemH(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxTranslate = this.getConfig().getInt(this.getPath("max-translate"), 1);
      this.maxKeybind = this.getConfig().getInt(this.getPath("max-keybind"), 1);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, ItemStackDecodeEvent itemStackDecodeEvent) {
      if (!protocolPlayer.usesComponents()) {
         return CheckResult.ALLOW;
      }

      ItemStack itemStack = itemStackDecodeEvent.getItemStack();
      CheckResult checkResult = CheckResult.ALLOW;
      Component component = (Component)itemStack.getComponentOr(ComponentTypes.CUSTOM_NAME, null);
      if (component != null) {
         checkResult = this.z8(component, "custom_name", itemStackDecodeEvent);
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }
      }

      Component component2 = (Component)itemStack.getComponentOr(ComponentTypes.ITEM_NAME, null);
      if (component2 != null) {
         checkResult = this.z8(component2, "item_name", itemStackDecodeEvent);
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }
      }

      ItemLore itemLore = (ItemLore)itemStack.getComponentOr(ComponentTypes.LORE, null);
      if (itemLore != null) {
         for (int value = 0; value < itemLore.getLines().size(); value++) {
            checkResult = this.z8((Component)itemLore.getLines().get(value), "lore[" + value + "]", itemStackDecodeEvent);
            if (checkResult == CheckResult.DENY) {
               return checkResult;
            }
         }
      }

      WrittenBookContent writtenBookContent = (WrittenBookContent)itemStack.getComponentOr(ComponentTypes.WRITTEN_BOOK_CONTENT, null);
      if (writtenBookContent != null) {
         for (int value2 = 0; value2 < writtenBookContent.getPages().size(); value2++) {
            Filterable filterable = (Filterable)writtenBookContent.getPages().get(value2);
            checkResult = this.z8((Component)filterable.getRaw(), "page[" + value2 + "]", itemStackDecodeEvent);
            if (checkResult == CheckResult.DENY) {
               return checkResult;
            }

            if (filterable.getFiltered() != null) {
               checkResult = this.z8((Component)filterable.getFiltered(), "page_filtered[" + value2 + "]", itemStackDecodeEvent);
               if (checkResult == CheckResult.DENY) {
                  return checkResult;
               }
            }
         }
      }

      return checkResult;
   }

   private CheckResult z8(Component component, Object object, ItemStackDecodeEvent itemStackDecodeEvent) {
      int[] values = new int[2];
      this.countComponents(component, values);
      boolean flag = values[0] > this.maxTranslate;
      boolean flag2 = values[1] > this.maxKeybind;
      if (!flag && !flag2) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("identifier", object), FlagInfo.of("translate", values[0]), FlagInfo.of("keybind", values[1])});
      return CheckResult.DENY;
   }

   private boolean shouldStop(int[] values) {
      int[] values2 = values;
      return values2[0] > this.maxTranslate || values2[1] > this.maxKeybind;
   }

   private void countComponents(Component component, int[] values) {
      int[] values2 = values;
      if (!this.shouldStop(values2)) {
         if (component instanceof TranslatableComponent translatableComponent) {
            values2[0]++;

            for (net.kyori.adventure.text.TranslationArgument var14a : translatableComponent.arguments()) {
               Component component2 = var14a.asComponent();
               if (this.shouldStop(values2)) {
                  return;
               }

               this.countComponents(component2, values2);
            }
         }

         if (component instanceof KeybindComponent) {
            values2[1]++;
         }

         for (Component component3 : component.children()) {
            if (this.shouldStop(values2)) {
               return;
            }

            this.countComponents(component3, values2);
         }
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
