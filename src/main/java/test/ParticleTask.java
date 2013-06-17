package test;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class ParticleTask extends BukkitRunnable {
    public Effect effect = Effect.CRIT;
    public Location loc;
    public Vector offset = new Vector(1,1,1);
    public int id = 1;
    public int data = 1;
    public float speed = 1;
    public int count = 10;
    public World world;

    public void run() {
        System.out.println("Run " + this.toString());
        world.playEffect(loc, effect, id, data, (float)offset.getX(), (float)offset.getY(), (float)offset.getZ(), speed, count, 64);
    }

    public String toString() {
        return effect.toString() + " @ (" + loc.toVector().toString() + "), spread (" + offset.toString() + "), count " + count + ", speed " + speed;
    }
}
