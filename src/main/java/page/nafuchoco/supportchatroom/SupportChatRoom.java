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

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Category;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

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
        saveDefaultConfig();
        if (getConfig().getBoolean("discordIntegration")) {
            // DiscordSRV
            var discordSRV = (DiscordSRV) getServer().getPluginManager().getPlugin("DiscordSRV");
            if (discordSRV != null) {
                initDiscord(discordSRV);
            }
        }

        getServer().getPluginManager().registerEvents(new AsyncPlayerChatEventListener(), this);
        getCommand("chatroom").setExecutor(new ChatRoomCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("Delete a generated channel.");
        getRoomManager().getRooms().stream()
                .map(r -> r.getLinkedChannel())
                .filter(c -> c != null)
                .forEach(t -> t.delete().submit());
    }

    public void initDiscord(DiscordSRV discordSRV) {
        getServer().getScheduler().runTaskLater(this, () -> {
            if (!DiscordSRV.isReady) {
                initDiscord(discordSRV);
                return;
            }

            getLogger().info("Enable the integration function with DiscordSRV.");
            var mainGuild = discordSRV.getMainGuild();

            if (mainGuild != null) {
                var categories = mainGuild.getCategoriesByName("[SC] Support Rooms", true);
                Category category = null;
                if (categories.isEmpty()) {
                    try {
                        category = mainGuild.createCategory("[SC] Support Rooms").submit().get();
                    } catch (InterruptedException | ExecutionException e) {
                        getLogger().log(Level.WARNING, "An error has occurred during the integration process with DiscordSRV.", e);
                    }
                    getLogger().info("Created a category for Discord guilds.");
                } else {
                    category = categories.get(0);
                }

                getRoomManager().setDiscordApi(category);
                discordSRV.getJda().addEventListener(new MessageReceivedEventHandler(getRoomManager()));

                // Delete dump channels.
                category.getChannels().forEach(c -> c.delete().submit());
            }
        }, 20L * 10);
    }

    public RoomManager getRoomManager() {
        if (roomManager == null)
            roomManager = new RoomManager();
        return roomManager;
    }
}
