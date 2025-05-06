/**

ElytraReplace IS a part of Meteor, but only as ElytraFly

*/

package com.ElytraReplace.addon.modules;

import meteordevelopment.meteorclient.systems.modules.Category;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.component.DataComponentTypes;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;

public class ElytraReplace extends Module {
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	public final Setting<Integer> replaceDurability = sgGeneral.add(new IntSetting.Builder()
        .name("replace-durability")
        .description("The durability threshold your elytra will be replaced at.")
        .defaultValue(2)
        .range(1, Items.ELYTRA.getComponents().get(DataComponentTypes.MAX_DAMAGE) - 1)
        .sliderRange(1, Items.ELYTRA.getComponents().get(DataComponentTypes.MAX_DAMAGE) - 1)
        .build()
    );
	
	private final Setting<Boolean> logOut = sgGeneral.add(new BoolSetting.Builder()
            .name("log-out")
            .description("Disconnect from the server upon having no elytras to be replaced")
            .defaultValue(false)
            .build()
    );
	
    public ElytraReplace(Category cat) {
        super(cat, "elytra-replace", "Automatically replaces your elytra when its broken and continues flying.");
    }
	
    @EventHandler
    private void onTick(TickEvent.Pre event) {
		if (mc.player.playerScreenHandler != mc.player.currentScreenHandler) return;
		int chestSlot = 38;
		
		ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
		if (chest.isOf(Items.ELYTRA) && chest.getMaxDamage() - chest.getDamage() <= replaceDurability.get()) {
			FindItemResult elytra = InvUtils.find(stack -> stack.getMaxDamage() - stack.getDamage() > replaceDurability.get() && stack.getItem() == Items.ELYTRA);
			if (elytra.found()){
				InvUtils.move().from(elytra.slot()).toArmor(2);
				return;
			}
			
			if (logOut.get()){
				disconnect("You ran out of elytras.");
			}
		}
	}
	
	
	private void disconnect(String reason) {
        disconnect(Text.literal(reason));
    }

    private void disconnect(Text reason) {
        MutableText text = Text.literal("[AutoLog] ");
        text.append(reason);

        mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(text));
    }
}