package com.InfinityRaider.AgriCraft.compatibility.waila;

import com.InfinityRaider.AgriCraft.blocks.BlockChannelValve;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

public class AgriCraftValveDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        Block block = dataAccessor.getBlock();
        TileEntity te = dataAccessor.getTileEntity();
        if(block instanceof BlockChannelValve && te instanceof TileEntityCustomWood) {
            ItemStack stack = new ItemStack(Blocks.blockChannelValve, 1, 0);
            stack.setTagCompound(((TileEntityCustomWood) te).getMaterialTag());
            return stack;
        }
        return null;
    }

    @Override
    public ITaggedList.ITipList getWailaHead(ItemStack itemStack, ITaggedList.ITipList currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public ITaggedList.ITipList getWailaBody(ItemStack itemStack, ITaggedList.ITipList currentTip, IWailaDataAccessor dataAccessor, IWailaConfigHandler config) {
        // TODO: check if we need to clear the currentTip list as it was done in 1.7
        // list = new ArrayList<String>();
        Block block = dataAccessor.getBlock();
        TileEntity te = dataAccessor.getTileEntity();
        if(block!=null && block instanceof BlockChannelValve && te!=null && te instanceof TileEntityValve) {
            TileEntityValve valve = (TileEntityValve) te;
            //define material
            ItemStack materialStack =valve.getMaterial();
            String material = materialStack.getItem().getItemStackDisplayName(materialStack);
            currentTip.add(StatCollector.translateToLocal("agricraft_tooltip.material")+": "+material);
            //show status
            String status = StatCollector.translateToLocal(valve.isPowered() ? "agricraft_tooltip.closed" : "agricraft_tooltip.open");
            currentTip.add(StatCollector.translateToLocal("agricraft_tooltip.state")+": "+status);
            //show contents
            int contents = valve.getFluidLevel();
            int capacity = 500;
            currentTip.add(StatCollector.translateToLocal("agricraft_tooltip.waterLevel")+": "+contents+"/"+capacity);
        }
        return currentTip;
    }

    @Override
    public ITaggedList.ITipList getWailaTail(ItemStack itemStack, ITaggedList.ITipList currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag, IWailaDataAccessorServer accessor) {
        return tag;
    }
}