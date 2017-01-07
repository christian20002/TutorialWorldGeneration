/*
 * Copyright 2015 MovingBlocks
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

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.PerlinNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.*;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

@Updates(@Facet(SurfaceHeightFacet.class))
public class MountainsProvider implements ConfigurableFacetProvider {

    private Noise mountainNoise;

    //Be sure to initialize this!
    private MountainsConfiguration configuration = new MountainsConfiguration();

    @Override
    public void setSeed(long seed) {  //edited octaves
        mountainNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 2), 9), new Vector2f(10f, 10f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceHeightFacet facet = region.getRegionFacet(SurfaceHeightFacet.class);
        float mountainHeight = configuration.mountainHeight;
        // loop through every position on our 2d array
        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            // scale our max mountain height to noise (between -1 and 1)
            float additiveMountainHeight = mountainNoise.noise(position.x(), position.y()) * mountainHeight;
            // dont bother subtracting mountain height,  that will allow unaffected regions
            additiveMountainHeight = TeraMath.clamp(additiveMountainHeight, 4, mountainHeight);

            facet.setWorld(position, facet.getWorld(position) + additiveMountainHeight);
        }
    }

    @Override
    public String getConfigurationName()
    {
        return "Mountains";
    }

    @Override
    public Component getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration)
    {
        this.configuration = (MountainsConfiguration)configuration;
    }

    private static class MountainsConfiguration implements Component
    {
        @Range(min = 0, max = 70f, increment = 2f, precision = 10, description = "Mountain Height")
        private float mountainHeight = 0;
    }
}
