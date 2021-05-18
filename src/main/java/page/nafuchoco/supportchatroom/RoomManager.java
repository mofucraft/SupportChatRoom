/*
 * Copyright 2021 NAFU_at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package page.nafuchoco.supportchatroom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class RoomManager {
    private final Map<UUID, SupportRoom> roomStore;

    public RoomManager() {
        roomStore = new HashMap<>();
    }

    public void openRoom(Player owner, String name) {
        var room = new SupportRoom(UUID.randomUUID(), owner.getUniqueId(), new ArrayList<>(), name);
        roomStore.put(room.getRoomId(), room);
    }

    public void closeRoom(SupportRoom room) {
        room.getJoinedPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(p -> p.sendMessage("[" + ChatColor.GOLD + "ChatRoom" + ChatColor.RESET + "] " + "Room has been closed by the owner."));
        roomStore.remove(room.getRoomId());
    }

    public List<SupportRoom> getRooms() {
        return new ArrayList<>(roomStore.values());
    }

    public SupportRoom getRoom(UUID roomId) {
        return roomStore.get(roomId);
    }

    public SupportRoom getJoiningRoom(UUID playerId) {
        return getRooms().stream()
                .filter(room -> room.getOwnerId().equals(playerId)
                        || room.getJoinedPlayers().stream().anyMatch(id -> id.equals(playerId)))
                .findFirst().orElse(null);
    }
}
