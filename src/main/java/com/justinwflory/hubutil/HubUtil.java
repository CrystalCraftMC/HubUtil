/*
 * Copyright (c) 2016 Justin W. Flory
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.justinwflory.hubutil;

import com.justinwflory.hubutil.listeners.Events;
import com.justinwflory.hubutil.commands.CommandHub;
import com.justinwflory.hubutil.commands.CommandSetHub;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * HubUtil.java
 *
 * Bukkit/Spigot plugin that enables you to set a spawnpoint for a hub and automatically teleport players there
 * whenever they join the server. This plugin intends to go a step above the normal <code>/setspawn</code> plugin by
 * forcing the players back to spawn on join.
 *
 * Original credit for this plugin goes to Whomp54. Maintained by Justin W. Flory (jflory7).
 *
 * @author Whomp54
 * @author Justin W. Flory
 * @version 2016.03.19.v1
 */
public class HubUtil extends JavaPlugin implements Listener {

    /**
     * Behavior that is executed when the server first starts up.
     */
    public void onEnable() {
        // Local variables
        PluginManager pm = getServer().getPluginManager();

        // Save default configuration if one does not already exist
        saveDefaultConfig();

        // Register listener and commands
        pm.registerEvents(new Events(this), this);
        getCommand("hub").setExecutor(new CommandHub(this));
        getCommand("sethub").setExecutor(new CommandSetHub(this));

        // Initiate logger
        //this.log = getLogger();
        this.getLogger().info(ChatColor.GREEN + "HubUtil enabled.");
    }

    /**
     * Behavior executed on server stop.
     */
    public void onDisable() {
        this.getLogger().info(ChatColor.GREEN + "HubUtil disabled.");
    }
}