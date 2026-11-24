package com.comphenix.protocol.events;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import java.util.List;
import java.util.Set;
public class PacketContainer {
    public StructureModifier<Set<PlayerInfoAction>> getPlayerInfoActions() { return null; }
    public StructureModifier<List<PlayerInfoData>> getPlayerInfoDataLists() { return null; }
    public <T> StructureModifier<List<T>> getLists(com.comphenix.protocol.wrappers.Converters.Converter<T> converter) { return null; }
}
