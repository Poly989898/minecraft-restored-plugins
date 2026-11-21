package com.nexomc.libs.morepersistentdatatypes;

import com.nexomc.libs.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableArrayDataType;
import com.nexomc.libs.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;

public interface DataType_1_18_1 {
   PersistentDataType<byte[], PlayerProfile> PLAYER_PROFILE = new ConfigurationSerializableDataType(PlayerProfile.class);
   PersistentDataType<byte[], PlayerProfile[]> PLAYER_PROFILE_ARRAY = new ConfigurationSerializableArrayDataType(PlayerProfile[].class);
}
