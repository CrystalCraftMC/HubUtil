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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ConcurrentModificationException;
import java.util.Vector;

/**
 * CommandHub.java
 *
 * Class handles all variations of the <code>/hub</code> command, including teleportion to the hub and reloading the
 * configuration file.
 *
 * @author Whomp54
 * @author Justin W. Flory
 * @version 2016.03.19.v1
 */
public class CommandHub implements CommandExecutor {

    // Attributes
    public static Vector<Player> mover = new Vector<Player>();
    private HubUtil plugin;
    private Server server = Bukkit.getServer();

    /**
     * Default constructor. Takes an instance of the plugin and sets it locally.
     *
     * @param plugin the HubUtil class for configuration and other uses
     */
    public CommandHub(HubUtil plugin) {
        this.plugin = plugin;
    }

    /**
     * Method that handles the invocation of the <code>/hub</code> command on the server. Can teleport player to the
     * hub location, reload the configuration file, and handles timed teleportations.
     *
     * @param sender the sender of the commander
     * @param cmd the base command used
     * @param commandLabel the label of the command
     * @param args the arguments used with the command
     * @return true if command executed successfully
     */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // Local variables
        double x = plugin.getConfig().getDouble("x");
        double y = plugin.getConfig().getDouble("y");
        double z = plugin.getConfig().getDouble("z");
        float yaw = plugin.getConfig().getInt("yaw");
        float pitch = plugin.getConfig().getInt("pitch");
        long time = plugin.getConfig().getInt("tpdelaytime");
        Player p = null;
        World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world"));

        // Check to see if the sender is anyone but a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "ERROR: Only in-game players can use this command.");
            return true;
        } else {
            p = (Player) sender;
        }

        // Only valid argument as of now can be "reload" – reloads the pluginConf or exits gracefully
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("hub.reload")) {
                    plugin.reloadConfig();
                    p.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN + "HubUtil" + ChatColor.GOLD + "]" +
                            ChatColor.GREEN + " Configuration reloaded.");
                } else {
                    p.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
                }
            } else {
                p.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN + "HubUtil" + ChatColor.GOLD + "]" +
                        ChatColor.RED + " ERROR: Unknown command.");
                return false;
            }
            return true;
        }

        // Handles the teleportation of the player to the hub (delayed and non-delayed)
        else {
            if (p.hasPermission("hub.use")) {
                if (plugin.getConfig().get("tpdelay").equals(false) || !p.hasPermission("hub.tpdelay.ignore")) {
                    p.sendMessage(ChatColor.GOLD + "Teleporting to the hub.");
                    p.teleport(new Location(w, x, y, z, yaw, pitch));
                    return true;
                } else {
                    mover.add(p);
                    p.sendMessage(ChatColor.GOLD + "Teleporting in " + time + " seconds... don't move.");
                    server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        // Local variables
                        double x = plugin.getConfig().getDouble("x");
                        double y = plugin.getConfig().getDouble("y");
                        double z = plugin.getConfig().getDouble("z");
                        float yaw = plugin.getConfig().getInt("yaw");
                        float pitch = plugin.getConfig().getInt("pitch");
                        World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world"));

                        public void run() {
                            synchronized (mover) {
                                try {
                                    for (Player p : mover) {
                                        p.teleport(new Location(w, x, y, z, yaw, pitch));
                                        p.sendMessage(ChatColor.GOLD + "Teleporting to the hub.");
                                        mover.remove(p);
                                    }
                                } catch (ConcurrentModificationException e) {
                                    plugin.getLogger().finest("A ConcurrentModificationException was fired, but not " +
                                            "printed.");
                                }
                            }
                        }
                    }, time);
                }
            } else {
                p.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
                return true;
            }
        }
        return true;
    }
}
