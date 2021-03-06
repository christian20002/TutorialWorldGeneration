/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.tutorialWorldGeneration;

import org.terasology.core.world.generator.rasterizers.FloraType;
import org.terasology.math.ChunkMath;
import org.terasology.math.Region3i;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

import java.util.List;
import java.util.Map.Entry;

public class BushRasterizer implements WorldRasterizer {
    private Block stone;
    private Block water;

    @Override
    public void initialize() {
        stone = CoreRegistry.get(BlockManager.class).getBlock("Core:DeadBush");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        BushFacet bushFacet = chunkRegion.getFacet(BushFacet.class);
        //SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getRegionFacet(SurfaceHeightFacet.class);



       for (Entry<BaseVector3i, Bush> entry : bushFacet.getWorldEntries().entrySet()) {
            // there should be a bush here
            // create a couple 3d regions to help iterate through the cube shape, inside and out
            Vector3i centerBushPosition = new Vector3i(entry.getKey());
            int extent = entry.getValue().getExtent();
            centerBushPosition.add(0, 0, 0);
            Vector3i blockPosition = new Vector3i(centerBushPosition);
            blockPosition.add(0,3,0);
            //chunk.setBlock(ChunkMath.calcBlockPos(centerBushPosition), stone);
            Region3i walls = Region3i.createFromCenterExtents(centerBushPosition, extent);

            Region3i inside = Region3i.createFromCenterExtents(centerBushPosition, extent - 1);


            // loop through each of the positions in the cube, ignoring the is
            // commenting out 3d part for now...
            for (Vector3i newBlockPosition : walls) {
                if (chunkRegion.getRegion().encompasses(newBlockPosition)
                        && !inside.encompasses(newBlockPosition)) {
                    chunk.setBlock(ChunkMath.calcBlockPos(newBlockPosition), stone);
                   chunk.setBlock(ChunkMath.calcBlockPos(blockPosition), stone);

                }


            }


        }

    }

}
