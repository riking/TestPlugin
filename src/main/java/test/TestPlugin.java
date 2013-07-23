package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BeaconPaidEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TestPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("about")) {
            Player p = (Player) sender;
            Block block = p.getTargetBlock(null, 8);
            BlockState state = block.getState();
            if (state instanceof Beacon) {
                Beacon beacon = (Beacon) state;
                sender.sendMessage("That's a beacon");
                sender.sendMessage(printState(beacon).split("\n"));
            }
        } else if (command.getName().equalsIgnoreCase("absorb")) {
            Player p = (Player) sender;
            Block block = p.getTargetBlock(null, 8);
            BlockState state = block.getState();
            if (state instanceof Beacon) {
                Beacon beacon = (Beacon) state;

                // Swap Regen & Absorption
                if (beacon.getEffects().contains(new PotionEffect(PotionEffectType.REGENERATION, 180, 0, true))) {
                    List<PotionEffect> effects = new ArrayList<PotionEffect>(beacon.getEffects());
                    ListIterator<PotionEffect> iter = effects.listIterator();
                    while (iter.hasNext()) {
                        PotionEffect eff = iter.next();
                        if (eff.getType().equals(PotionEffectType.REGENERATION)) {
                            iter.set(new PotionEffect(PotionEffectType.ABSORPTION, 180, 0, true));
                        }
                    }
                }
            }
        }
        return false;
    }

    public String printState(Beacon beacon) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append("Active: " + beacon.isActive() + " Pyramid size: " + beacon.getPyramidSize() + " Radius: " + beacon.getRadius() + " Sees sky: " + beacon.canSeeSky());
        sb.append('\n');
        Collection<PotionEffect> effects = beacon.getEffects();
        for (PotionEffect e : effects) {
            sb.append(e.getType().toString() + "x" + (e.getAmplifier() + 1));
            sb.append('\n');
        }
        if (!beacon.resetEffects().equals(effects)) {
            sb.append("Effects have been modified").append('\n');
            beacon.setEffects(effects);
        }
        return sb.toString();
    }

    @EventHandler
    public void onPaid(BeaconPaidEvent event) {
        System.out.println(event.getPlayer().getName() + " paid to change a beacon");
        Beacon beacon = event.getState();

        int extraLevels = beacon.getPyramidSize(true, 10) - 4;
        System.out.println("Beacon size grants " + extraLevels + " extra levels to effects");
        Collection<PotionEffect> effects = event.getNewEffects();
        List<PotionEffect> modEffects = new ArrayList<PotionEffect>();
        for (PotionEffect effect : effects) {
            if (effect.getType().equals(PotionEffectType.FAST_DIGGING)) {
                // Buff all Haste effects
                event.getPlayer().sendMessage("Increased Haste effect to level III");
                modEffects.add(new PotionEffect(effect.getType(), 0, 2));
            } else {
                if (extraLevels > 0) {
                    System.out.println("Buffed " + effect.getType());
                    modEffects.add(new PotionEffect(effect.getType(), 0, effect.getAmplifier() + extraLevels));
                } else {
                    System.out.println("Passing " + effect.getType());
                    modEffects.add(effect);
                }
            }
        }
        // Always set new effects
        event.setNewEffects(modEffects);
    }
}
