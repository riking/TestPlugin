package test;

import java.util.Random;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

/**
 * The TemplateGenerator loads chunks from an alternate world to a 'main'
 * world that can be modified and reset as needed.
 * <p>
 * How to use:
 * <p>
 * The first method we want to look at is
 * {@link org.bukkit.Server#createWorld(org.bukkit.WorldCreator)}. This takes
 * a WorldCreator, what's that? Well, the constructor is WorldCreator(String
 * name).
 * <p>
 * The general use-case for this is a unchanging template populating a
 * revertable main world. So, we don't want any of the random parts of
 * Minecraft worldgen.
 * <p>
 * Let's say that your template world is named "gameTemplate", and the game
 * world (the one that players join) will be named "gameWorld". So first:
 *
 * <pre>
 * World templateWorld = getServer().createWorld(new WorldCreator(&quot;gameTemplate&quot;));
 * </pre>
 *
 * Now that we have the template world loaded, we want to setup the playing
 * world. We need to set the generator, and some other options to make sure
 * that Minecraft doesn't mess us up and add stuff to our beautiful template:
 *
 * <pre>
 * WorldCreator gameCreator = new WorldCreator("gameWorld")
 *   .generateStructures(false) // no mineshafts
 *   .type(WorldType.FLAT) // no ores
 *   .generator(new TemplateGenerator(templateWorld));
 * </pre>
 * And we're almost done setting up! Now we just need to:
 *
 * <pre>
 * World gameWorld = getServer().createWorld(gameCreator);
 * </pre>
 *
 * We can now teleport players into the gameWorld and let them play away.
 * When the game is done and it's time to clean up, use
 * {@link World#regenerateChunk(int, int)} and the Game world will revert back
 * to the Template world. It's as simple as that!
 *
 * <p>
 *
 * If you want to use the OBCTemplateGenerator, which should be faster, do this:
 * <pre>
 * WorldCreator gameCreator = ...; // same as above
 * try {
 *   gameCreator.generator(new OBCTemplateGenerator(templateWorld));
 * } catch (Throwable t) {
 *   // ignore - we can't use it right now
 * }
 */
public class TemplateGenerator extends ChunkGenerator {

    protected World world;

    public TemplateGenerator(World templateWorld) {
        this.world = templateWorld;
    }

    /*
    void setBlock(short[][] result, int x, int y, int z, short blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new short[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }
    */

    @Override
    public short[][] generateExtBlockSections(World contextWorld, Random worldRandom, int cx, int cz, BiomeGrid biomes) {
        ChunkSnapshot templateChunk = world.getChunkAt(cx, cz).getChunkSnapshot();

        // Pure-bukkit impl - can be optimized via NMS

        // Load block IDs
        short[][] result = new short[256 / 16][];
        for (int y = 0; y < 256; y += 16) {
            short[] locres = new short[4096];
            for (int py = y; py < y + 16; py++) {
                for (int px = 0; px < 16; px++) {
                    for (int pz = 0; pz < 16; pz++) {
                        locres[((py & 0xF) << 8) | (pz << 4) | px] = (short) templateChunk.getBlockTypeId(px, py, pz);
                    }
                }
            }
            result[y >> 4] = locres;
        }

        loadBiomesIntoGrid(biomes, templateChunk);

        return result;
    }

    public void loadBiomesIntoGrid(BiomeGrid biomes, ChunkSnapshot template) {
        for (int px = 0; px < 16; px++) {
            for (int pz = 0; pz < 16; pz++) {
                biomes.setBiome(px, pz, template.getBiome(px, pz));
            }
        }
    }
}
