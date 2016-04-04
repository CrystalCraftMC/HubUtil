# HubUtils [![Build Status](https://travis-ci.org/CrystalCraftMC/HubUtil.svg?branch=master)](https://travis-ci.org/CrystalCraftMC/HubUtil)

The swiss army knife of hub management for people who have never run a hub before


## What

This is a simple [Bukkit/Spigot](http://www.spigotmc.org/wiki/about-spigot/) plugin designed for [CrystalCraftMC]
(https://crystalcraftmc.com).
 
It is designed to simplify hub management for server owners who just want to get up and running with a hub server 
quickly and easily.

This plugin does the following things:

* Sets a hub spawnpoint (can force players there on login)
* Can designate a hub world as a "silent" hub, disabling chat and hiding players from each other


## Using

The plugin is simple and easy to use. There are only two commands you need to know.

| Command           | Description                     | Permission |
|-------------------|---------------------------------|------------|
| ```/hub```        | Teleports to the hub spawnpoint | hub.use    |
| ```/sethub```     | Sets the hub spawnpoint         | hub.set    |
| ```/hub reload``` | Reloads the configuration file  | hub.reload |


## Building

This plugin uses Maven, so building the plugin is easy. Clone the repository and navigate to the directory, and run 
the following command.

```
$ mvn clean package
```

This will generate a JAR file that you can use.


## Legal

This plugin is licensed under the [Mozilla Public License 2.0](https://mozilla.org/MPL/2.0/).