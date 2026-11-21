package io.lumine.mythic.bukkit.utils.config.properties;

import io.lumine.mythic.bukkit.utils.config.properties.types.BlockLocProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.BlockOrientListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.BlockOrientMapProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.BlockOrientProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.BlockPositionProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.BlockRegionProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.BooleanProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.ChromaProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.ChunkLocProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.ChunkPositionProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.CircularRegionProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.CircularZoneProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.ColorProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.ConfigurationSectionProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.DoubleProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.EnumProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.HotbarMenuProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.IconProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.Int2IntMapProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.IntProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.ItemProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.LangListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.LangProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.LocListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.LocMapProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.LocProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.LongProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.MapListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.MaterialProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.MenuProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.Node2StringListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.Node2StringProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.OrientListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.OrientMapProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.OrientProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.PointListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.PointProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.PositionProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.RegionProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.SoundProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.SqlCredentialsProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.ZoneProp;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.serialize.BlockLocus;
import io.lumine.mythic.bukkit.utils.serialize.BlockOrient;
import io.lumine.mythic.bukkit.utils.serialize.BlockPosition;
import io.lumine.mythic.bukkit.utils.serialize.BlockRegion;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.serialize.ChunkLocus;
import io.lumine.mythic.bukkit.utils.serialize.ChunkPosition;
import io.lumine.mythic.bukkit.utils.serialize.CircularRegion;
import io.lumine.mythic.bukkit.utils.serialize.CircularZone;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.bukkit.utils.serialize.Orient;
import io.lumine.mythic.bukkit.utils.serialize.Point;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import io.lumine.mythic.bukkit.utils.serialize.Sound;
import io.lumine.mythic.bukkit.utils.serialize.Zone;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public final class Property {
   public static void deleteNode(Object scope, String node) {
      boolean fileAbsolute = false;
      String file;
      if (scope == null) {
         throw new IllegalArgumentException("Scope cannot be null");
      } else {
         if (scope instanceof PropertyScope) {
            file = ((PropertyScope)scope).get();
            fileAbsolute = false;
         } else if (scope instanceof File) {
            file = ((File)scope).getAbsolutePath();
            fileAbsolute = true;
         } else if (scope instanceof String fileName) {
            file = fileName;
            fileAbsolute = false;
         } else {
            file = scope.toString();
            fileAbsolute = false;
         }

         ConfigurationSection config = getPlugin(scope).getPropertyConfig(file, fileAbsolute);
         config.set(node, null);

         try {
            ((FileConfiguration)config).save(getPlugin(scope).getPropertyFile(file));
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static ConfigurationSection getFileConfiguration(Object scope) {
      boolean fileAbsolute = false;
      String file;
      if (scope == null) {
         throw new IllegalArgumentException("Scope cannot be null");
      } else {
         if (scope instanceof PropertyScope) {
            file = ((PropertyScope)scope).get();
            fileAbsolute = false;
         } else if (scope instanceof File) {
            file = ((File)scope).getAbsolutePath();
            fileAbsolute = true;
         } else {
            if (scope instanceof ConfigurationSection configurationSection) {
               return configurationSection;
            }

            if (scope instanceof String fileName) {
               file = fileName;
               fileAbsolute = false;
            } else {
               file = scope.toString();
               fileAbsolute = false;
            }
         }

         return getPlugin(scope).getPropertyConfig(file, fileAbsolute);
      }
   }

   public static BlockLocProp BlockLoc(Object scope, String key) {
      return new BlockLocProp(getPlugin(scope), scope, key);
   }

   public static BlockLocProp BlockLoc(Object scope, String key, BlockLocus def) {
      return new BlockLocProp(getPlugin(scope), scope, key, def);
   }

   public static BlockLocProp BlockLoc(LuminePlugin plugin, Object scope, String key) {
      return new BlockLocProp(plugin, scope, key);
   }

   public static BlockLocProp BlockLoc(LuminePlugin plugin, Object scope, String key, BlockLocus def) {
      return new BlockLocProp(plugin, scope, key, def);
   }

   public static BlockPositionProp BlockPosition(Object scope, String key) {
      return new BlockPositionProp(getPlugin(scope), scope, key);
   }

   public static BlockPositionProp BlockPosition(Object scope, String key, BlockPosition def) {
      return new BlockPositionProp(getPlugin(scope), scope, key, def);
   }

   public static BlockPositionProp BlockPosition(LuminePlugin plugin, Object scope, String key) {
      return new BlockPositionProp(plugin, scope, key);
   }

   public static BlockPositionProp BlockPosition(LuminePlugin plugin, Object scope, String key, BlockPosition def) {
      return new BlockPositionProp(plugin, scope, key, def);
   }

   public static BlockRegionProp BlockRegion(Object scope, String key) {
      return new BlockRegionProp(getPlugin(scope), scope, key);
   }

   public static BlockRegionProp BlockRegion(Object scope, String key, BlockRegion def) {
      return new BlockRegionProp(getPlugin(scope), scope, key, def);
   }

   public static BlockRegionProp BlockRegion(LuminePlugin plugin, Object scope, String key) {
      return new BlockRegionProp(plugin, scope, key);
   }

   public static BlockRegionProp BlockRegion(LuminePlugin plugin, Object scope, String key, BlockRegion def) {
      return new BlockRegionProp(plugin, scope, key, def);
   }

   public static BooleanProp Boolean(Object scope, String key) {
      return new BooleanProp(getPlugin(scope), scope, key);
   }

   public static BooleanProp Boolean(Object scope, String key, boolean def) {
      return new BooleanProp(getPlugin(scope), scope, key, def);
   }

   public static BooleanProp Boolean(LuminePlugin plugin, Object scope, String key) {
      return new BooleanProp(plugin, scope, key);
   }

   public static BooleanProp Boolean(LuminePlugin plugin, Object scope, String key, boolean def) {
      return new BooleanProp(plugin, scope, key, def);
   }

   public static ChromaProp Chroma(Object scope, String key) {
      return new ChromaProp(getPlugin(scope), scope, key);
   }

   public static ChromaProp Chroma(Object scope, String key, Chroma def) {
      return new ChromaProp(getPlugin(scope), scope, key, def);
   }

   public static ChromaProp Chroma(LuminePlugin plugin, Object scope, String key) {
      return new ChromaProp(plugin, scope, key);
   }

   public static ChromaProp Chroma(LuminePlugin plugin, Object scope, String key, Chroma def) {
      return new ChromaProp(plugin, scope, key, def);
   }

   public static ChunkLocProp ChunkLoc(Object scope, String key) {
      return new ChunkLocProp(getPlugin(scope), scope, key);
   }

   public static ChunkLocProp ChunkLoc(Object scope, String key, ChunkLocus def) {
      return new ChunkLocProp(getPlugin(scope), scope, key, def);
   }

   public static ChunkLocProp ChunkLoc(LuminePlugin plugin, Object scope, String key) {
      return new ChunkLocProp(plugin, scope, key);
   }

   public static ChunkLocProp ChunkLoc(LuminePlugin plugin, Object scope, String key, ChunkLocus def) {
      return new ChunkLocProp(plugin, scope, key, def);
   }

   public static ChunkPositionProp ChunkPosition(Object scope, String key) {
      return new ChunkPositionProp(getPlugin(scope), scope, key);
   }

   public static ChunkPositionProp ChunkPosition(Object scope, String key, ChunkPosition def) {
      return new ChunkPositionProp(getPlugin(scope), scope, key, def);
   }

   public static ChunkPositionProp ChunkPosition(LuminePlugin plugin, Object scope, String key) {
      return new ChunkPositionProp(plugin, scope, key);
   }

   public static ChunkPositionProp ChunkPosition(LuminePlugin plugin, Object scope, String key, ChunkPosition def) {
      return new ChunkPositionProp(plugin, scope, key, def);
   }

   public static CircularRegionProp CircularRegion(Object scope, String key) {
      return new CircularRegionProp(getPlugin(scope), scope, key);
   }

   public static CircularRegionProp CircularRegion(Object scope, String key, CircularRegion def) {
      return new CircularRegionProp(getPlugin(scope), scope, key, def);
   }

   public static CircularRegionProp CircularRegion(LuminePlugin plugin, Object scope, String key) {
      return new CircularRegionProp(plugin, scope, key);
   }

   public static CircularRegionProp CircularRegion(LuminePlugin plugin, Object scope, String key, CircularRegion def) {
      return new CircularRegionProp(plugin, scope, key, def);
   }

   public static CircularZoneProp CircularZone(Object scope, String key) {
      return new CircularZoneProp(getPlugin(scope), scope, key);
   }

   public static CircularZoneProp CircularZone(Object scope, String key, CircularZone def) {
      return new CircularZoneProp(getPlugin(scope), scope, key, def);
   }

   public static CircularZoneProp CircularZone(LuminePlugin plugin, Object scope, String key) {
      return new CircularZoneProp(plugin, scope, key);
   }

   public static CircularZoneProp CircularZone(LuminePlugin plugin, Object scope, String key, CircularZone def) {
      return new CircularZoneProp(plugin, scope, key, def);
   }

   public static ColorProp Color(Object scope, String key) {
      return new ColorProp(getPlugin(scope), scope, key);
   }

   public static ColorProp Color(Object scope, String key, Chroma def) {
      return new ColorProp(getPlugin(scope), scope, key, def);
   }

   public static ColorProp Color(LuminePlugin plugin, Object scope, String key) {
      return new ColorProp(plugin, scope, key);
   }

   public static ColorProp Color(LuminePlugin plugin, Object scope, String key, Chroma def) {
      return new ColorProp(plugin, scope, key, def);
   }

   public static ConfigurationSectionProp ConfigurationSection(Object scope, String key) {
      return new ConfigurationSectionProp(getPlugin(scope), scope, key);
   }

   public static DoubleProp Double(Object scope, String key) {
      return new DoubleProp(getPlugin(scope), scope, key);
   }

   public static DoubleProp Double(Object scope, String key, double def) {
      return new DoubleProp(getPlugin(scope), scope, key, def);
   }

   public static DoubleProp Double(LuminePlugin plugin, Object scope, String key) {
      return new DoubleProp(plugin, scope, key);
   }

   public static DoubleProp Double(LuminePlugin plugin, Object scope, String key, double def) {
      return new DoubleProp(plugin, scope, key, def);
   }

   public static EnumProp Enum(Object scope, Class clazz, String key) {
      return new EnumProp(getPlugin(scope), scope, clazz, key);
   }

   public static EnumProp Enum(Object scope, Class clazz, String key, Enum def) {
      return new EnumProp(getPlugin(scope), scope, clazz, key, def);
   }

   public static EnumProp Enum(LuminePlugin plugin, Object scope, Class clazz, String key) {
      return new EnumProp(plugin, scope, clazz, key);
   }

   public static EnumProp Enum(LuminePlugin plugin, Object scope, Class clazz, String key, Enum def) {
      return new EnumProp(plugin, scope, clazz, key, def);
   }

   public static HotbarMenuProp HotbarMenu(Object scope, String key) {
      return new HotbarMenuProp(getPlugin(scope), scope, key, null);
   }

   public static HotbarMenuProp HotbarMenu(LuminePlugin plugin, Object scope, String key) {
      return new HotbarMenuProp(plugin, scope, key, null);
   }

   public static IconProp Icon(Object scope, String key) {
      return new IconProp(getPlugin(scope), scope, key);
   }

   public static IconProp Icon(Object scope, String key, IconBuilder def) {
      return new IconProp(getPlugin(scope), scope, key, def);
   }

   public static IconProp Icon(LuminePlugin plugin, Object scope, String key) {
      return new IconProp(plugin, scope, key);
   }

   public static IconProp Icon(LuminePlugin plugin, Object scope, String key, IconBuilder def) {
      return new IconProp(plugin, scope, key, def);
   }

   public static IntProp Int(Object scope, String key) {
      return new IntProp(getPlugin(scope), scope, key);
   }

   public static IntProp Int(Object scope, String key, int def) {
      return new IntProp(getPlugin(scope), scope, key, def);
   }

   public static IntProp Int(LuminePlugin plugin, Object scope, String key) {
      return new IntProp(plugin, scope, key);
   }

   public static IntProp Int(LuminePlugin plugin, Object scope, String key, int def) {
      return new IntProp(plugin, scope, key, def);
   }

   public static LongProp Long(Object scope, String key) {
      return new LongProp(getPlugin(scope), scope, key);
   }

   public static LongProp Long(Object scope, String key, long def) {
      return new LongProp(getPlugin(scope), scope, key, def);
   }

   public static LongProp Long(LuminePlugin plugin, Object scope, String key) {
      return new LongProp(plugin, scope, key);
   }

   public static LongProp Long(LuminePlugin plugin, Object scope, String key, long def) {
      return new LongProp(plugin, scope, key, def);
   }

   public static Int2IntMapProp Int2IntMap(Object scope, String key) {
      return new Int2IntMapProp(getPlugin(scope), scope, key);
   }

   public static Int2IntMapProp Int2IntMap(LuminePlugin plugin, Object scope, String key) {
      return new Int2IntMapProp(plugin, scope, key);
   }

   public static ItemProp Item(Object scope, String key) {
      return new ItemProp(getPlugin(scope), scope, key);
   }

   public static ItemProp Item(Object scope, String key, ItemStack def) {
      return new ItemProp(getPlugin(scope), scope, key, def);
   }

   public static ItemProp Item(LuminePlugin plugin, Object scope, String key) {
      return new ItemProp(plugin, scope, key);
   }

   public static ItemProp Item(LuminePlugin plugin, Object scope, String key, ItemStack def) {
      return new ItemProp(plugin, scope, key, def);
   }

   public static LangProp Lang(Object scope, String key) {
      return new LangProp(getPlugin(scope), scope, key);
   }

   public static LangProp Lang(Object scope, String key, String def) {
      return new LangProp(getPlugin(scope), scope, key, def);
   }

   public static LangProp Lang(LuminePlugin plugin, Object scope, String key) {
      return new LangProp(plugin, scope, key);
   }

   public static LangProp Lang(LuminePlugin plugin, Object scope, String key, String def) {
      return new LangProp(plugin, scope, key, def);
   }

   public static LangListProp LangList(Object scope, String key) {
      return new LangListProp(getPlugin(scope), scope, key);
   }

   public static LangListProp LangList(LuminePlugin plugin, Object scope, String key) {
      return new LangListProp(plugin, scope, key);
   }

   public static LocProp Loc(Object scope, String key) {
      return new LocProp(getPlugin(scope), scope, key);
   }

   public static LocProp Loc(Object scope, String key, Locus def) {
      return new LocProp(getPlugin(scope), scope, key, def);
   }

   public static LocProp Loc(LuminePlugin plugin, Object scope, String key) {
      return new LocProp(plugin, scope, key);
   }

   public static LocProp Loc(LuminePlugin plugin, Object scope, String key, Locus def) {
      return new LocProp(plugin, scope, key, def);
   }

   public static LocListProp LocList(Object scope, String key) {
      return new LocListProp(getPlugin(scope), scope, key);
   }

   public static LocListProp LocList(LuminePlugin plugin, Object scope, String key) {
      return new LocListProp(plugin, scope, key);
   }

   public static LocMapProp LocMap(Object scope, String key) {
      return new LocMapProp(getPlugin(scope), scope, key);
   }

   public static LocMapProp LocMap(LuminePlugin plugin, Object scope, String key) {
      return new LocMapProp(plugin, scope, key);
   }

   public static MapListProp MapList(Object scope, String key) {
      return new MapListProp(getPlugin(scope), scope, key);
   }

   public static MaterialProp Material(Object scope, String key) {
      return new MaterialProp(getPlugin(scope), scope, key);
   }

   public static MaterialProp Material(Object scope, String key, Material def) {
      return new MaterialProp(getPlugin(scope), scope, key, def);
   }

   public static MaterialProp Material(LuminePlugin plugin, Object scope, String key) {
      return new MaterialProp(plugin, scope, key);
   }

   public static MaterialProp Material(LuminePlugin plugin, Object scope, String key, Material def) {
      return new MaterialProp(plugin, scope, key, def);
   }

   public static MenuProp Menu(Object scope, String key) {
      return new MenuProp(getPlugin(scope), scope, key, null);
   }

   public static MenuProp Menu(LuminePlugin plugin, Object scope, String key) {
      return new MenuProp(plugin, scope, key, null);
   }

   public static NodeListProp NodeList(Object scope, String key) {
      return new NodeListProp(getPlugin(scope), scope, key);
   }

   public static NodeListProp NodeList(LuminePlugin plugin, Object scope, String key) {
      return new NodeListProp(plugin, scope, key);
   }

   public static Node2StringProp Node2String(Object scope, String key) {
      return new Node2StringProp(getPlugin(scope), scope, key);
   }

   public static Node2StringProp Node2String(LuminePlugin plugin, Object scope, String key) {
      return new Node2StringProp(plugin, scope, key);
   }

   public static Node2StringListProp Node2StringList(Object scope, String key) {
      return new Node2StringListProp(getPlugin(scope), scope, key);
   }

   public static Node2StringListProp Node2StringList(LuminePlugin plugin, Object scope, String key) {
      return new Node2StringListProp(plugin, scope, key);
   }

   public static OrientProp Orient(Object scope, String key) {
      return new OrientProp(getPlugin(scope), scope, key);
   }

   public static OrientProp Orient(Object scope, String key, Orient def) {
      return new OrientProp(getPlugin(scope), scope, key, def);
   }

   public static OrientProp Orient(LuminePlugin plugin, Object scope, String key) {
      return new OrientProp(plugin, scope, key);
   }

   public static OrientProp Orient(LuminePlugin plugin, Object scope, String key, Orient def) {
      return new OrientProp(plugin, scope, key, def);
   }

   public static OrientListProp OrientList(Object scope, String key) {
      return new OrientListProp(getPlugin(scope), scope, key);
   }

   public static OrientListProp OrientList(LuminePlugin plugin, Object scope, String key) {
      return new OrientListProp(plugin, scope, key);
   }

   public static OrientMapProp OrientMap(Object scope, String key) {
      return new OrientMapProp(getPlugin(scope), scope, key);
   }

   public static OrientMapProp OrientMap(LuminePlugin plugin, Object scope, String key) {
      return new OrientMapProp(plugin, scope, key);
   }

   public static BlockOrientProp BlockOrient(Object scope, String key) {
      return new BlockOrientProp(getPlugin(scope), scope, key);
   }

   public static BlockOrientProp BlockOrient(Object scope, String key, BlockOrient def) {
      return new BlockOrientProp(getPlugin(scope), scope, key, def);
   }

   public static BlockOrientProp BlockOrient(LuminePlugin plugin, Object scope, String key) {
      return new BlockOrientProp(plugin, scope, key);
   }

   public static BlockOrientProp BlockOrient(LuminePlugin plugin, Object scope, String key, BlockOrient def) {
      return new BlockOrientProp(plugin, scope, key, def);
   }

   public static BlockOrientListProp BlockOrientList(Object scope, String key) {
      return new BlockOrientListProp(getPlugin(scope), scope, key);
   }

   public static BlockOrientListProp BlockOrientList(LuminePlugin plugin, Object scope, String key) {
      return new BlockOrientListProp(plugin, scope, key);
   }

   public static BlockOrientMapProp BlockOrientMap(Object scope, String key) {
      return new BlockOrientMapProp(getPlugin(scope), scope, key);
   }

   public static BlockOrientMapProp BlockOrientMap(LuminePlugin plugin, Object scope, String key) {
      return new BlockOrientMapProp(plugin, scope, key);
   }

   public static PointProp Point(Object scope, String key) {
      return new PointProp(getPlugin(scope), scope, key);
   }

   public static PointProp Point(Object scope, String key, Point def) {
      return new PointProp(getPlugin(scope), scope, key, def);
   }

   public static PointProp Point(LuminePlugin plugin, Object scope, String key) {
      return new PointProp(plugin, scope, key);
   }

   public static PointProp Point(LuminePlugin plugin, Object scope, String key, Point def) {
      return new PointProp(plugin, scope, key, def);
   }

   public static PointListProp PointList(Object scope, String key) {
      return new PointListProp(getPlugin(scope), scope, key);
   }

   public static PointListProp PointList(LuminePlugin plugin, Object scope, String key) {
      return new PointListProp(plugin, scope, key);
   }

   public static PositionProp Position(Object scope, String key) {
      return new PositionProp(getPlugin(scope), scope, key);
   }

   public static PositionProp Position(Object scope, String key, Position def) {
      return new PositionProp(getPlugin(scope), scope, key, def);
   }

   public static PositionProp Position(LuminePlugin plugin, Object scope, String key) {
      return new PositionProp(plugin, scope, key);
   }

   public static PositionProp Position(LuminePlugin plugin, Object scope, String key, Position def) {
      return new PositionProp(plugin, scope, key, def);
   }

   public static RegionProp Region(Object scope, String key) {
      return new RegionProp(getPlugin(scope), scope, key);
   }

   public static RegionProp Region(Object scope, String key, Region def) {
      return new RegionProp(getPlugin(scope), scope, key, def);
   }

   public static RegionProp Region(LuminePlugin plugin, Object scope, String key) {
      return new RegionProp(plugin, scope, key);
   }

   public static RegionProp Region(LuminePlugin plugin, Object scope, String key, Region def) {
      return new RegionProp(plugin, scope, key, def);
   }

   public static SoundProp Sound(Object scope, String key) {
      return new SoundProp(getPlugin(scope), scope, key);
   }

   public static SoundProp Sound(Object scope, String key, Sound def) {
      return new SoundProp(getPlugin(scope), scope, key, def);
   }

   public static StringProp String(Object scope, String key) {
      return new StringProp(getPlugin(scope), scope, key);
   }

   public static StringProp String(Object scope, String key, String def) {
      return new StringProp(getPlugin(scope), scope, key, def);
   }

   public static StringProp String(LuminePlugin plugin, Object scope, String key) {
      return new StringProp(plugin, scope, key);
   }

   public static StringProp String(LuminePlugin plugin, Object scope, String key, String def) {
      return new StringProp(plugin, scope, key, def);
   }

   public static StringListProp StringList(Object scope, String key) {
      return new StringListProp(getPlugin(scope), scope, key);
   }

   public static StringListProp StringList(LuminePlugin plugin, Object scope, String key) {
      return new StringListProp(plugin, scope, key);
   }

   public static StringListProp StringList(Object scope, String key, List<String> defaultValue) {
      return new StringListProp(getPlugin(scope), scope, key, defaultValue);
   }

   public static StringListProp StringList(LuminePlugin plugin, Object scope, String key, List<String> defaultValue) {
      return new StringListProp(plugin, scope, key, defaultValue);
   }

   public static SqlCredentialsProp SqlCredentials(Object scope, String key) {
      return new SqlCredentialsProp(getPlugin(scope), scope, key);
   }

   public static SqlCredentialsProp SqlCredentials(LuminePlugin plugin, Object scope, String key) {
      return new SqlCredentialsProp(plugin, scope, key);
   }

   public static ZoneProp Zone(Object scope, String key) {
      return new ZoneProp(getPlugin(scope), scope, key);
   }

   public static ZoneProp Zone(Object scope, String key, Zone def) {
      return new ZoneProp(getPlugin(scope), scope, key, def);
   }

   public static ZoneProp Zone(LuminePlugin plugin, Object scope, String key) {
      return new ZoneProp(plugin, scope, key);
   }

   public static ZoneProp Zone(LuminePlugin plugin, Object scope, String key, Zone def) {
      return new ZoneProp(plugin, scope, key, def);
   }

   private static LuminePlugin getPlugin(Object scope) {
      if (scope instanceof PropertyScope) {
         return LoaderUtils.getPlugin(scope);
      } else {
         throw new IllegalArgumentException("Property's scope must implement PropertyScope if no plugin is provided");
      }
   }
}
