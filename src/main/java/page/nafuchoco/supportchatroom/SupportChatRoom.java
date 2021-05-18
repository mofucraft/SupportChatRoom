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
import org.bukkit.plugin.java.JavaPlugin;

public final class SupportChatRoom extends JavaPlugin {
    private static SupportChatRoom instance;

    private RoomManager roomManager;

    public static SupportChatRoom getInstance() {
        if (instance == null)
            instance = (SupportChatRoom) Bukkit.getPluginManager().getPlugin("SupportChatRoom");
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatEventListener(), this);
        getCommand("chatroom").setExecutor(new ChatRoomCommand());
    }

    public RoomManager getRoomManager() {
        if (roomManager == null)
            roomManager = new RoomManager();
        return roomManager;
    }
}
