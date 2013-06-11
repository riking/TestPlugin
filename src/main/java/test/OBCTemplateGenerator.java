package test;

import java.lang.reflect.Field;
import java.util.Random;

import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftChunkSnapshot;


public class OBCTemplateGenerator extends TemplateGenerator {

    public OBCTemplateGenerator(World templateWorld) {
        super(templateWorld);
    }

    /**
     * @throws NoClassDefFoundError on wrong version of craftbukkit
     */
    @Override
    public short[][] generateExtBlockSections(World contextWorld, Random worldRandom, int cx, int cz, BiomeGrid biomes)
      throws RuntimeException {
        ChunkSnapshot tmp = world.getChunkAt(cx, cz).getChunkSnapshot();
        // let's do this first, shall we?
        loadBiomesIntoGrid(biomes, tmp);

        try {
            CraftChunkSnapshot templateChunk = (CraftChunkSnapshot)tmp;

            Field blockids = CraftChunkSnapshot.class.getDeclaredField("blockids");
            blockids.setAccessible(true);

            short[][] result = (short[][]) blockids.get(templateChunk);
            return result;
        } catch (Exception e) {
            System.err.println("[WARNING] Suppressing" + e.toString() + ", falling back on pure-bukkit");
            return super.generateExtBlockSections(contextWorld, worldRandom, cx, cz, biomes);
        }
    }
}
