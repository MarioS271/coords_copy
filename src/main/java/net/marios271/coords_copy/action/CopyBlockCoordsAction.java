package net.marios271.coords_copy.action;

import net.marios271.coords_copy.CoordsCopy;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
//? < 1.19 {
/*import net.minecraft.network.chat.TranslatableComponent;
*///?} else {
import net.minecraft.network.chat.Component;
//?}
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;

public class CopyBlockCoordsAction {
    public static void player(){
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        String playerPos = String.format("%d %d %d", (int) client.player.getX(), (int) client.player.getY(), (int) client.player.getZ());

        copyToClipboard(playerPos);
        sendCopyMessage(CopyMessage.COPY_MSG_COPIED_PLAYER, playerPos);
    }

    public static void block(){
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.hitResult == null) return;
        HitResult hit = client.hitResult;

        switch(Objects.requireNonNull(hit).getType()){
            case MISS:
			case ENTITY:
                sendCopyMessage(CopyMessage.COPY_MSG_NO_BLOCK, "");
                break;
            case BLOCK:
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockHitPos = blockHit.getBlockPos();

                String blockPos = String.format("%d %d %d", blockHitPos.getX(), blockHitPos.getY(), blockHitPos.getZ());

                copyToClipboard(blockPos);
                sendCopyMessage(CopyMessage.COPY_MSG_COPIED_BLOCK, blockPos);
                break;
            default:
                sendCopyMessage(CopyMessage.COPY_MSG_ERROR, "");
                break;
        }
    }

    private static void copyToClipboard(String string){
        Minecraft client = Minecraft.getInstance();
        client.keyboardHandler.setClipboard(string);
    }

    private enum CopyMessage {
        COPY_MSG_NO_BLOCK,
        COPY_MSG_COPIED_PLAYER,
        COPY_MSG_COPIED_BLOCK,
        COPY_MSG_ERROR
    }

    private static void sendCopyMessage(CopyMessage copyMessage, String coords) {
        boolean chatOutput = CoordsCopy.CONFIG.chat_instead_of_actionbar;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

		//? < 1.17 {
		/*Component msg;
		switch (copyMessage) {
			case COPY_MSG_COPIED_PLAYER: msg = new TranslatableComponent("message.coords_copy.copied_player_coords", coords); break;
			case COPY_MSG_COPIED_BLOCK: msg = new TranslatableComponent("message.coords_copy.copied_block_coords", coords); break;
			case COPY_MSG_NO_BLOCK: msg = new TranslatableComponent("message.coords_copy.no_block"); break;
			default: msg = new TranslatableComponent("message.coords_copy.error"); break;
		}
		*///?} >= 1.17 && < 1.19 {
        /*Component msg = switch (copyMessage) {
            case COPY_MSG_COPIED_PLAYER -> new TranslatableComponent("message.coords_copy.copied_player_coords", coords);
            case COPY_MSG_COPIED_BLOCK -> new TranslatableComponent("message.coords_copy.copied_block_coords", coords);
            case COPY_MSG_NO_BLOCK -> new TranslatableComponent("message.coords_copy.no_block");
            default -> new TranslatableComponent("message.coords_copy.error");
        };
		*///?} else {
		Component msg = switch (copyMessage) {
			case COPY_MSG_COPIED_PLAYER -> Component.translatable("message.coords_copy.copied_player_coords", coords);
			case COPY_MSG_COPIED_BLOCK -> Component.translatable("message.coords_copy.copied_block_coords", coords);
			case COPY_MSG_NO_BLOCK -> Component.translatable("message.coords_copy.no_block");
			default -> Component.translatable("message.coords_copy.error");
		};
		//?}

		//? >= 26.1 {
        /*if (chatOutput) client.player.sendSystemMessage(msg);
        else client.player.sendOverlayMessage(msg);
		*///?} else {
		client.player.displayClientMessage(msg, chatOutput);
		//?}
    }
}
