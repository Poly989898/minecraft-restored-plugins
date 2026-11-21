package com.nexomc.nexo.commands;

import com.nexomc.nexo.decomp.ResultShim;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.ColorableKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.UtilsKt;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.text.HexExtensionsKt;
import kotlin.text.HexFormat;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000\u001a\f\u0010\t\u001a\u0004\u0018\u00010\n*\u00020\u000b\"\u0014\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0007\u0010\b¨\u0006\f"},
   d2 = {"dyeCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "hexFormat", "Lkotlin/text/HexFormat;", "getHexFormat$annotations", "()V", "toColor", "Lorg/bukkit/Color;", "", "com.nexomc:core"}
)
public final class DyeCommandKt {
   @NotNull
   private static final HexFormat hexFormat;

   public static final void dyeCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("dye");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.dye");
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("color", (ArgumentType)StringArgumentType.greedyString());
      root.then(literalArgumentBuilder2.then((ArgumentBuilder)nexoCommand2.executesPlayer$com_nexomc_core(requiredArgumentBuilder, DyeCommandKt::dyeCommand$lambda$0)));
   }

   @Nullable
   public static final Color toColor(@NotNull String str2) {
      String str = str2;

      Object obj2;
      try {
         int value = 0;
         Color color2;
         if (!StringsKt.startsWith(str, "#", false) && !StringsKt.startsWith(str, "0x", false)) {
            if (!StringsKt.contains((CharSequence)str, (CharSequence)",", false)) {
               if (StringsKt.toIntOrNull(str) != null) {
                  color2 = Color.fromRGB(Integer.parseInt(str));
               } else {
                  NamedTextColor namedTextColor = NamedTextColor.NAMES.value(str);
                  if (namedTextColor != null) {
                     int p0 = namedTextColor.value();
                     int value5 = 0;
                     color2 = Color.fromRGB(p0);
                  } else {
                     color2 = null;
                  }
               }
            } else {
               CharSequence charSequence = (CharSequence)StringUtilsKt.removeSpaces(str);
               String[] strs = new String[]{","};
               List color = StringsKt.split(charSequence, strs, false, 0);
               Iterable iterable = (Iterable)color;
               Collection destination = (Collection)(new ObjectArrayList());

               for(Object element : iterable) {
                  int value2 = 0;
                  String p0 = (String)element;
                  int value3 = 0;
                  Integer number = StringsKt.toIntOrNull(p0);
                  if (number != null) {
                     Object it = number;
                     int value4 = 0;
                     destination.add(it);
                  }
               }

               switch (((ObjectArrayList)destination).size()) {
                  case 3 -> color2 = Color.fromRGB(Integer.parseInt((String)color.get(0)), Integer.parseInt((String)color.get(1)), Integer.parseInt((String)color.get(2)));
                  case 4 -> color2 = Color.fromARGB(Integer.parseInt((String)color.get(0)), Integer.parseInt((String)color.get(1)), Integer.parseInt((String)color.get(2)), Integer.parseInt((String)color.get(3)));
                  default -> color2 = null;
               }
            }
         } else {
            String hex = StringsKt.removePrefix(StringsKt.removePrefix(str, (CharSequence)"#"), (CharSequence)"0x");
            color2 = Color.fromARGB(HexExtensionsKt.hexToInt(StringsKt.take(StringsKt.padStart(hex, 8, 'F'), 8), hexFormat));
         }

         obj2 = ResultShim.ok(color2);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = UtilsKt.printOnFailure(obj2, true);
      return (Color)(ResultShim.isFailure(obj) ? null : obj);
   }

   private static final Unit dyeCommand$lambda$0(Player player, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "color";
      int value3 = 0;
      Object argument = ctx.getArgument(str, String.class);
      Color color2 = toColor((String)argument);
      if (color2 == null) {
         Message.DYE_WRONG_COLOR.send((CommandSender)player);
         return Unit.INSTANCE;
      } else {
         Color color = color2;
         ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
         value3 = 0;
         ItemStack itemStack = !itemInMainHand.isEmpty() ? itemInMainHand : null;
         if (itemStack == null) {
            ItemStack it = player.getInventory().getItemInOffHand();
            int value = 0;
            itemStack = !it.isEmpty() ? it : null;
            if (itemStack == null) {
               Message.DYE_FAILED.send((CommandSender)player);
               return Unit.INSTANCE;
            }
         }

         ItemStack item = itemStack;
         Colorable colorable = ColorableKt.asColorable(item);
         if (colorable != null) {
            Colorable it = colorable;
            int value2 = 0;
            it.setColor(color);
            Message.DYE_SUCCESS.send((CommandSender)player);
            return Unit.INSTANCE;
         } else {
            Message.DYE_FAILED.send((CommandSender)player);
            return Unit.INSTANCE;
         }
      }
   }

   static {
      HexFormat.Builder builder = new HexFormat.Builder();
      int value = 0;
      builder.setUpperCase(true);
      HexFormat.NumberHexFormat.Builder number = builder.getNumber();
      int value2 = 0;
      number.setRemoveLeadingZeros(false);
      hexFormat = builder.build();
   }
}
