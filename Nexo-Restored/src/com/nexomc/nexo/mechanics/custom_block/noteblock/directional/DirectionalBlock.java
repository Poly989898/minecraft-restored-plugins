package com.nexomc.nexo.mechanics.custom_block.noteblock.directional;

import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockRegistry;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockType;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanicFactory;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.blockstate.MultiVariant;
import team.unnamed.creative.blockstate.Variant;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0016\u0018\u0000 ,2\u00020\u0001:\u0002+,B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001f\u0010\u000e\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0007H\u0000¢\u0006\u0002\b\u0010J\u0006\u0010\u0011\u001a\u00020\u0012J\u001c\u0010\u001b\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\u0013\u0010 \u001a\u00070!¢\u0006\u0002\b\"2\u0006\u0010#\u001a\u00020\u0014J\u0012\u0010$\u001a\u00020\u00122\b\u0010%\u001a\u0004\u0018\u00010\u0007H\u0016J\u0018\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)2\u0006\u0010#\u001a\u00020*H\u0016R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u00148F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u001c\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0018X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a¨\u0006-"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "parentBlock", "", "getParentBlock", "()Ljava/lang/String;", "directionalType", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock$DirectionalType;", "getDirectionalType", "()Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock$DirectionalType;", "childIdOrCreate", "prefix", "childIdOrCreate$com_nexomc_core", "isParentBlock", "", "parentMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "getParentMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "blocks", "", "getBlocks", "()Ljava/util/List;", "directionMechanic", "face", "Lorg/bukkit/block/BlockFace;", "player", "Lorg/bukkit/entity/Player;", "directionalModel", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "mechanic", "anyMatch", "itemId", "parseVariantBuilder", "", "variantBuilder", "Lteam/unnamed/creative/blockstate/Variant$Builder;", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "DirectionalType", "Companion", "com.nexomc:core"}
)
public class DirectionalBlock {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private final String parentBlock;
   @NotNull
   private final DirectionalType directionalType;
   @NotNull
   private final List<String> blocks;

   public DirectionalBlock(@NotNull ConfigurationSection section) {
      super();
      DirectionalBlock directionalBlock = this;
      String string = section.getString("parent_block");
      if (string != null) {
         String str = string;
         String rootId = NexoYamlKt.getRootId(section);
         int value = 0;
         boolean flag = rootId.equals(str);
         directionalBlock = this;
         string = !flag ? str : null;
      } else {
         string = null;
      }

      this.parentBlock = string;
      String key = "type";
      DirectionalType directionalType2 = EnumUtils.getEnum(DirectionalType.class, NexoYamlKt.stringOrNull(section, key));
      if (directionalType2 == null) {
         directionalType2 = DirectionalBlock.DirectionalType.LOG;
      }

      this.directionalType = directionalType2;
      this.blocks = CollectionsKt.emptyList();
   }

   @Nullable
   public final String getParentBlock() {
      return this.parentBlock;
   }

   @NotNull
   public final DirectionalType getDirectionalType() {
      return this.directionalType;
   }

   @Nullable
   public final String childIdOrCreate$com_nexomc_core(@NotNull ConfigurationSection section, @NotNull String prefix) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      if (this.parentBlock != null) {
         return null;
      } else {
         String str = NexoYamlKt.stringOrNull(section, prefix + "_block");
         if (str == null) {
            String childId = StringUtilsKt.appendIfMissing(NexoYamlKt.getRootId(section), "_" + prefix);
            int value = 0;
            Key key = NexoYamlKt.key(section, prefix + "_model");
            if (key == null) {
               ConfigurationSection parent = section.getParent();
               key = parent != null ? NexoYamlKt.key(parent, "model") : null;
               if (key == null) {
                  key = NexoYamlKt.key(NexoYamlKt.getRootSection(section), "Pack.model");
                  if (key == null) {
                     key = Key.key(NexoYamlKt.getRootId(section));
                  }
               }
            }

            Key model = key;
            CustomBlockRegistry customBlockRegistry = CustomBlockRegistry.INSTANCE;
            ConfigurationSection configurationSection = section.getParent();
            CustomBlockType customBlockType = customBlockRegistry.get(configurationSection != null ? NexoYamlKt.stringOrNull(configurationSection, "type") : null);
            CustomBlockType type = customBlockType;
            Integer number = NexoYamlKt.intOrNull(section, prefix + "_variation");
            int variation = number != null ? number : CustomBlockRegistry.INSTANCE.generateVariation(model, type, false);
            ConfigurationSection configurationSection2 = (new YamlConfiguration()).createSection(childId).createSection("Mechanics").createSection("custom_block");
            ConfigurationSection fakeSection = configurationSection2;
            configurationSection2 = NexoYamlKt.plus(NexoYamlKt.plus(NexoYamlKt.plus(fakeSection, "type", type.name()), "custom_variation", variation), "model", model).createSection("directional");
            NexoYamlKt.plus(NexoYamlKt.plus(configurationSection2, "parent_block", NexoYamlKt.getRootId(section)), "type", this.directionalType.name());
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

            block: {
               for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
                  int value2 = 0;
                  MechanicFactory mechanicFactory2 = it;
                  if (!(it instanceof NoteBlockMechanicFactory)) {
                     mechanicFactory2 = null;
                  }

                  it = (NoteBlockMechanicFactory)mechanicFactory2;
                  if (it != null) {
                     mechanicFactory = it;
                     break block;
                  }
               }

               mechanicFactory = null;
            }

            NoteBlockMechanicFactory noteBlockMechanicFactory = (NoteBlockMechanicFactory)mechanicFactory;
            if (noteBlockMechanicFactory != null) {
               NoteBlockMechanicFactory factory = noteBlockMechanicFactory;
               NoteBlockMechanic noteBlockMechanic = factory.parse(fakeSection);
               if (noteBlockMechanic != null) {
                  NoteBlockMechanic mechanic = noteBlockMechanic;
                  section.set(prefix + "_variation", variation);
                  Variant.Builder variantBuilder = Variant.builder().model(model);
                  if (!section.contains(prefix + "_model")) {
                     switchBlock:
                     switch (DirectionalBlock.WhenMappings.$EnumSwitchMapping$0[this.directionalType.ordinal()]) {
                        case 1:
                           switch (prefix.hashCode()) {
                              case 120:
                                 if (prefix.equals("x")) {
                                    variantBuilder.x(90);
                                 }
                                 break switchBlock;
                              case 121:
                                 if (!prefix.equals("y")) {
                                 }
                                 break switchBlock;
                              case 122:
                                 if (prefix.equals("z")) {
                                    variantBuilder.x(90).y(90);
                                 }
                              default:
                                 break switchBlock;
                           }
                        case 2:
                           switch (prefix) {
                              case "up":
                                 variantBuilder.x(0).y(0);
                                 break switchBlock;
                              case "down":
                                 variantBuilder.x(180).y(0);
                                 break switchBlock;
                              case "east":
                                 variantBuilder.x(90).y(90);
                                 break switchBlock;
                              case "west":
                                 variantBuilder.x(90).y(270);
                                 break switchBlock;
                              case "north":
                                 variantBuilder.x(90).y(0);
                                 break switchBlock;
                              case "south":
                                 variantBuilder.x(90).y(180);
                              default:
                                 break switchBlock;
                           }
                        default:
                           switch (prefix.hashCode()) {
                              case 3739:
                                 if (prefix.equals("up")) {
                                    variantBuilder.x(270).y(0);
                                 }
                                 break;
                              case 3089570:
                                 if (prefix.equals("down")) {
                                    variantBuilder.x(90).y(0);
                                 }
                                 break;
                              case 3105789:
                                 if (prefix.equals("east")) {
                                    variantBuilder.x(0).y(90);
                                 }
                                 break;
                              case 3645871:
                                 if (prefix.equals("west")) {
                                    variantBuilder.x(0).y(270);
                                 }
                                 break;
                              case 105007365:
                                 if (!prefix.equals("north")) {
                                 }
                                 break;
                              case 109627853:
                                 if (prefix.equals("south")) {
                                    variantBuilder.x(0).y(180);
                                 }
                           }
                     }
                  }

                  Variant[] variants = new Variant[]{variantBuilder.build()};
                  factory.registerVariant(mechanic, MultiVariant.of(variants));
               }
            }

            str = childId;
         }

         return str;
      }
   }

   public final boolean isParentBlock() {
      return this.parentBlock == null;
   }

   @Nullable
   public final NoteBlockMechanic getParentMechanic() {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof NoteBlockMechanicFactory)) {
               mechanicFactory = null;
            }

            it = (NoteBlockMechanicFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      return (NoteBlockMechanicFactory)mechanicFactory != null ? ((NoteBlockMechanicFactory)mechanicFactory).getMechanic(this.parentBlock) : null;
   }

   @NotNull
   public List<String> getBlocks() {
      return this.blocks;
   }

   @Nullable
   public NoteBlockMechanic directionMechanic(@NotNull BlockFace face, @Nullable Player player) {
      return null;
   }

   @NotNull
   public final Key directionalModel(@NotNull NoteBlockMechanic mechanic) {
      String string = mechanic.getSection().getString("model");
      if (string == null) {
         block: {
            DirectionalBlock directional = mechanic.getDirectional();
            if (directional != null) {
               NoteBlockMechanic parentMechanic = directional.getParentMechanic();
               if (parentMechanic != null) {
                  Key model = parentMechanic.getModel();
                  if (model != null) {
                     string = model.asString();
                     break block;
                  }
               }
            }

            string = null;
         }

         if (string == null) {
            ConfigurationSection parent = mechanic.getSection().getParent();
            string = parent.getString("Pack.model", mechanic.getItemID());
            if (string == null) {
               string = mechanic.getItemID();
            }
         }
      }

      Key key = Key.key(string);
      return key;
   }

   public boolean anyMatch(@Nullable String itemId) {
      return false;
   }

   public void parseVariantBuilder(@NotNull Variant.Builder variantBuilder, @NotNull CustomBlockMechanic mechanic) {
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock$DirectionalType;", "", "<init>", "(Ljava/lang/String;I)V", "LOG", "FURNACE", "DROPPER", "BARREL", "com.nexomc:core"}
   )
   public static enum DirectionalType {
      LOG,
      FURNACE,
      DROPPER,
      BARREL;

      @NotNull
      public static EnumEntries<DirectionalType> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u000b\u001a\u00020\u0007¨\u0006\f"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock$Companion;", "", "<init>", "()V", "fromSection", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "extraVariations", "", "", "blockSection", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final DirectionalBlock fromSection(@NotNull ConfigurationSection section) {
         String key = "type";
         DirectionalType directionalType2 = EnumUtils.getEnum(DirectionalType.class, NexoYamlKt.stringOrNull(section, key));
         if (directionalType2 == null) {
            directionalType2 = DirectionalBlock.DirectionalType.LOG;
         }

         DirectionalType type = directionalType2;
         DirectionalBlock directionalBlock;
         switch (type) {
            case LOG -> directionalBlock = new DirectionalLog(section);
            case FURNACE -> directionalBlock = new DirectionalFurnace(section);
            case DROPPER -> directionalBlock = new DirectionalDropper(section);
            case BARREL -> directionalBlock = new DirectionalBarrel(section);
            default -> throw new NoWhenBranchMatchedException();
         }

         return directionalBlock;
      }

      @NotNull
      public final List<Integer> extraVariations(@NotNull ConfigurationSection blockSection) {
         List variations = (List)(new ArrayList());
         ConfigurationSection configurationSection = NexoYamlKt.section(blockSection, "directional");
         if (configurationSection == null) {
            return variations;
         } else {
            ConfigurationSection directional = configurationSection;
            ((Collection)variations).add(directional.getInt("y_variation"));
            ((Collection)variations).add(directional.getInt("x_variation"));
            ((Collection)variations).add(directional.getInt("z_variation"));
            ((Collection)variations).add(directional.getInt("north_variation"));
            ((Collection)variations).add(directional.getInt("south_variation"));
            ((Collection)variations).add(directional.getInt("east_variation"));
            ((Collection)variations).add(directional.getInt("west_variation"));
            ((Collection)variations).add(directional.getInt("up_variation"));
            ((Collection)variations).add(directional.getInt("down_variation"));
            variations.removeIf((a0_ -> Companion.extraVariations$lambda$0((Integer)a0_)));
            return variations;
         }
      }

      private static final boolean extraVariations$lambda$0(Integer it) {
         return it <= 0;
      }

      private static final boolean extraVariations$lambda$1(Function1 tmp0, Object p0) {
         return (Boolean)tmp0.invoke(p0);
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock.DirectionalType.values().length];
         try { a_0[com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock.DirectionalType.LOG.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock.DirectionalType.BARREL.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
