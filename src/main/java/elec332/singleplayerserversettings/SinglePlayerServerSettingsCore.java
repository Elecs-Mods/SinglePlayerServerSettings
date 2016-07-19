package elec332.singleplayerserversettings;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

/**
 * Created by Elec332 on 18-7-2016.
 */
@IFMLLoadingPlugin.TransformerExclusions("elec332.singleplayerserversettings.")
public class SinglePlayerServerSettingsCore implements IFMLLoadingPlugin {

    private static File configFolder;

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
        configFolder = new File((File) data.get("mcLocation"), "config");
        SinglePlayerServerSettingsConfig.INSTANCE.loadConfig();
    }

    protected static File getConfigFolder(){
        if (configFolder == null){
            throw new IllegalStateException();
        }
        return configFolder;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
