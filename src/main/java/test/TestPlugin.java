package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Beacon.ActivationState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BeaconPulseEvent;
import org.bukkit.event.player.PlayerPayBeaconEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.ImmutableList;

public class TestPlugin extends JavaPlugin implements Listener {
    Set<String> bonusEffectPlayers = new HashSet<String>();
    Set<String> noBeaconPlayers = new HashSet<String>();

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
                            iter.add(new PotionEffect(PotionEffectType.HEALTH_BOOST, 180, 0, true));
                        }
                    }
                }
            }
        } else if (command.getName().equalsIgnoreCase("active")) {
            ActivationState acstate = ActivationState.valueOf(args[0].toUpperCase());
            Player p = (Player) sender;
            Block block = p.getTargetBlock(null, 8);
            BlockState state = block.getState();
            if (state instanceof Beacon) {
                Beacon beacon = (Beacon) state;
                beacon.setActivationState(acstate);
            }
        } else if (command.getName().equalsIgnoreCase("player")) {
            if (args[0].equalsIgnoreCase("deny")) {
                noBeaconPlayers.add(args[1]);
            } else if (args[0].equalsIgnoreCase("allow")) {
                noBeaconPlayers.remove(args[1]);
            } else if (args[0].equalsIgnoreCase("bonus")) {
                bonusEffectPlayers.add(args[1]);
            } else if (args[0].equalsIgnoreCase("nobonus")) {
                bonusEffectPlayers.remove(args[1]);
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("active")) {
            return ImmutableList.of("ON", "OFF", "DEFAULT");
        } else if (command.getName().equalsIgnoreCase("player")) {
            if (args.length == 1) {
                return ImmutableList.of("deny", "allow", "bonus", "nobonus");
            } else {

            }
        }
        return ImmutableList.of();
    }

    public String printState(Beacon beacon) {
        StringBuilder sb = new StringBuilder();
        sb.append("Active: " + beacon.isActive() + " (" + beacon.getActivationState().toString() + ") Pyramid size: " + beacon.getPyramidSize() + " Radius: " + beacon.getRadius());
        sb.append('\n');
        Collection<PotionEffect> effects = beacon.getEffects();
        for (PotionEffect e : effects) {
            sb.append(e.getType().toString() + "x" + (e.getAmplifier() + 1));
            sb.append(' ');
        }
        if (beacon.hasCustomEffects()) {
            sb.append("Effects have been modified").append('\n');
        }
        return sb.toString();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPulse(BeaconPulseEvent event) {
        List<HumanEntity> players = event.getPlayers();
        ListIterator<HumanEntity> iter = players.listIterator();
        boolean boost = event.getBeacon().getPyramidSize(true, 5) > 4;
        while (iter.hasNext()) {
            HumanEntity ent = iter.next();
            if (!(ent instanceof Player)) continue;
            Player p = (Player) ent;
            if (!p.hasPermission("beacon.use")) {
                iter.remove(); continue;
            }
            if (boost && p.hasPermission("beacon.bonus")) {
                iter.remove();
                for (PotionEffect effect : event.getEffects()) {
                    applyCombinedEffect(p, boostEffect(effect));
                }
            }
        }
        event.setPlayers(players);
    }

    private void applyCombinedEffect(Player p, PotionEffect eff2) {
        PotionEffect eff1 = null;
        PotionEffectType type = eff2.getType();
        for (PotionEffect e : p.getActivePotionEffects()) {
            if (e.getType() == type) {
                eff1 = e; break;
            }
        }
        if (eff1 != null) {
            if (eff1.getAmplifier() > eff2.getAmplifier()) {
                // nothing
                return;
            } else if (eff1.getAmplifier() < eff2.getAmplifier()) {
                // higher amp prevails
                p.addPotionEffect(eff2, true);
            } else {
                // equal
                p.addPotionEffect(new PotionEffect(type, Math.max(eff1.getDuration(), eff2.getDuration()), eff1.getAmplifier(), eff1.isAmbient() && eff2.isAmbient()), true);
            }
        } else {
            p.addPotionEffect(eff2);
        }
    }

    private PotionEffect boostEffect(PotionEffect e) {
        return new PotionEffect(e.getType(), e.getDuration(), e.getAmplifier() + 1, e.isAmbient());
    }

    @EventHandler
    public void onPaid(PlayerPayBeaconEvent event) {
        System.out.println(event.getPlayer().getName() + " paid to change a beacon");
        Beacon beacon = event.getBeacon();

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
