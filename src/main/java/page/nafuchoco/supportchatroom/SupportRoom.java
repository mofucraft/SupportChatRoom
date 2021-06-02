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

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Data
public class SupportRoom {
    private final UUID roomId;
    private final UUID ownerId;
    private final List<UUID> joinedPlayers;
    private String roomName;
    private TextChannel linkedChannel;


    public void sendRoomMessage(Player sender, String message) {
        List<Player> playerList = joinedPlayers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (Bukkit.getPlayer(ownerId) != null)
            playerList.add(Bukkit.getPlayer(ownerId));

        var chatMessage = ChatColor.BOLD + "[" + ChatColor.AQUA + roomName + ChatColor.WHITE + "] " + ChatColor.RESET +
                sender.getDisplayName() + " >> " + ChatColor.translateAlternateColorCodes('&', message);
        playerList.forEach(p -> p.sendMessage(chatMessage));
        SupportChatRoom.getInstance().getLogger().info(chatMessage);
    }

    public void sendRoomMessage(String senderName, String message) {
        List<Player> playerList = joinedPlayers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (Bukkit.getPlayer(ownerId) != null)
            playerList.add(Bukkit.getPlayer(ownerId));

        var chatMessage = ChatColor.BOLD + "[" + ChatColor.AQUA + roomName + ChatColor.WHITE + "] " + ChatColor.RESET +
                "[D] " + senderName + " >> " + ChatColor.translateAlternateColorCodes('&', message);
        playerList.forEach(p -> p.sendMessage(chatMessage));
        SupportChatRoom.getInstance().getLogger().info(chatMessage);
    }
}
