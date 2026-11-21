package it.ytnoos.lpx.utils;

import com.github.retrooper.packetevents.wrapper.e1;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class BlockStateHandler {
   private static final Map<Class<?>, Method> METHOD_CACHE = new ConcurrentHashMap<>();
   private static final String GET_STATE = "getState";

   public BlockStateHandler() {
   }

   public static BlockState getBlockState(Block block) {
      try {
         Method method = METHOD_CACHE.computeIfAbsent(block.getClass(), class2 -> {
            try {
               String string = GET_STATE;
               Class[] classes = new Class[]{boolean.class};
               return class2.getMethod(e1.b(string, class2, classes), classes);
            } catch (NoSuchMethodException noSuchMethodException) {
               return null;
            }
         });
         if (method != null) {
            return (BlockState)method.invoke(block, false);
         }
      } catch (Exception exception) {
      }

      return block.getState();
   }
}
