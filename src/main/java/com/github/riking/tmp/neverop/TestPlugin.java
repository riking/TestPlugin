package com.github.riking.tmp.neverop;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent ev) {
        if (ev.getPlayer().isOp()) {
            ev.getPlayer().setOp(false);
            loga(ChatColor.YELLOW + "Deopping " + ev.getPlayer().getName());
        }
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent ev) {
        Player p = ev.getPlayer();
        if (p.isOp()) {
            p.setOp(false);
            loga(ChatColor.RED + p.getName() + ChatColor.DARK_RED + " attempted to run a command while opped");
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("sudo")) return false;
        String cmdStr = StringUtils.join(args, " ");
        if (sender.hasPermission("x.sudoer")) {
            loga(ChatColor.BLUE + "[Sudo] " + ChatColor.YELLOW + sender.getName() + ChatColor.AQUA + " is now executing as an op: " + ChatColor.RESET + cmdStr);
            sender.setOp(true);
            getServer().dispatchCommand(sender, cmdStr);
            sender.setOp(false);
            return true;
        } else {
            loga(ChatColor.BLUE + "[Sudo] " + ChatColor.DARK_RED + sender.getName() + ChatColor.RED + " attempted to sudo without permission: " + cmdStr);
            return true;
        }
    }

    private void loga(String s) {
        this.getLogger().warning(ChatColor.stripColor(s));
        this.getServer().broadcast(s, Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
    }
}
