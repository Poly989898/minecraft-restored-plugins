package io.lumine.mythic.bukkit.utils.network.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public interface BungeeCord {
   String ALL_SERVERS = "ALL";
   String ONLINE_SERVERS = "ONLINE";

   void connect(@Nonnull Player player, @Nonnull String str);

   void connectOther(@Nonnull String str, @Nonnull String str2);

   @Nonnull
   Promise<Entry<String, Integer>> ip(@Nonnull Player player);

   @Nonnull
   Promise<Integer> playerCount(@Nonnull String str);

   @Nonnull
   Promise<List<String>> playerList(@Nonnull String str);

   @Nonnull
   Promise<List<String>> getServers();

   void message(@Nonnull String str, @Nonnull String str2);

   @Nonnull
   Promise<String> getServer();

   @Nonnull
   Promise<UUID> uuid(@Nonnull Player player);

   @Nonnull
   Promise<UUID> uuidOther(@Nonnull String str);

   @Nonnull
   Promise<Entry<String, Integer>> serverIp(@Nonnull String str);

   void kickPlayer(@Nonnull String str, @Nonnull String str2);

   void forward(@Nonnull String str, @Nonnull String str2, @Nonnull byte[] bs);

   void forward(@Nonnull String str, @Nonnull String str2, @Nonnull ByteArrayDataOutput byteArrayDataOutput);

   void forwardToPlayer(@Nonnull String str, @Nonnull String str2, @Nonnull byte[] bs);

   void forwardToPlayer(@Nonnull String str, @Nonnull String str2, @Nonnull ByteArrayDataOutput byteArrayDataOutput);

   void registerForwardCallbackRaw(@Nonnull String str, @Nonnull Predicate<byte[]> predicate);

   void registerForwardCallback(@Nonnull String str, @Nonnull Predicate<ByteArrayDataInput> predicate);
}
