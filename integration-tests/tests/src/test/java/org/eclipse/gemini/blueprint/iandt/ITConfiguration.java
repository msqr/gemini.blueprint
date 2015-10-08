package org.eclipse.gemini.blueprint.iandt;

import org.ops4j.pax.exam.Option;

import static org.eclipse.gemini.blueprint.test.BlueprintOptions.defaultConfig;

/**
 * Created by dsklyut on 12/1/14.
 */
public class ITConfiguration implements org.ops4j.pax.exam.ConfigurationFactory {
    @Override
    public Option[] createConfiguration() {
        return defaultConfig();
    }
}
