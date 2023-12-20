package de.presti.schizomc.events;

import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SchizoEvents implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (ArrayUtils.schizoPlayers.containsKey(e.getPlayer())) {
            ArrayUtils.schizoBlocks.add(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        if (!e.isAsynchronous() || e.isCancelled() || !ArrayUtils.schizoPlayers.containsKey(e.getPlayer())) return;

        String message = e.getMessage();

        if (message.startsWith(SchizoUtil.schizoPrefix)) {
            e.setMessage(message.substring(SchizoUtil.schizoPrefix.length()));
        } else {
            if (!ArrayUtils.schizoMessages.contains(e.getMessage())) {
                ArrayUtils.schizoMessages.add(e.getMessage());
            }
        }
    }

}
