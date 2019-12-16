package ink.ptms.zaphkiel;

import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.logger.TLogger;

import java.io.File;

/**
 * @Author sky
 * @Since 2019-12-15 20:09
 */
@Plugin.Version(5.13)
public class Zaphkiel extends Plugin {

    @TInject
    public static final TLogger LOGS = null;
    @TInject
    public static final TConfig CONF = null;

    @Override
    public void onStarting() {
        if (!new File(getDataFolder(), "item").exists()) {
            saveResource("item/def.yml", true);
        }
        if (!new File(getDataFolder(), "display").exists()) {
            saveResource("display/def.yml", true);
        }
    }

    @Override
    public void onActivated() {
        ZaphkielAPI.INSTANCE.reloadItem();
        ZaphkielAPI.INSTANCE.reloadDisplay();
    }
}
