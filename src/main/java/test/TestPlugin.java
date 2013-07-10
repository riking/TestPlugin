package test;

import com.github.riking.templateworlds.api.ApiMain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {
    World lobbytemplate;
    World lobby;
    Location entryLoc;
    ApiMain templateApi;

    @Override
    public void onEnable() {
        Bukkit.createWorld(WorldCreator.name("lobby-template"));
        templateApi = getServer().getServicesManager().getRegistration(ApiMain.class).getProvider();
        lobbytemplate = WorldCreator.name("lobby-template").type(WorldType.FLAT).createWorld();
        lobby = templateApi.createWorld("lobby", lobbytemplate);
        entryLoc = new Location(lobby, 0, 64, 0);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("enter")) {
            player.teleport(entryLoc);
            return true;
        } else if (command.getName().equalsIgnoreCase("edit")) {
            if (args.length == 0) return false;
            if ("start".equalsIgnoreCase(args[0])) {
                Location loc = entryLoc.clone();
                loc.setWorld(lobbytemplate);
                player.teleport(loc);
                player.setGameMode(GameMode.CREATIVE);
                return true;
            } else if ("stop".equalsIgnoreCase(args[0])) {
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(ChatColor.AQUA+"Please wait while lobby is reset...");
                player.teleport(getSpawn());
                final Player p = player;
                templateApi.resetAreaGradually(lobby, -9, -9, 9, 9, new Runnable() {
                    @Override
                    public void run() {
                        if (p.isValid() && p.isOnline()) {
                            p.teleport(entryLoc);
                            p.sendMessage(ChatColor.AQUA + "Lobby reset complete.");
                        }
                    }
                });
                return true;
            }
            return false;
        } else if (command.getName().equalsIgnoreCase("exit")) {
            Location loc = player.getBedSpawnLocation();
            if (loc == null) {
                loc = getSpawn();
            }
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(loc);
            return true;
        } else if (command.getName().equalsIgnoreCase("reset")) {
            templateApi.resetArea(lobby, -9, -9, 9, 9);
            return true;
        }
        return false;
    }

    public Location getSpawn() {
        return getServer().getWorlds().get(0).getSpawnLocation();
    }
}
