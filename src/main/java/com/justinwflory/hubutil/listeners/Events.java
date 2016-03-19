/*
 * Copyright (c) 2016 Justin W. Flory
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.justinwflory.hubutil.listeners;

import com.justinwflory.hubutil.HubUtil;
import com.justinwflory.hubutil.commands.CommandHub;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Events.java
 *
 * Class listens for all events that can be triggered by the player that HubUtil needs to listen for and acts upon
 * them based on configuration.
 *
 * @author Whomp54
 * @author Justin W. Flory
 * @version 2016.03.19.v1
 */
public class Events implements Listener {

    // Attributes
    private ArrayList<Player> inHub = new ArrayList();
    private Vector<Player> mover = CommandHub.mover;
    private HubUtil plugin;

    /**
     * Default constructor. Takes an instance of the plugin and sets it locally.
     *
     * @param plugin the HubUtil class for configuration and other uses
     */
    public Events(HubUtil plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles the behavior when a player first joins the server.
     *
     * @param pje the PlayerJoinEvent fired when a player joins the server
     */
    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent pje) {
        // Local variables
        Player p = pje.getPlayer();

        // Check if player is in specified world, add to list if yes
        if (p.getWorld().equals(plugin.getConfig().getString("world"))) {
            inHub.add(p);
        } else {
            inHub.remove(p);
        }

        // Send new players to spawn if configured to do so
        if (plugin.getConfig().get("hub-on-connect").equals(true)) {
            // Determine player's location
            World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world"));
            double x = plugin.getConfig().getDouble("x");
            double y = plugin.getConfig().getDouble("y");
            double z = plugin.getConfig().getDouble("z");
            float yaw = plugin.getConfig().getInt("yaw");
            float pitch = plugin.getConfig().getInt("pitch");

            // Teleport player to new location
            p.teleport(new Location(w, x, y, z, yaw, pitch));
        }
    }

    /**
     * Method hides players from other players in the hub world and alerts new players to this when they join, if the
     * server operator has configured the hub to be silent.
     *
     * @param pje the PlayerJoinEvent fired when a player joins the server
     */
    @EventHandler
    public void makeInvisible(PlayerJoinEvent pje) {
        // Local variables
        int numPlayersOnline = (Bukkit.getServer().getOnlinePlayers()).size();
        Player newPlayer = pje.getPlayer();
        World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world"));

        // Alert new player they are in silent hub and hide them from others
        if (plugin.getConfig().get("silent-hub").equals(true)) {
            if ((newPlayer.getWorld().equals(w)) && (!newPlayer.hasPermission("hub.notsilent"))) {
                inHub.add(newPlayer);

                for (Player p : inHub) {
                    newPlayer.sendMessage(ChatColor.RED + "This hub is silent. You won't be able to see who's online " +
                            "or talk until you leave the hub.");
                    newPlayer.hidePlayer(p);
                    p.hidePlayer(newPlayer);
                }
            }
        }
    }

    /**
     * Method handles behavior when a player changes worlds, either into or out of the hub world. Will make them
     * visible or invisible depending on which world.
     *
     * @param pcwe the PlayerChangedWorldEvent when a player changes worlds
     */
    @EventHandler
    public void worldChange(PlayerChangedWorldEvent pcwe) {
        // Local variables
        int numPlayersOnline = (Bukkit.getServer().getOnlinePlayers()).size();
        Player newPlayer = pcwe.getPlayer();
        World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world"));

        // Makes player invisible in the hub world if they change worlds
        if (plugin.getConfig().get("silent-hub").equals(Boolean.valueOf(true)) && !newPlayer.hasPermission("hub" +
                ".notsilent")) {
            if (!newPlayer.getWorld().equals(w)) {
                inHub.remove(newPlayer);
                for (Player p : inHub) {
                    p.showPlayer(newPlayer);
                    newPlayer.showPlayer(p);
                }
            } else {
                for (Player p : inHub) {
                    newPlayer.sendMessage(ChatColor.RED + "You have now entered the hub. You cannot speak here.");
                    inHub.add(newPlayer);
                    p.hidePlayer(newPlayer);
                    newPlayer.hidePlayer(p);
                }
            }
        }
    }

    /**
     * Detects if a player is moving, and if so, removes them from a list of players that have been moved. Used in
     * CommandHub class to detect if a player is attempting to move while being queued to teleport.
     *
     * @param pme the PlayerMoveEvent when a player begins moving
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent pme) {
        // Local variables
        Player movingPlayer = pme.getPlayer();

        //TODO Check and see if this is necessary. Isn't that what we're listening for already...?
        // Removes moving player from list of moved players
        if (((pme.getFrom().getBlockX() != pme.getTo().getBlockX()) ||
                (pme.getFrom().getBlockY() != pme.getTo().getBlockY()) ||
                (pme.getFrom().getBlockZ() != pme.getTo().getBlockZ())) &&
                (mover.contains(movingPlayer))) {
            mover.remove(movingPlayer);
        }
    }

    /**
     * Method listens to player chat, and if the server is set as a silent hub, chat will be muted and the player
     * will be alerted about that.
     *
     * @param pce the PlayerChatEvent when a player types in chat
     */
    @EventHandler
    public void silenceHub(PlayerChatEvent pce) {
        // Local variables
        Player talkingPlayer = pce.getPlayer();
        World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("world"));

        // Cancels player chat event if player is in a silent hub and doesn't have permissions
        if ((plugin.getConfig().get("silent-hub").equals(true)) && (!talkingPlayer.hasPermission("hub.talk")) &&
                (talkingPlayer.getWorld().equals(w))) {
            talkingPlayer.sendMessage(ChatColor.DARK_RED + "Chat is disabled in the hub.");
            pce.setCancelled(true);
        }
    }
}
