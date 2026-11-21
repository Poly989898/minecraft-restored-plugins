package com.nexomc.nexo.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.recipes.CustomRecipe;
import com.nexomc.nexo.recipes.builders.BrewingBuilder;
import com.nexomc.nexo.recipes.builders.FurnaceBuilder;
import com.nexomc.nexo.recipes.builders.RecipeBuilder;
import com.nexomc.nexo.recipes.builders.ShapedBuilder;
import com.nexomc.nexo.recipes.builders.ShapelessBuilder;
import com.nexomc.nexo.recipes.builders.StonecuttingBuilder;
import com.nexomc.nexo.recipes.listeners.RecipeEventManager;
import com.nexomc.nexo.utils.AdventureUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"recipeCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class RecipesCommandKt {
   public static final void recipeCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      Function3 saveLogic = ((a0_, a1_, a2_) -> RecipesCommandKt.recipeCommand$lambda$0((CommandSender)a0_, (String)a1_, (String)a2_));
      Function3 cookingLogic = ((a0_, a1_, a2_) -> RecipesCommandKt.recipeCommand$lambda$1((Integer)a0_, (Float)a1_, (CommandSender)a2_));
      RequiredArgumentBuilder cookingCmdStructure = (RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("cookingtime", (ArgumentType)IntegerArgumentType.integer(0)).executes((a0_ -> RecipesCommandKt.recipeCommand$lambda$2(cookingLogic, a0_)))).then(Commands.argument("experience", (ArgumentType)FloatArgumentType.floatArg(0.0F)).executes((a0_ -> RecipesCommandKt.recipeCommand$lambda$3(cookingLogic, a0_))));
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("show");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.recipes.show");
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("type", (ArgumentType)StringArgumentType.word());
      LiteralArgumentBuilder showCommand = (LiteralArgumentBuilder)literalArgumentBuilder2.then((ArgumentBuilder)nexoCommand2.executesPlayer$com_nexomc_core(requiredArgumentBuilder, RecipesCommandKt::recipeCommand$lambda$4));
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder3 = Commands.literal("save");
      LiteralArgumentBuilder saveCommand = (LiteralArgumentBuilder)nexoCommand3.withPermission$com_nexomc_core(literalArgumentBuilder3, "nexo.command.recipes.save").then(((RequiredArgumentBuilder)Commands.argument("id", (ArgumentType)StringArgumentType.word()).executes((a0_ -> RecipesCommandKt.recipeCommand$lambda$5(saveLogic, a0_)))).then(Commands.argument("permission", (ArgumentType)StringArgumentType.word()).executes((a0_ -> RecipesCommandKt.recipeCommand$lambda$6(saveLogic, a0_)))));
      nexoCommand3 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand4 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder4 = Commands.literal("builder");
      LiteralArgumentBuilder literalArgumentBuilder5 = nexoCommand4.withPermission$com_nexomc_core(literalArgumentBuilder4, "nexo.command.recipes.builder");
      NexoCommand nexoCommand5 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder6 = Commands.literal("brewing");
      literalArgumentBuilder5 = (LiteralArgumentBuilder)literalArgumentBuilder5.then((ArgumentBuilder)nexoCommand5.executesSender$com_nexomc_core(literalArgumentBuilder6, RecipesCommandKt::recipeCommand$lambda$7));
      nexoCommand5 = NexoCommand.INSTANCE;
      literalArgumentBuilder6 = Commands.literal("shaped");
      literalArgumentBuilder5 = (LiteralArgumentBuilder)literalArgumentBuilder5.then((ArgumentBuilder)nexoCommand5.executesSender$com_nexomc_core(literalArgumentBuilder6, RecipesCommandKt::recipeCommand$lambda$8));
      nexoCommand5 = NexoCommand.INSTANCE;
      literalArgumentBuilder6 = Commands.literal("shapeless");
      literalArgumentBuilder5 = (LiteralArgumentBuilder)literalArgumentBuilder5.then((ArgumentBuilder)nexoCommand5.executesSender$com_nexomc_core(literalArgumentBuilder6, RecipesCommandKt::recipeCommand$lambda$9));
      nexoCommand5 = NexoCommand.INSTANCE;
      literalArgumentBuilder6 = Commands.literal("stonecutting");
      ArgumentBuilder argumentBuilder = ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder5.then((ArgumentBuilder)nexoCommand5.executesSender$com_nexomc_core(literalArgumentBuilder6, RecipesCommandKt::recipeCommand$lambda$10))).then(Commands.literal("furnace").then((ArgumentBuilder)cookingCmdStructure))).then(Commands.literal("blasting").then((ArgumentBuilder)cookingCmdStructure))).then(Commands.literal("smoking").then((ArgumentBuilder)cookingCmdStructure));
      LiteralArgumentBuilder builderCommand = nexoCommand3.executesSender$com_nexomc_core((LiteralArgumentBuilder)argumentBuilder, RecipesCommandKt::recipeCommand$lambda$11);
      NexoCommand nexoCommand6 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder7 = Commands.literal("recipes");
      root.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)nexoCommand6.withPermission$com_nexomc_core(literalArgumentBuilder7, "nexo.command.recipes").then((ArgumentBuilder)showCommand)).then((ArgumentBuilder)saveCommand)).then((ArgumentBuilder)builderCommand));
   }

   private static final int recipeCommand$lambda$0(CommandSender sender, String recipeId, String permission) {
      try {
      int value = 0;
      if (!(sender instanceof Player)) {
         Message.NOT_PLAYER.send(sender);
      } else {
         RecipeBuilder.Companion companion = RecipeBuilder.Companion;
         UUID uniqueId = ((Player)sender).getUniqueId();
         RecipeBuilder recipeBuilder = companion.currentBuilder(uniqueId);
         if (recipeBuilder == null) {
            Message.RECIPE_NO_BUILDER.send(sender);
         } else {
            RecipeBuilder recipe = recipeBuilder;
            if (!recipe.getConfigFile().exists()) {
               recipe.getConfigFile().createNewFile();
            }

            String str = permission;
            if (permission == null) {
               str = "";
            }

            recipe.saveRecipe(recipeId, str);
            Message message = Message.RECIPE_SAVE;
            TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("name", recipeId)};
            message.send(sender, tagResolvers);
         }
      }

      return 1;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final int recipeCommand$lambda$1(int cookingTime, float experience, CommandSender sender) {
      if (sender instanceof Player) {
         RecipeBuilder.Companion companion = RecipeBuilder.Companion;
         UUID uniqueId = ((Player)sender).getUniqueId();
         FurnaceBuilder furnaceBuilder = companion.currentBuilder(uniqueId, FurnaceBuilder.class);
         if (furnaceBuilder == null) {
            furnaceBuilder = new FurnaceBuilder((Player)sender);
         }

         FurnaceBuilder recipe = furnaceBuilder;
         recipe.setCookingTime(cookingTime);
         recipe.setExperience(experience);
         recipe.open();
      } else {
         Message.NOT_PLAYER.send(sender);
      }

      return 1;
   }

   private static final int recipeCommand$lambda$2(Function3 cookingLogic, CommandContext it) {
      Object argument = it.getArgument("cookingtime", Integer.TYPE);
      Float number = 0.0F;
      CommandSender sender = ((CommandSourceStack)it.getSource()).getSender();
      return ((Number)cookingLogic.invoke(argument, number, sender)).intValue();
   }

   private static final int recipeCommand$lambda$3(Function3 cookingLogic, CommandContext it) {
      Object argument = it.getArgument("cookingtime", Integer.TYPE);
      Object obj = it.getArgument("experience", Float.TYPE);
      CommandSender sender = ((CommandSourceStack)it.getSource()).getSender();
      return ((Number)cookingLogic.invoke(argument, obj, sender)).intValue();
   }

   private static final Unit recipeCommand$lambda$4(Player player, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "type";
      int value2 = 0;
      Object argument = ctx.getArgument(str, String.class);
      String type = (String)argument;
      Iterable iterable = (Iterable)RecipeEventManager.Companion.instance().permittedRecipes((CommandSender)player);
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         CustomRecipe it = (CustomRecipe)element;
         int value = 0;
         if (Intrinsics.areEqual(it.getName(), type) || Intrinsics.areEqual(type, "all")) {
            destination.add(element);
         }
      }

      List recipes = (List)destination;
      if (recipes.isEmpty()) {
         return Unit.INSTANCE;
      } else {
         NexoPlugin.Companion.instance().invManager().recipesShowcase(0, recipes).open((HumanEntity)player);
         return Unit.INSTANCE;
      }
   }

   private static final int recipeCommand$lambda$5(Function3 saveLogic, CommandContext it) {
      CommandSender sender = ((CommandSourceStack)it.getSource()).getSender();
      Object argument = it.getArgument("id", String.class);
      return ((Number)saveLogic.invoke(sender, argument, (Object)null)).intValue();
   }

   private static final int recipeCommand$lambda$6(Function3 saveLogic, CommandContext it) {
      String id = (String)it.getArgument("id", String.class);
      String permission = (String)it.getArgument("permission", String.class);
      CommandSender sender = ((CommandSourceStack)it.getSource()).getSender();
      return ((Number)saveLogic.invoke(sender, id, permission)).intValue();
   }

   private static final Unit recipeCommand$lambda$7(CommandSender sender) {
      if (sender instanceof Player) {
         RecipeBuilder.Companion companion = RecipeBuilder.Companion;
         UUID uniqueId = ((Player)sender).getUniqueId();
         BrewingBuilder brewingBuilder = companion.currentBuilder(uniqueId, BrewingBuilder.class);
         if (brewingBuilder == null) {
            brewingBuilder = new BrewingBuilder((Player)sender);
         }

         brewingBuilder.open();
      } else {
         Message.NOT_PLAYER.send(sender);
      }

      return Unit.INSTANCE;
   }

   private static final Unit recipeCommand$lambda$8(CommandSender sender) {
      if (sender instanceof Player) {
         RecipeBuilder.Companion companion = RecipeBuilder.Companion;
         UUID uniqueId = ((Player)sender).getUniqueId();
         ShapedBuilder shapedBuilder = companion.currentBuilder(uniqueId, ShapedBuilder.class);
         if (shapedBuilder == null) {
            shapedBuilder = new ShapedBuilder((Player)sender);
         }

         shapedBuilder.open();
      } else {
         Message.NOT_PLAYER.send(sender);
      }

      return Unit.INSTANCE;
   }

   private static final Unit recipeCommand$lambda$9(CommandSender sender) {
      if (sender instanceof Player) {
         RecipeBuilder.Companion companion = RecipeBuilder.Companion;
         UUID uniqueId = ((Player)sender).getUniqueId();
         ShapelessBuilder shapelessBuilder = companion.currentBuilder(uniqueId, ShapelessBuilder.class);
         if (shapelessBuilder == null) {
            shapelessBuilder = new ShapelessBuilder((Player)sender);
         }

         shapelessBuilder.open();
      } else {
         Message.NOT_PLAYER.send(sender);
      }

      return Unit.INSTANCE;
   }

   private static final Unit recipeCommand$lambda$10(CommandSender sender) {
      if (sender instanceof Player) {
         RecipeBuilder.Companion companion = RecipeBuilder.Companion;
         UUID uniqueId = ((Player)sender).getUniqueId();
         StonecuttingBuilder stonecuttingBuilder = companion.currentBuilder(uniqueId, StonecuttingBuilder.class);
         if (stonecuttingBuilder == null) {
            stonecuttingBuilder = new StonecuttingBuilder((Player)sender);
         }

         stonecuttingBuilder.open();
      } else {
         Message.NOT_PLAYER.send(sender);
      }

      return Unit.INSTANCE;
   }

   private static final Unit recipeCommand$lambda$11(CommandSender sender) {
      if (sender instanceof Player) {
         RecipeBuilder.Companion companion = RecipeBuilder.Companion;
         UUID uniqueId = ((Player)sender).getUniqueId();
         StonecuttingBuilder stonecuttingBuilder = companion.currentBuilder(uniqueId, StonecuttingBuilder.class);
         if (stonecuttingBuilder == null) {
            stonecuttingBuilder = new StonecuttingBuilder((Player)sender);
         }

         stonecuttingBuilder.open();
      } else {
         Message.NOT_PLAYER.send(sender);
      }

      return Unit.INSTANCE;
   }
}
