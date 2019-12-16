package ink.ptms.zaphkiel;

import ink.ptms.zaphkiel.api.event.ItemBuildEvent;
import ink.ptms.zaphkiel.api.event.ItemRebuildEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @Author sky
 * @Since 2019-12-15 20:20
 */
public class WeightHook implements Listener {

    @EventHandler
    public void e(ItemBuildEvent.Pre e) {
        e.addLore("WEIGHT", e.getItemStream().getZaphkielData().get("weight").asDouble());
    }
}
