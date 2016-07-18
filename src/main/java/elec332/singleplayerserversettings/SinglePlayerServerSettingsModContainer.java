package elec332.singleplayerserversettings;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Elec332 on 18-7-2016.
 */
public class SinglePlayerServerSettingsModContainer extends DummyModContainer {

    public SinglePlayerServerSettingsModContainer(){
        super(new ModMetadata());
        ModMetadata md = getMetadata();
        md.authorList = Lists.newArrayList("Elec332");
        md.description = "Allows for playing LAN worlds without an active internet connection.";
        md.modId = "singleplayerserversettings";
        md.name = "SinglePlayerServerSettings";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }

    @Override
    public Object getMod() {
        return this;
    }

    @Override
    public boolean matches(Object mod) {
        return mod == this;
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerAboutToStartEvent event){
        event.getServer().setOnlineMode(false);
        throw new RuntimeException();
    }

}
