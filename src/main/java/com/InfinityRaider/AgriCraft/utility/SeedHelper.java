package com.InfinityRaider.AgriCraft.utility;

import chococraft.common.items.seeds.ItemGysahlSeeds;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.chococraft.ChococraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.plantmegapack.PlantMegaPackHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Crops;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.SeedInformation;
import mods.natura.common.NContent;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Random;

public abstract class SeedHelper {
    private static ItemStack[] seedBlackList;
    private static ItemStack[] spreadChancesOverrides;
    private static Integer[] spreadChances;

    public static void initSeedBlackList() {
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readSeedBlackList());
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for(String line:data) {
            LogHelper.debug("parsing "+line);
            ItemStack seedStack = IOHelper.getStack(line);
            Item seed = seedStack!=null?seedStack.getItem():null;
            boolean success = seed!=null && seed instanceof ItemSeeds;
            String errorMsg = "Invalid seed";
            if(success) {
                list.add(seedStack);
            }
            else {
                LogHelper.info("Error when adding seed to blacklist: "+errorMsg+" (line: "+line+")");
            }
        }
        seedBlackList = list.toArray(new ItemStack[list.size()]);
        LogHelper.info("Registered seeds blacklist:");
        for(ItemStack seed:seedBlackList) {
            LogHelper.info(" - "+Item.itemRegistry.getNameForObject(seed.getItem())+":"+seed.getItemDamage());
        }
    }

    public static void initSpreadChancesOverrides() {
        //read mutation chance overrides & initialize the arrays
        setMutationChances(IOHelper.getLinesArrayFromData(ConfigurationHandler.readSpreadChances()));
        LogHelper.info("Registered Mutations Chances overrides:");
        for(int i=0;i<spreadChances.length;i++) {
            String mutation = spreadChancesOverrides[i].getItem()!=null?(Item.itemRegistry.getNameForObject(spreadChancesOverrides[i].getItem())+':'+spreadChancesOverrides[i].getItemDamage()):"null";
            String chance = spreadChances[i]+" percent";
            LogHelper.info(" - "+mutation + ": " + chance);
        }
    }

    //initializes the mutation chances arrays
    private static void setMutationChances(String[] input) {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        ArrayList<Integer> chances = new ArrayList<Integer>();
        LogHelper.debug("reading mutation chance overrides");
        for(String line:input) {
            String[] data = IOHelper.getData(line);
            boolean success = data.length==2;
            String errorMsg = "Incorrect amount of arguments";
            LogHelper.debug("parsing "+line);
            if(success) {
                ItemStack seedStack = IOHelper.getStack(data[0]);
                Item seed = seedStack!=null?seedStack.getItem():null;
                success = seed!=null && seed instanceof ItemSeeds;
                errorMsg = "Invalid seed";
                if(success) {
                    int chance = Integer.parseInt(data[1]);
                    success = chance>=0 && chance<=100;
                    errorMsg = "Chance should be between 0 and 100";
                    if(success) {
                        stacks.add(seedStack);
                        chances.add(chance);
                    }
                }
            }
            if(!success) {
                LogHelper.info("Error when adding mutation chance override: "+errorMsg+" (line: "+line+")");
            }
        }
        spreadChancesOverrides = stacks.toArray(new ItemStack[stacks.size()]);
        spreadChances = chances.toArray(new Integer[chances.size()]);
    }

    public static double getSpreadChance(ItemSeeds seed, int meta) {
        if(spreadChances.length>0) {
            for (int i = 0; i < spreadChances.length; i++) {
                if (seed == spreadChancesOverrides[i].getItem() && meta == spreadChancesOverrides[i].getItemDamage()) {
                    return ((double) spreadChances[i]) / 100;
                }
            }
        }
        return 1.00/ SeedHelper.getSeedTier(seed);
    }

    public static int getSeedTier(ItemSeeds seed) {
        if(seed == null) {
            return 0;
        }
        String domain = Item.itemRegistry.getNameForObject(seed).substring(0, Item.itemRegistry.getNameForObject(seed).indexOf(':'));
        if(domain.equalsIgnoreCase("agricraft")) {
            return ((ItemModSeed) seed).getPlant().tier;
        }
        if(domain.equalsIgnoreCase("harvestcraft")) {
            return 2;
        }
        if(domain.equalsIgnoreCase("natura")) {
            return 2;
        }
        if(domain.equalsIgnoreCase("magicalcrops")) {
            return 4;
        }
        if(domain.equalsIgnoreCase("plantmegapack")) {
            return 2;
        }
        if(domain.equalsIgnoreCase("weeeflowers")) {
            return 2;
        }
        return 1;
    }

    public static int getBaseGrowth(int tier) {
        switch(tier) {
            case 1: return Constants.growthTier1;
            case 2: return Constants.growthTier2;
            case 3: return Constants.growthTier3;
            case 4: return Constants.growthTier4;
            case 5: return Constants.growthTier5;
            default: return 0;
        }
    }

    //find the crop for a seed
    public static BlockBush getPlant(ItemSeeds seed) {
        if(seed == null) {
            return null;
        }
        else if(seed == Items.melon_seeds) {
            return Crops.melon;
        }
        else if(seed == Items.pumpkin_seeds) {
            return Crops.pumpkin;
        }
        else {
            if(seed.getPlant(null, 0, 0, 0) instanceof BlockCrops) {
                return (BlockCrops) seed.getPlant(null, 0, 0, 0);
            }
            else {
                return (BlockBush) seed.getPlant(null, 0, 0, 0);
            }
        }
    }

    //gets the seed domain
    public static String getPlantDomain(ItemSeeds seed) {
        String name = Item.itemRegistry.getNameForObject(seed);
        return name.substring(0, name.indexOf(":")).toLowerCase();
    }

    //gets the fruits
    public static ArrayList<ItemStack> getPlantFruits(ItemSeeds seed, World world, int x, int y, int z, int gain, int meta) {
        int nr =  (int) (Math.ceil((gain + 0.00) / 3));
        BlockBush plant = getPlant(seed);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        //nether wart exception
        if(plant==Blocks.nether_wart) {
            items.add(new ItemStack(seed, 1, 0));
        }
        //agricraft crop
        else if(plant instanceof BlockModPlant) {
            items.add(((BlockModPlant) plant).getFruit(nr));
        }
        //natura crop
        else if(ModIntegration.LoadedMods.natura && getPlantDomain(seed).equalsIgnoreCase("natura")) {
            items.add(new ItemStack(NContent.plantItem, nr, meta*3));
        }
        //harvestcraft crop
        else if(ModIntegration.LoadedMods.harvestcraft && getPlantDomain(seed).equalsIgnoreCase("harvestcraft")) {
            items.add(new ItemStack(getPlant(seed).getItemDropped(7, new Random(), 0), nr));
        }
        //chococraft crop
        else if(ModIntegration.LoadedMods.chococraft && seed instanceof ItemGysahlSeeds) {
            items.add(ChococraftHelper.getFruit(gain, nr));
        }
        //other crop
        else {
            int harvestMeta = 7;
            //plant mega pack crop
            if(ModIntegration.LoadedMods.plantMegaPack && getPlantDomain(seed).equalsIgnoreCase("plantmegapack")) {
                harvestMeta=PlantMegaPackHelper.getTextureIndex(seed, harvestMeta);
            }
            //other crop
            ArrayList<ItemStack> defaultDrops = plant.getDrops(world, x, y, z, harvestMeta, 0);
            for (ItemStack drop : defaultDrops) {
                if (!(drop.getItem() instanceof ItemSeeds) && drop.getItem()!=null) {
                    boolean add = true;
                    for(ItemStack item:items) {
                        if(item.getItem()==drop.getItem() && item.getItemDamage()==drop.getItemDamage()) {
                            add = false;
                        }
                    }
                    if(add) {
                        items.add(new ItemStack(drop.getItem(), nr, drop.getItemDamage()));
                    }
                }
            }
        }
        return items;
    }

    //check if the seed is valid
    public static boolean isValidSeed(ItemSeeds seed, int meta) {
        if(ModIntegration.LoadedMods.thaumicTinkerer && getPlantDomain(seed).equalsIgnoreCase(Names.Mods.thaumicTinkerer)) {
            LogHelper.debug("Thaumic Tinkerer infused seeds are not supported, sorry");
            return false;
        }
        for(ItemStack blacklistedSeed:seedBlackList) {
            if(blacklistedSeed.getItem()==seed && blacklistedSeed.getItemDamage()==meta) {
                return false;
            }
        }
        return true;
    }

    //get the base growth
    public static int getBaseGrowth(ItemSeeds seed) {
        return getBaseGrowth(getSeedTier(seed));
    }

    //define NBT tag
    public static void setNBT(NBTTagCompound tag, short growth, short gain, short strength, boolean analyzed) {
        tag.setShort(Names.NBT.growth, growth==0?Constants.defaultGrowth:growth>10?10:growth);
        tag.setShort(Names.NBT.gain, gain==0?Constants.defaultGain:gain>10?10:gain);
        tag.setShort(Names.NBT.strength, strength==0?Constants.defaultGain:strength>10?10:strength);
        tag.setBoolean(Names.NBT.analyzed, analyzed);
    }

    //get a string of information about the seed for the journal
    public static String getSeedInformation(ItemStack seedStack) {
        if (!(seedStack.getItem() instanceof ItemSeeds)) {
            return null;
        }
        return SeedInformation.getSeedInformation(seedStack);
    }

    //get a random seed
    public static ItemStack getRandomSeed(boolean setTag) {
        ArrayList<ItemStack> seeds = OreDictionary.getOres(Names.OreDict.listAllseed);
        ItemStack seed = null;
        while(seed==null || !(seed.getItem() instanceof ItemSeeds) || !isValidSeed((ItemSeeds) seed.getItem(), seed.getItemDamage())) {
            seed = seeds.get((int) Math.floor(Math.random()*seeds.size()));
        }
        if(setTag) {
            int gain = (int) Math.ceil(Math.random()*7);
            int growth = (int) Math.ceil(Math.random()*7);
            int strength = (int) Math.ceil(Math.random()*7);
            NBTTagCompound tag = new NBTTagCompound();
            setNBT(tag, (short) growth, (short) gain, (short) strength, false);
            seed.stackTagCompound = tag;
        }
        return seed;
    }
}
