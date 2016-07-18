package elec332.singleplayerserversettings;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * Created by Elec332 on 18-7-2016.
 */
@IFMLLoadingPlugin.TransformerExclusions("elec332.singleplayerserversettings.*")
public class SinglePlayerServerSettingsCore implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                SinglePlayerServerSettingsTransformer.class.getCanonicalName()
        };
    }

    @Override
    public String getModContainerClass() {
        return SinglePlayerServerSettingsModContainer.class.getCanonicalName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
