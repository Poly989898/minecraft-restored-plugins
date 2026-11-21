package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ChargedProjectiles;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemAdventurePredicate;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemContainerContents;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemTool;
import com.github.retrooper.packetevents.protocol.component.builtin.item.WritableBookContent;
import com.github.retrooper.packetevents.protocol.component.builtin.item.WrittenBookContent;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemAdventurePredicate.BlockPredicate;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile.Property;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.util.Filterable;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.Examiner;

public class ItemK extends PlayerCheck<ItemStackDecodeEvent> {
   private int maxContainerItems;
   private int maxTotalNestedItems;
   private int maxProfileProperties;
   private int maxProfileTotalBytes;
   private int maxChargedProjectiles;
   private int maxToolRules;
   private int maxCanBreakPredicates;
   private int maxCanBreakProperties;
   private int maxBookPages;
   private int maxBookPageBytes;

   public ItemK(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.maxContainerItems = this.getInt("max-container-items", 96);
      this.maxTotalNestedItems = this.getInt("max-total-nested-items", 256);
      this.maxProfileProperties = this.getInt("max-profile-properties", 16);
      this.maxProfileTotalBytes = this.getInt("max-profile-total-bytes", 8192);
      this.maxChargedProjectiles = this.getInt("max-charged-projectiles", 16);
      this.maxToolRules = this.getInt("max-tool-rules", 96);
      this.maxCanBreakPredicates = this.getInt("max-can-break-predicates", 192);
      this.maxCanBreakProperties = this.getInt("max-can-break-properties", 256);
      this.maxBookPages = this.getInt("max-book-pages", 100);
      this.maxBookPageBytes = this.getInt("max-book-page-bytes", 3072);
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, ItemStackDecodeEvent itemStackDecodeEvent) {
      return !protocolPlayer.usesComponents() ? CheckResult.ALLOW : this.visitItem(itemStackDecodeEvent.getItemStack(), new ItemK.VisitState(), itemStackDecodeEvent);
   }

   private CheckResult visitItem(ItemStack itemStack, ItemK.VisitState visitState, ItemStackDecodeEvent itemStackDecodeEvent) {
      if (itemStack != null && !itemStack.isEmpty()) {
         CheckResult checkResult = this.checkWritableBook(itemStack, itemStackDecodeEvent);
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }

         checkResult = this.checkProfile(itemStack, itemStackDecodeEvent);
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }

         checkResult = this.checkChargedProjectiles(itemStack, visitState, itemStackDecodeEvent);
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }

         checkResult = this.checkTool(itemStack, itemStackDecodeEvent);
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }

         checkResult = this.checkCanBreak(itemStack, itemStackDecodeEvent);
         return checkResult == CheckResult.DENY ? checkResult : this.checkContainer(itemStack, visitState, itemStackDecodeEvent);
      } else {
         return CheckResult.ALLOW;
      }
   }

   private CheckResult checkWritableBook(ItemStack itemStack, ItemStackDecodeEvent itemStackDecodeEvent) {
      WritableBookContent writableBookContent = (WritableBookContent)itemStack.getComponentOr(ComponentTypes.WRITABLE_BOOK_CONTENT, null);
      if (writableBookContent != null) {
         List list = writableBookContent.getPages();
         if (list.size() > this.maxBookPages) {
            this.raiseViolation(
               itemStackDecodeEvent,
               new FlagInfo[]{
                  FlagInfo.of("identifier", "writable-book-pages"), FlagInfo.of("pages", list.size()), FlagInfo.of("max-pages", this.maxBookPages)
               }
            );
            return CheckResult.DENY;
         }

         for (int value = 0; value < list.size(); value++) {
            CheckResult checkResult = this.checkStringPage("writable-book-page", (Filterable)list.get(value), value, itemStackDecodeEvent);
            if (checkResult == CheckResult.DENY) {
               return checkResult;
            }
         }
      }

      WrittenBookContent writtenBookContent = (WrittenBookContent)itemStack.getComponentOr(ComponentTypes.WRITTEN_BOOK_CONTENT, null);
      if (writtenBookContent == null) {
         return CheckResult.ALLOW;
      }

      List list2 = writtenBookContent.getPages();
      if (list2.size() > this.maxBookPages) {
         this.raiseViolation(
            itemStackDecodeEvent,
            new FlagInfo[]{FlagInfo.of("identifier", "written-book-pages"), FlagInfo.of("pages", list2.size()), FlagInfo.of("max-pages", this.maxBookPages)}
         );
         return CheckResult.DENY;
      }

      for (int value2 = 0; value2 < list2.size(); value2++) {
         CheckResult checkResult2 = this.checkComponentPage("written-book-page", (Filterable)list2.get(value2), value2, itemStackDecodeEvent);
         if (checkResult2 == CheckResult.DENY) {
            return checkResult2;
         }
      }

      return CheckResult.ALLOW;
   }

   private CheckResult checkProfile(ItemStack itemStack, ItemStackDecodeEvent itemStackDecodeEvent) {
      ItemProfile itemProfile = (ItemProfile)itemStack.getComponentOr(ComponentTypes.PROFILE, null);
      if (itemProfile == null) {
         return CheckResult.ALLOW;
      }

      List list = itemProfile.getProperties();
      if (list.size() > this.maxProfileProperties) {
         this.raiseViolation(
            itemStackDecodeEvent,
            new FlagInfo[]{
               FlagInfo.of("identifier", "profile-properties"),
               FlagInfo.of("properties", list.size()),
               FlagInfo.of("max-properties", this.maxProfileProperties)
            }
         );
         return CheckResult.DENY;
      }

      int value = 0;

      for (Object var17_o : list) { Property property = (Property) var17_o;
         value += this.byteSize(property.getName());
         value += this.byteSize(property.getValue());
         value += this.byteSize(property.getSignature());
      }

      if (value > this.maxProfileTotalBytes) {
         this.raiseViolation(
            itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("identifier", "profile-bytes"), FlagInfo.of("bytes", value), FlagInfo.of("max-bytes", this.maxProfileTotalBytes)}
         );
         return CheckResult.DENY;
      } else {
         return CheckResult.ALLOW;
      }
   }

   private CheckResult checkChargedProjectiles(ItemStack itemStack, ItemK.VisitState visitState, ItemStackDecodeEvent itemStackDecodeEvent) {
      ChargedProjectiles chargedProjectiles = (ChargedProjectiles)itemStack.getComponentOr(ComponentTypes.CHARGED_PROJECTILES, null);
      if (chargedProjectiles == null) {
         return CheckResult.ALLOW;
      }

      List list = chargedProjectiles.getItems();
      if (list.size() > this.maxChargedProjectiles) {
         this.raiseViolation(
            itemStackDecodeEvent,
            new FlagInfo[]{
               FlagInfo.of("identifier", "charged-projectiles"),
               FlagInfo.of("projectiles", list.size()),
               FlagInfo.of("max-projectiles", this.maxChargedProjectiles)
            }
         );
         return CheckResult.DENY;
      }

      CheckResult checkResult = this.addNestedItems(list.size(), "charged-projectiles", visitState, itemStackDecodeEvent);
      if (checkResult == CheckResult.DENY) {
         return checkResult;
      }

      for (Object var20_o : list) { ItemStack itemStack2 = (ItemStack) var20_o;
         checkResult = this.visitItem(itemStack2, visitState, itemStackDecodeEvent);
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }
      }

      return CheckResult.ALLOW;
   }

   private CheckResult checkTool(ItemStack itemStack, ItemStackDecodeEvent itemStackDecodeEvent) {
      ItemTool itemTool = (ItemTool)itemStack.getComponentOr(ComponentTypes.TOOL, null);
      if (itemTool == null) {
         return CheckResult.ALLOW;
      }

      int value = itemTool.getRules().size();
      if (value <= this.maxToolRules) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(itemStackDecodeEvent, new FlagInfo[]{FlagInfo.of("identifier", "tool-rules"), FlagInfo.of("rules", value), FlagInfo.of("max-rules", this.maxToolRules)});
      return CheckResult.DENY;
   }

   private CheckResult checkCanBreak(ItemStack itemStack, ItemStackDecodeEvent itemStackDecodeEvent) {
      ItemAdventurePredicate itemAdventurePredicate = (ItemAdventurePredicate)itemStack.getComponentOr(ComponentTypes.CAN_BREAK, null);
      if (itemAdventurePredicate == null) {
         return CheckResult.ALLOW;
      }

      List list = itemAdventurePredicate.getPredicates();
      if (list.size() > this.maxCanBreakPredicates) {
         this.raiseViolation(
            itemStackDecodeEvent,
            new FlagInfo[]{
               FlagInfo.of("identifier", "can-break-predicates"),
               FlagInfo.of("predicates", list.size()),
               FlagInfo.of("max-predicates", this.maxCanBreakPredicates)
            }
         );
         return CheckResult.DENY;
      }

      int value = 0;

      for (Object var14_o : list) { BlockPredicate blockPredicate = (BlockPredicate) var14_o;
         List list2 = blockPredicate.getProperties();
         if (list2 != null) {
            value += list2.size();
         }
      }

      if (value <= this.maxCanBreakProperties) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(
         itemStackDecodeEvent,
         new FlagInfo[]{
            FlagInfo.of("identifier", "can-break-properties"), FlagInfo.of("properties", value), FlagInfo.of("max-properties", this.maxCanBreakProperties)
         }
      );
      return CheckResult.DENY;
   }

   private CheckResult checkContainer(ItemStack itemStack, ItemK.VisitState visitState, ItemStackDecodeEvent itemStackDecodeEvent) {
      ItemContainerContents itemContainerContents = (ItemContainerContents)itemStack.getComponentOr(ComponentTypes.CONTAINER, null);
      if (itemContainerContents == null) {
         return CheckResult.ALLOW;
      }

      List list = itemContainerContents.getItems();
      if (list.size() > this.maxContainerItems) {
         this.raiseViolation(
            itemStackDecodeEvent,
            new FlagInfo[]{FlagInfo.of("identifier", "container-items"), FlagInfo.of("items", list.size()), FlagInfo.of("max-items", this.maxContainerItems)}
         );
         return CheckResult.DENY;
      }

      CheckResult checkResult = this.addNestedItems(list.size(), "container-items", visitState, itemStackDecodeEvent);
      if (checkResult == CheckResult.DENY) {
         return checkResult;
      }

      for (Object var20_o : list) { ItemStack itemStack2 = (ItemStack) var20_o;
         checkResult = this.visitItem(itemStack2, visitState, itemStackDecodeEvent);
         if (checkResult == CheckResult.DENY) {
            return checkResult;
         }
      }

      return CheckResult.ALLOW;
   }

   private CheckResult addNestedItems(int value, Object object, ItemK.VisitState visitState, ItemStackDecodeEvent itemStackDecodeEvent) {
      if (value <= 0) {
         return CheckResult.ALLOW;
      }

      visitState.totalNestedItems += value;
      if (visitState.totalNestedItems <= this.maxTotalNestedItems) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(
         itemStackDecodeEvent,
         new FlagInfo[]{
            FlagInfo.of("identifier", object),
            FlagInfo.of("total-nested-items", visitState.totalNestedItems),
            FlagInfo.of("max-total-nested-items", this.maxTotalNestedItems)
         }
      );
      return CheckResult.DENY;
   }

   private CheckResult checkStringPage(String string, Filterable filterable, int value, ItemStackDecodeEvent itemStackDecodeEvent) {
      int value2 = this.byteSize((String)filterable.getRaw());
      if (value2 > this.maxBookPageBytes) {
         this.raiseViolation(
            itemStackDecodeEvent,
            new FlagInfo[]{
               FlagInfo.of("identifier", string),
               FlagInfo.of("page-index", value),
               FlagInfo.of("page-bytes", value2),
               FlagInfo.of("max-page-bytes", this.maxBookPageBytes)
            }
         );
         return CheckResult.DENY;
      }

      String string2 = (String)filterable.getFiltered();
      if (string2 == null) {
         return CheckResult.ALLOW;
      }

      int value3 = this.byteSize(string2);
      if (value3 <= this.maxBookPageBytes) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(
         itemStackDecodeEvent,
         new FlagInfo[]{
            FlagInfo.of("identifier", string + "-filtered"),
            FlagInfo.of("page-index", value),
            FlagInfo.of("page-bytes", value3),
            FlagInfo.of("max-page-bytes", this.maxBookPageBytes)
         }
      );
      return CheckResult.DENY;
   }

   private CheckResult checkComponentPage(String string, Filterable filterable, int value, ItemStackDecodeEvent itemStackDecodeEvent) {
      int value2 = this.componentSize((Component)filterable.getRaw());
      if (value2 > this.maxBookPageBytes) {
         this.raiseViolation(
            itemStackDecodeEvent,
            new FlagInfo[]{
               FlagInfo.of("identifier", string),
               FlagInfo.of("page-index", value),
               FlagInfo.of("page-bytes", value2),
               FlagInfo.of("max-page-bytes", this.maxBookPageBytes)
            }
         );
         return CheckResult.DENY;
      }

      Component component = (Component)filterable.getFiltered();
      if (component == null) {
         return CheckResult.ALLOW;
      }

      int value3 = this.componentSize(component);
      if (value3 <= this.maxBookPageBytes) {
         return CheckResult.ALLOW;
      }

      this.raiseViolation(
         itemStackDecodeEvent,
         new FlagInfo[]{
            FlagInfo.of("identifier", string + "-filtered"),
            FlagInfo.of("page-index", value),
            FlagInfo.of("page-bytes", value3),
            FlagInfo.of("max-page-bytes", this.maxBookPageBytes)
         }
      );
      return CheckResult.DENY;
   }

   private int componentSize(Component component) {
      return this.size(component, this.maxBookPageBytes + 1);
   }

   private int size(Object object, int value) {
      if (object == null || value <= 0) {
         return 0;
      } else if (object instanceof String string) {
         return Math.min(value, this.byteSize(string));
      } else if (object instanceof Key key) {
         return Math.min(value, this.byteSize(key.asString()));
      } else if (object instanceof TextColor textColor) {
         return Math.min(value, this.byteSize(textColor.asHexString()));
      } else if (object instanceof ClickEvent clickEvent) {
         int value2 = this.size(clickEvent.action(), value);
         return value2 >= value ? value : value2 + this.size(clickEvent.value(), value - value2);
      } else if (object instanceof HoverEvent hoverEvent) {
         int value3 = this.size(hoverEvent.action(), value);
         return value3 >= value ? value : value3 + this.size(hoverEvent.value(), value - value3);
      } else if (object instanceof Optional optional) {
         return this.size(optional.orElse(null), value);
      } else if (object instanceof Map map) {
         int value4 = 0;

         for (Object var38_o : map.entrySet()) { Entry entry = (Entry) var38_o;
            value4 += this.size(entry.getKey(), value - value4);
            if (value4 >= value) {
               return value;
            }

            value4 += this.size(entry.getValue(), value - value4);
            if (value4 >= value) {
               return value;
            }
         }

         return value4;
      } else if (object instanceof Iterable iterable) {
         int value5 = 0;

         for (Object object2 : iterable) {
            value5 += this.size(object2, value - value5);
            if (value5 >= value) {
               return value;
            }
         }

         return value5;
      } else if (object instanceof Examinable examinable) {
         int value6 = 0;

         for (ExaminableProperty examinableProperty : examinable.examinableProperties().toList()) {
            value6 += this.size(examinableProperty.name(), value - value6);
            if (value6 >= value) {
               return value;
            }

            value6 += Math.min(value - value6, (Integer)examinableProperty.examine(ItemK.ComponentExaminer.componentExaminer));
            if (value6 >= value) {
               return value;
            }
         }

         return value6;
      } else if (object.getClass().isArray()) {
         int value7 = 0;
         int value8 = Array.getLength(object);

         for (int value9 = 0; value9 < value8; value9++) {
            value7 += this.size(Array.get(object, value9), value - value7);
            if (value7 >= value) {
               return value;
            }
         }

         return value7;
      } else if (object instanceof Enum enum2) {
         return Math.min(value, this.byteSize(enum2.name()));
      } else {
         return !(object instanceof Number) && !(object instanceof Boolean) && !(object instanceof Character)
            ? 0
            : Math.min(value, this.byteSize(String.valueOf(object)));
      }
   }

   private int byteSize(String string) {
      return string == null ? 0 : string.getBytes(StandardCharsets.UTF_8).length;
   }

   @Override
   public boolean isPreVia() {
      return true;
   }


   static final class ComponentExaminer implements Examiner<Integer> {
      private static final ItemK.ComponentExaminer componentExaminer = new ItemK.ComponentExaminer();

      private ComponentExaminer() {
      }

      public Integer examine(String string, Stream stream) {
         int value = byteSize(string);

         for (Object var15_o : stream.toList()) { ExaminableProperty examinableProperty = (ExaminableProperty) var15_o;
            value += byteSize(examinableProperty.name());
            value += examinableProperty.examine(this);
         }

         return value;
      }

      public Integer examine(Object object) {
         if (object == null) {
            return 0;
         } else if (object instanceof String string) {
            return byteSize(string);
         } else if (object instanceof Key key) {
            return byteSize(key.asString());
         } else if (object instanceof TextColor textColor) {
            return byteSize(textColor.asHexString());
         } else if (object instanceof Enum enum2) {
            return byteSize(enum2.name());
         } else if (object instanceof Number || object instanceof Boolean || object instanceof Character) {
            return byteSize(String.valueOf(object));
         } else if (object instanceof ClickEvent clickEvent) {
            return this.examine(clickEvent.action()) + this.examine(clickEvent.value());
         } else if (object instanceof HoverEvent hoverEvent) {
            return this.examine(hoverEvent.action()) + this.examine(hoverEvent.value());
         } else if (object instanceof Optional optional) {
            return this.examine(optional.orElse(null));
         } else if (object instanceof Examinable examinable) {
            return (Integer)this.examine(examinable);
         } else if (object instanceof Map map) {
            int value = 0;

            for (Object var31_o : map.entrySet()) { Entry entry = (Entry) var31_o;
               value += this.examine(entry.getKey());
               value += this.examine(entry.getValue());
            }

            return value;
         } else if (object instanceof Iterable iterable) {
            int value2 = 0;

            for (Object object2 : iterable) {
               value2 += this.examine(object2);
            }

            return value2;
         } else {
            if (!object.getClass().isArray()) {
               return 0;
            }

            int value3 = 0;
            int value4 = Array.getLength(object);

            for (int value5 = 0; value5 < value4; value5++) {
               value3 += this.examine(Array.get(object, value5));
            }

            return value3;
         }
      }

      public Integer examine(boolean flag) {
         return byteSize(String.valueOf(flag));
      }

      public Integer examine(boolean[] flags) {
         boolean[] flags2 = flags;
         return examineArray(flags2 == null ? 0 : flags2.length);
      }

      public Integer examine(byte b) {
         return byteSize(String.valueOf(b));
      }

      public Integer examine(byte[] bs) {
         byte[] bs2 = bs;
         return examineArray(bs2 == null ? 0 : bs2.length);
      }

      public Integer examine(char ch) {
         return byteSize(String.valueOf((char)ch));
      }

      public Integer examine(char[] chs) {
         char[] chs2 = chs;
         return chs2 == null ? 0 : byteSize(new String(chs2));
      }

      public Integer examine(double number) {
         return byteSize(String.valueOf(number));
      }

      public Integer examine(double[] numbers) {
         double[] numbers2 = numbers;
         return examineArray(numbers2 == null ? 0 : numbers2.length);
      }

      public Integer examine(float floatValue) {
         return byteSize(String.valueOf(floatValue));
      }

      public Integer examine(float[] floatValues) {
         float[] floatValues2 = floatValues;
         return examineArray(floatValues2 == null ? 0 : floatValues2.length);
      }

      public Integer examine(int value) {
         return byteSize(String.valueOf(value));
      }

      public Integer examine(int[] values) {
         int[] values2 = values;
         return examineArray(values2 == null ? 0 : values2.length);
      }

      public Integer examine(long longValue) {
         return byteSize(String.valueOf(longValue));
      }

      public Integer examine(long[] longValues) {
         long[] longValues2 = longValues;
         return examineArray(longValues2 == null ? 0 : longValues2.length);
      }

      public Integer examine(short shortValue) {
         return byteSize(String.valueOf(shortValue));
      }

      public Integer examine(short[] shortValues) {
         short[] shortValues2 = shortValues;
         return examineArray(shortValues2 == null ? 0 : shortValues2.length);
      }

      public Integer examine(String string) {
         return byteSize(string);
      }

      private static int examineArray(int value) {
         return byteSize(String.valueOf(value));
      }

      private static int byteSize(String string) {
         return string == null ? 0 : string.getBytes(StandardCharsets.UTF_8).length;
      }

   }

   static class VisitState {
      private int totalNestedItems;

      private VisitState() {
      }
   }
}
