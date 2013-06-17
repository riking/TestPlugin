package test;

import org.bukkit.Effect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TestPlugin extends JavaPlugin {
    ParticleTask task;

    public void onEnable() {
        task = new ParticleTask();
        getCommand("start").setExecutor(this);
        getCommand("effect").setExecutor(this);
        getCommand("speed").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (command.getName().equals("start")) {
            task.world = player.getWorld();
            task.loc = player.getEyeLocation();
            System.out.println(task.loc);
            try {
                task.runTaskTimer(this, 0, 20);
            } catch (Exception t) {}
            sender.sendMessage("Started " +task.toString());
            return true;
        }

        if (command.getName().equals("effect")) {
            Effect n = Effect.valueOf(args[0]);
            if (n.getType() == Effect.Type.PARTICLE) {
                task.effect = n;
            }
            return true;
        }

        if (command.getName().equals("speed")) {
            task.speed = Float.parseFloat(args[0]);
            return true;
        }

        if (command.getName().equals("count")) {
            task.count = Integer.parseInt(args[0]);
            return true;
        }

        if (command.getName().equals("spread")) {
            task.offset = new Vector(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            return true;
        }
        return false;
    }
}
