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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ChatRoomCommand implements CommandExecutor, TabCompleter {
    private final Map<Player, SupportRoom> invited;

    public ChatRoomCommand() {
        invited = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            var player = (Player) sender;
            var roomManager = SupportChatRoom.getInstance().getRoomManager();

            if (args.length == 0) {
                var supportRoom = roomManager.getJoiningRoom(player.getUniqueId());
                if (supportRoom != null)
                    sendPluginMessage(sender, "You are in the room " + supportRoom.getRoomName());
                else
                    sendPluginMessage(sender, "You have not joined the room.");
            } else if (!player.hasPermission("supportchat." + args[0])) {
                sendPluginMessage(sender, ChatColor.RED + "You do not have permission to execute this command.");
            } else if (args.length == 1) {
                switch (args[0]) {
                    case "accept": {
                        SupportRoom room = invited.get(player);
                        if (room != null) {
                            room.getJoinedPlayers().add(player.getUniqueId());
                            sendPluginMessage(sender, ChatColor.GREEN + "Joined the room.");
                        }
                        break;
                    }

                    case "list":
                        var listBuilder =
                                new StringBuilder(ChatColor.AQUA + "======== Room List ========\n" + ChatColor.RESET);
                        roomManager.getRooms().forEach(room -> {
                            String roomId = room.getRoomId().toString().split("-")[0];
                            listBuilder.append(roomId + ": " + room.getRoomName() + "\n");
                        });
                        sendPluginMessage(sender, listBuilder.toString());
                        break;

                    case "close": {
                        var supportRoom = roomManager.getJoiningRoom(player.getUniqueId());
                        if (supportRoom != null && supportRoom.getOwnerId() == player.getUniqueId()) {
                            sendPluginMessage(sender, ChatColor.GREEN + "Room closed.");
                            roomManager.closeRoom(supportRoom);
                        }
                        break;
                    }

                    case "leave": {
                        var supportRoom = roomManager.getJoiningRoom(player.getUniqueId());
                        if (supportRoom != null) {
                            supportRoom.getJoinedPlayers().remove(player.getUniqueId());
                            supportRoom.getJoinedPlayers().stream()
                                    .map(Bukkit::getPlayer)
                                    .filter(Objects::nonNull)
                                    .forEach(p -> sendPluginMessage(p, player.getDisplayName() + " left the room."));
                            if (Bukkit.getPlayer(supportRoom.getOwnerId()) != null)
                                sendPluginMessage(Bukkit.getPlayer(supportRoom.getOwnerId()), player.getDisplayName() + " left the room.");
                            sendPluginMessage(sender, ChatColor.GREEN + "Left the room.");
                        }
                        break;
                    }
                }
            } else switch (args[0]) {
                case "open": {
                    String name = ChatColor.translateAlternateColorCodes('&', args[1]);
                    roomManager.openRoom(player, name);
                    sendPluginMessage(sender, ChatColor.GREEN + "Chat room created.");
                    break;
                }

                case "invite": {
                    var supportRoom = roomManager.getJoiningRoom(player.getUniqueId());
                    if (supportRoom != null && supportRoom.getOwnerId() == player.getUniqueId()) {
                        var target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            invited.put(target, supportRoom);
                            sendPluginMessage(sender, "Invited players.");
                            sendPluginMessage(target, "You have been invited to a chat room.\n" +
                                    "Please run /chatroom accept to join.");
                        } else {
                            sendPluginMessage(sender, ChatColor.RED + "Specified player not found.");
                        }
                    } else {
                        sendPluginMessage(sender, ChatColor.RED + "To invite someone to a room, you must be the owner of the room.");
                    }
                    break;
                }

                case "join": {
                    var optionalSupportRoom = roomManager.getRooms().stream()
                            .filter(room -> room.getRoomId().toString().split("-")[0].equals(args[1]))
                            .findFirst();
                    if (optionalSupportRoom.isPresent()) {
                        optionalSupportRoom.get().getJoinedPlayers().add(player.getUniqueId());
                        sendPluginMessage(sender, ChatColor.GREEN + "Joined the room.");
                    } else {
                        sendPluginMessage(sender, ChatColor.RED + "Specified room not found.");
                    }
                    break;
                }
            }
        } else {
            sendPluginMessage(sender, "This command must be executed in-game.");
        }
        return true;
    }

    private void sendPluginMessage(CommandSender sender, String message) {
        sender.sendMessage("[" + ChatColor.GOLD + "ChatRoom" + ChatColor.RESET + "] " + message);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length <= 1)
            return Arrays.asList("open", "invite", "accept", "join", "leave", "close", "list");
        else if (args[0].equals("invite"))
            return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        else
            return null;
    }
}
