package io.lumine.mythic.core.menus;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.compatibility.CrucibleSupport;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.Menu;
import io.lumine.mythic.bukkit.utils.menu.MenuBuilder;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import java.awt.Color;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ColorPickerMenu {
   private ItemStack COLOR_SWATCH_ITEM = new ItemStack(Material.LEATHER_BOOTS);
   private Menu<ColorPickerMenu.ColorMenuContext> colorPickerMenu;
   final String dyeSwatchModel = "item/ui/dye_swatch";
   @Getter
   final NamespacedKey dyeSwatchKey;

   protected ColorPickerMenu() {
      if (MythicBukkit.inst().getCompatibility().getCrucible().isPresent()) {
         CrucibleSupport.ItemGenerationFeedback generationFeedback = MythicBukkit.inst()
            .getCompatibility()
            .getCrucible()
            .get()
            .addItemToGeneration(MythicBukkit.inst().getPackManager().getInternalsPack(), Material.LEATHER_CHESTPLATE, 0, "item/ui/dye_swatch");
         if (generationFeedback.getItemModel().isPresent()) {
            this.dyeSwatchKey = generationFeedback.getItemModel().get();
         } else {
            this.dyeSwatchKey = NamespacedKey.fromString("minecraft:leather_chestplate");
         }
      } else {
         this.dyeSwatchKey = NamespacedKey.fromString("minecraft:leather_chestplate");
      }

      MenuBuilder<ColorPickerMenu.ColorMenuContext> colorPickerMenu = Menu.<ColorPickerMenu.ColorMenuContext>builder()
         .addIcon(
            22,
            IconBuilder.<ColorPickerMenu.ColorMenuContext>create()
               .item(
                  (context, player) -> ItemFactory.of(Material.LEATHER_BOOTS)
                     .name("Current Color")
                     .hideAttributes()
                     .itemModel(this.dyeSwatchKey)
                     .color(context.getBukkitColor())
               )
               .build()
         )
         .addIcon(45, IconBuilder.<ColorPickerMenu.ColorMenuContext>create().item((color, player) -> ItemFactory.of(Material.ARROW).name("Reduce Saturation")).click((context, player) -> {
            context.addSaturation(-0.1F);
            this.openMenu(player, context);
         }).build())
         .addIcon(46, IconBuilder.<ColorPickerMenu.ColorMenuContext>create().item((color, player) -> ItemFactory.of(Material.ARROW).name("Increase Saturation")).click((context, player) -> {
            context.addSaturation(0.1F);
            this.openMenu(player, context);
         }).build())
         .addIcon(48, IconBuilder.<ColorPickerMenu.ColorMenuContext>create().item((color, player) -> ItemFactory.of(Material.ARROW).name("Zoom In")).click((context, player) -> {
            context.zoomIn();
            this.openMenu(player, context);
         }).build())
         .addIcon(49, IconBuilder.<ColorPickerMenu.ColorMenuContext>create().item((color, player) -> ItemFactory.of(Material.ARROW).name("Zoom Out")).click((context, player) -> {
            context.zoomOut();
            this.openMenu(player, context);
         }).build())
         .addIcon(51, IconBuilder.<ColorPickerMenu.ColorMenuContext>create().item((color, player) -> ItemFactory.of(Material.OAK_SIGN).name("Enter Color")).click((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type in the Color you'd like to use.", "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  try {
                     Chroma chroma = Chroma.of(input);
                     if (chroma == null) {
                        CommandHelper.sendError(player, "Invalid color format supplied");
                        return ChatPrompt.Response.TRY_AGAIN;
                     }

                     int red = chroma.red();
                     int green = chroma.green();
                     int blue = chroma.blue();
                     float[] color = Color.RGBtoHSB(red, green, blue, null);
                     context.setHue(color[0]);
                     context.setSaturation(color[1]);
                     context.setBrightness(color[2]);
                  } catch (Throwable e) {
                     CommandHelper.sendError(player, "Invalid color format supplied");
                     return ChatPrompt.Response.TRY_AGAIN;
                  }

                  return ChatPrompt.Response.ACCEPTED;
               }
            }).thenAcceptSync(in -> this.openMenu(player, context));
         }).build())
         .addIcon(
            53, IconBuilder.<ColorPickerMenu.ColorMenuContext>create().item((color, player) -> ItemFactory.of(Material.EMERALD_BLOCK)).click((context, color) -> context.complete()).build()
         );
      int[] slots = new int[]{
         18,
         19,
         20,
         21,
         23,
         24,
         25,
         26,
         9,
         10,
         11,
         12,
         13,
         14,
         15,
         16,
         17,
         0,
         1,
         2,
         3,
         4,
         5,
         6,
         7,
         8,
         27,
         28,
         29,
         30,
         31,
         32,
         33,
         34,
         35,
         36,
         37,
         38,
         39,
         40,
         41,
         42,
         43,
         44
      };

      for (int slot : slots) {
         colorPickerMenu.addIcon(slot, this.getColorSwatchIcon(slot));
      }

      this.colorPickerMenu = colorPickerMenu.title("Color Picker").build();
   }

   public Promise<Chroma> openMenu(Player player, Chroma color) {
      Promise<Chroma> promise = Promise.empty();
      ColorPickerMenu.ColorMenuContext state = new ColorPickerMenu.ColorMenuContext(color, promise);
      this.colorPickerMenu.open(player, state);
      return promise;
   }

   public void openMenu(Player player, ColorPickerMenu.ColorMenuContext state) {
      this.colorPickerMenu.open(player, state);
   }

   private Icon<ColorPickerMenu.ColorMenuContext> getColorSwatchIcon(int slot) {
      return IconBuilder.<ColorPickerMenu.ColorMenuContext>create()
         .item(
            (context, player) -> {
               float hue = (slot % 9 - 4) * context.getHueStep();
               float brightness = (2 - slot / 9) * context.getBrightnessStep();
               return ItemFactory.of(Material.LEATHER_BOOTS)
                  .name("")
                  .hideAttributes()
                  .itemModel(this.dyeSwatchKey)
                  .color(context.getOffsetBukkitColor(hue, brightness));
            }
         )
         .click((context, player) -> {
            float hue = (slot % 9 - 4) * context.getHueStep();
            float brightness = (2 - slot / 9) * context.getBrightnessStep();
            context.addHue(hue);
            context.addBrightness(brightness);
            this.openMenu(player, context);
         })
         .build();
   }

   @Getter
   @Setter
   @ToString
   public static class ColorMenuContext {
      private float hue = 0.4F;
      private float saturation = 1.0F;
      private float brightness = 0.6F;
      private int zoomLevel = 5;
      private final Promise<Chroma> then;

      public ColorMenuContext(Chroma chroma, Promise<Chroma> then) {
         if (chroma == null) {
            this.zoomLevel = 6;
         } else {
            int red = chroma.red();
            int green = chroma.green();
            int blue = chroma.blue();
            float[] color = Color.RGBtoHSB(red, green, blue, null);
            this.hue = color[0];
            this.saturation = color[1];
            this.brightness = color[2];
         }

         this.then = then;
      }

      public Color getColor() {
         return Color.getHSBColor(this.hue, this.saturation, this.brightness);
      }

      public org.bukkit.Color getBukkitColor() {
         Color offset = this.getColor();
         return org.bukkit.Color.fromRGB(offset.getRed(), offset.getGreen(), offset.getBlue());
      }

      public Color getOffsetColor(float hue, float brightness) {
         float newhue = this.hue + hue;
         if (newhue < 0.0F) {
            newhue++;
         } else if (newhue > 1.0F) {
            newhue--;
         }

         float newbrightness = Numbers.clamp(this.brightness + brightness, 0.0F, 1.0F);
         return Color.getHSBColor(newhue, this.saturation, newbrightness);
      }

      public org.bukkit.Color getOffsetBukkitColor(float hue, float brightness) {
         Color offset = this.getOffsetColor(hue, brightness);
         return org.bukkit.Color.fromRGB(offset.getRed(), offset.getGreen(), offset.getBlue());
      }

      public void addHue(float hue) {
         this.hue += hue;
         if (this.hue < 0.0F) {
            this.hue++;
         } else if (this.hue > 1.0F) {
            this.hue--;
         }
      }

      public void addSaturation(float saturation) {
         this.saturation += saturation;
         this.saturation = Numbers.clamp(this.saturation, 0.0F, 1.0F);
      }

      public void addBrightness(float brightness) {
         this.brightness += brightness;
         this.brightness = Numbers.clamp(this.brightness, 0.0F, 1.0F);
      }

      public void complete() {
         Color color = Color.getHSBColor(this.hue, this.saturation, this.brightness);
         Chroma chroma = Chroma.of(color.getRed(), color.getGreen(), color.getBlue());
         this.then.supply(chroma);
      }

      public float getHueStep() {
         return this.getBrightnessStep() / 2.0F;
      }

      public float getBrightnessStep() {
         return switch (this.zoomLevel) {
            case 0 -> 0.0025F;
            case 1 -> 0.005F;
            case 2 -> 0.01F;
            case 3 -> 0.025F;
            case 4 -> 0.05F;
            case 5 -> 0.1F;
            case 6 -> 0.2F;
            case 7 -> 0.25F;
            case 8 -> 0.333333F;
            case 9 -> 0.5F;
            default -> 0.1F;
         };
      }

      public void zoomIn() {
         if (this.zoomLevel > 0) {
            this.zoomLevel--;
         }
      }

      public void zoomOut() {
         if (this.zoomLevel < 9) {
            this.zoomLevel++;
         }
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof ColorPickerMenu.ColorMenuContext other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (Float.compare(this.getHue(), other.getHue()) != 0) {
            return false;
         } else if (Float.compare(this.getSaturation(), other.getSaturation()) != 0) {
            return false;
         } else if (Float.compare(this.getBrightness(), other.getBrightness()) != 0) {
            return false;
         } else if (this.getZoomLevel() != other.getZoomLevel()) {
            return false;
         } else {
            Object this$then = this.getThen();
            Object other$then = other.getThen();
            return this$then == null ? other$then == null : this$then.equals(other$then);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof ColorPickerMenu.ColorMenuContext;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + Float.floatToIntBits(this.getHue());
         result = result * 59 + Float.floatToIntBits(this.getSaturation());
         result = result * 59 + Float.floatToIntBits(this.getBrightness());
         result = result * 59 + this.getZoomLevel();
         Object $then = this.getThen();
         return result * 59 + ($then == null ? 43 : $then.hashCode());
      }
   }
}
