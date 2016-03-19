/*
 * Copyright (c) 2016 Justin W. Flory
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.justinwflory.hubutil.commands;

import com.justinwflory.hubutil.HubUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * CommandSetHub.java
 *
 * Class handles all variations of the <code>/sethub</code> command. Only function is to set new values to the
 * configuration file based on the player's in-game location.
 *
 * @author Whomp54
 * @author Justin W. Flory
 * @version 2016.03.19.v1
 */
public class CommandSetHub implements CommandExecutor {

    // Attributes
    private HubUtil plugin;

    /**
     * Default constructor. Takes an instance of the plugin and sets it locally.
     *
     * @param plugin the HubUtil class for configuration and other uses
     */
    public CommandSetHub(HubUtil plugin) {
        this.plugin = plugin;
    }

    /**
     * Method that handles the invocation of the <code>/sethub</code> command on the server. Sets the geographic
     * location of the hub spawn in the configuration file.
     *
     * @param sender the sender of the commander
     * @param cmd the base command used
     * @param commandLabel the label of the command
     * @param args the arguments used with the command
     * @return true if command executed successfully
     */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // Local variables
        Player p = null;

        // Check to see if the sender is anyone but a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "ERROR: Only in-game players can use this command.");
            return true;
        } else {
            p = (Player) sender;
        }

        // Takes player's position and writes the locations to the configuration file
        if (p.hasPermission("hub.set")) {
            plugin.getConfig().set("world", p.getWorld().getName());
            plugin.getConfig().set("x", p.getLocation().getBlockX() + 0.5D);
            plugin.getConfig().set("y", p.getLocation().getBlockY() + 0.5D);
            plugin.getConfig().set("z", p.getLocation().getBlockZ() + 0.5D);
            plugin.getConfig().set("pitch", p.getLocation().getPitch());
            plugin.getConfig().set("yaw", p.getLocation().getYaw());
            try {
                plugin.getConfig().save("plugins/HubUtil/config.yml");
                p.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN + "HubUtil" + ChatColor.GOLD + "]" +
                        ChatColor.GREEN + " Hub spawnpoint set!");
                return true;
            }
            catch (IOException e) { e.printStackTrace(); }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
            return true;
        }
        return false;
    }
}
