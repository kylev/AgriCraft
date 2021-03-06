package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigurationHandler {

    public static final String CATEGORY_AGRICRAFT = "agricraft";
    public static final String CATEGORY_FARMING = "farming";
    public static final String CATEGORY_DEBUG = "debug";
    public static final String CATEGORY_INTEGRATION = "integration";
    public static final String CATEGORY_IRRIGATION = "irrigation";
    public static final String CATEGORY_STORAGE = "storage";

    public static Configuration config;
    private static String directory;
    private static Property propGenerateDefaults = new Property("RegenDefaults","false", Property.Type.BOOLEAN);

    public static boolean debug;

    public static boolean enableNEI;
    public static boolean generateDefaults;
    public static boolean customCrops;
    public static boolean resourcePlants;
    public static double mutationChance;
    public static boolean singleSpreadsIncrement;
    public static int spreadingDifficulty;
    public static int cropsPerCraft;
    public static int cropStatCap;
    public static int cropStatDivisor;
    public static boolean enableWeeds;
    public static boolean weedsWipePlants;
    public static boolean enableHandRake;
    public static boolean bonemealMutation;
    public static boolean disableWorldGen;
    public static boolean disableVanillaFarming;
    public static boolean wipeTallGrassDrops;
    public static boolean renderBookInAnalyzer;
    public static boolean cactusGivesCactus;

    public static boolean disableSeedStorage;
    public static boolean disableSeedWarehouse;

    public static boolean disableIrrigation;
    public static int sprinklerRatePerSecond;
    public static int sprinklerRatePerHalfSecond;
    public static int sprinklerGrowthChance;
    public static float sprinklerGrowthChancePercent;
    public static int sprinklerGrowthInterval;
    public static int sprinklerGrowthIntervalTicks = 100;
    public static boolean placeWater;
    public static boolean fillFromFlowingWater;
    public static int villagerID;
    public static boolean villagerEnabled;

    public static boolean integration_HC;
    public static boolean integration_Nat;
    public static boolean integration_WeeeFlowers;
    public static boolean integration_PlantMegaPack;
    public static boolean integration_Chococraft;
    public static boolean integration_Botania;
    public static boolean integration_allowMagicFertiliser;
    public static boolean integration_instantMagicFertiliser;
    public static boolean integration_Psychedelicraft;

    public static void init(FMLPreInitializationEvent event) {
        directory = event.getModConfigurationDirectory().toString()+'/'+Reference.MOD_ID.toLowerCase()+'/';

        if(config == null) {
            config = new Configuration(new File(directory, "Configuration.cfg"));
            loadConfiguration();
        }

        LogHelper.debug("Configuration Loaded");
    }


    //read values from the config
    private static void loadConfiguration() {
        //agricraft settings
        resourcePlants = config.getBoolean("Resource Crops",CATEGORY_AGRICRAFT,false,"set to true if you wish to enable resource crops");
        mutationChance = (double) config.getFloat("Mutation Chance",CATEGORY_AGRICRAFT, (float) Constants.defaultMutationChance, 0, 1 , "Define mutation chance");
        singleSpreadsIncrement = config.getBoolean("Single spread stat increase",CATEGORY_AGRICRAFT, false, "Set to true to allow crops that spread from one single crop to increase stats");
        spreadingDifficulty = config.getInt("Farming difficulty",CATEGORY_AGRICRAFT,3,1,3, "Farming difficulty: 1 = Crops can inherit stats from any crop. 2 = Crops only inherit stats from parent and identical crops. 3 = Same as 2, but any other nearby crop will affect stats negatively.");
        cropsPerCraft = config.getInt("Crops per craft", CATEGORY_AGRICRAFT, 1, 1, 4, "The number of crops you get per crafting operation");
        cropStatCap = config.getInt("Crop stat cap", CATEGORY_AGRICRAFT, 10, 1, 10, "The maximum attainable value of the stats on a crop");
        cropStatDivisor = config.getInt("Crop stat divisor", CATEGORY_AGRICRAFT, 2, 1, 3, "On a mutation the stats on the crop will be divided by this number");
        enableWeeds = config.getBoolean("Enable weeds", CATEGORY_AGRICRAFT, true, "set to false if you wish to disable weeds");
        weedsWipePlants = enableWeeds && config.getBoolean("Weeds can overtake plants",CATEGORY_AGRICRAFT,true,"set to false if you don't want weeds to be able to overgrow other plants");
        enableHandRake = enableWeeds && config.getBoolean("Enable Hand Rake", CATEGORY_AGRICRAFT, true, "When enabled, weeds can only be removed by using this Hand Rake tool");
        bonemealMutation = config.getBoolean("Bonemeal Mutations",CATEGORY_AGRICRAFT, false, "set to false if you wish to disable using bonemeal on a cross crop to force a mutation");
        disableVanillaFarming = config.getBoolean("Disable Vanilla Farming", CATEGORY_AGRICRAFT, false, "set to true to disable vanilla farming, meaning you can only grow plants on crops");
        disableWorldGen = config.getBoolean("Disable World Gen", CATEGORY_AGRICRAFT, false, "set to true to disable world gen, no greenhouses will spawn in villages");
        enableNEI = config.getBoolean("Enable NEI", CATEGORY_AGRICRAFT, true, "set to false if you wish to disable mutation recipes in NEI");
        propGenerateDefaults = config.get(CATEGORY_AGRICRAFT, "GenerateDefaults", false, "set to true to regenerate a default mutations file (will turn back to false afterwards)");
        generateDefaults = propGenerateDefaults.getBoolean();
        customCrops = config.getBoolean("Custom crops", CATEGORY_AGRICRAFT, false, "set to true if you wish to create your own crops");
        wipeTallGrassDrops = config.getBoolean("Clear tall grass drops", CATEGORY_AGRICRAFT, false, "set to true to clear the list of items dropping from tall grass (Will run before adding seeds defined in the grass drops config).");
        renderBookInAnalyzer = config.getBoolean("Render journal in analyzer", CATEGORY_AGRICRAFT, true, "set to false to not render the journal on the analyzer");
        cactusGivesCactus = config.getBoolean("Cactus produces cactus blocks", CATEGORY_AGRICRAFT, false, "set to true to make the cactus crop produce cactus blocks instead of cactus green");

        disableSeedStorage = config.getBoolean("Disable seed storage system", CATEGORY_STORAGE, false, "set to true to disable the seed storage systems");
        disableSeedWarehouse = config.getBoolean("Disable seed storage warehouses", CATEGORY_STORAGE, false, "set to true to disable the seed storage warehouse blocks");

        disableIrrigation = config.getBoolean("Disable Irrigation", CATEGORY_IRRIGATION, false, "set to true if you want to disable irrigation systems");
        sprinklerRatePerSecond = config.getInt("Sprinkler water usage", CATEGORY_IRRIGATION, 10, 0, 10000, "Water usage of the sprinkler in mB per second");
        sprinklerRatePerHalfSecond = Math.round(sprinklerRatePerSecond / 2);
        sprinklerGrowthChance = config.getInt("Sprinkler growth chance", CATEGORY_IRRIGATION, 20, 0, 100, "Every x seconds each plant in sprinkler range has this chance to growth tick");
        sprinklerGrowthChancePercent = sprinklerGrowthChance / 100F;
        sprinklerGrowthInterval = config.getInt("Sprinkler growth interval", CATEGORY_IRRIGATION, 5, 1, 300, "Every x seconds each plant in sprinkler range has y chance to growth tick");
        sprinklerGrowthIntervalTicks = sprinklerGrowthInterval * 20;
        placeWater = config.getBoolean("Spawn water after breaking tank", CATEGORY_IRRIGATION, true, "set to false to disable placing a source block when breaking non-empty tanks");
        fillFromFlowingWater = config.getBoolean("Fill tank from flowing water", CATEGORY_IRRIGATION, false, "set to true to let tanks fill up when water flows above them");

        //mod integration
        integration_HC = ModIntegration.LoadedMods.harvestcraft && config.getBoolean("HarvestCraft",CATEGORY_INTEGRATION, true,"Set to false to disable automatic mutations for Pam's HarvestCraft");
        integration_Nat = ModIntegration.LoadedMods.natura && config.getBoolean("Natura",CATEGORY_INTEGRATION, true,"Set to false to disable automatic mutations for Natura");
        integration_WeeeFlowers = ModIntegration.LoadedMods.weeeFlowers && config.getBoolean("Weee Flowers",CATEGORY_INTEGRATION, true,"Set to false to disable automatic mutations for Pam's Weee Flowers");
        integration_PlantMegaPack = ModIntegration.LoadedMods.plantMegaPack && config.getBoolean("Plant Mega Pack",CATEGORY_INTEGRATION, true,"Set to false to disable automatic mutations for Plant Mega Pack");
        integration_Chococraft = ModIntegration.LoadedMods.chococraft && config.getBoolean("ChocoCraft",CATEGORY_INTEGRATION, true,"Set to false to disable automatic mutations for Chococraft");
        integration_Botania = ModIntegration.LoadedMods.botania && config.getBoolean("Botania", CATEGORY_INTEGRATION, true, "Set to false to disable Botania Mystical Flower Seeds");
        integration_allowMagicFertiliser = ModIntegration.LoadedMods.magicalCrops && config.getBoolean("Magical Crops Fertiliser",CATEGORY_INTEGRATION,true,"Set to false to disable using magical fertiliser on crops");
        integration_instantMagicFertiliser = ModIntegration.LoadedMods.magicalCrops && config.getBoolean("Magical Crops Fertiliser Instant Growth", CATEGORY_INTEGRATION, false, "Set to true to insta-grow plants on which the magical fertiliser is used on");
        integration_Psychedelicraft = ModIntegration.LoadedMods.psychedelicraft && config.getBoolean("Psychedelicraft",CATEGORY_INTEGRATION, true,"Set to false to disable automatic mutations for Psychedelicraft");

        villagerID = config.getInt("Villager ID", CATEGORY_AGRICRAFT, 10, 7, 99, "The profession ID the villager uses");
        villagerEnabled = config.getBoolean("Enable villagers",CATEGORY_AGRICRAFT, true, "Set to false if you wish to disable villagers spawning in the ArgiCraft houses");


        //toggle debug mode
        debug = config.getBoolean("debug",CATEGORY_DEBUG,false,"Set to true if you wish to enable debug mode");
        if(config.hasChanged()) {config.save();}
    }

    public static String readGrassDrops() {
        return IOHelper.readOrWrite(directory, "GrassDrops", IOHelper.getGrassDrops());
    }

    public static String readCustomCrops() {
        return IOHelper.readOrWrite(directory, "CustomCrops", IOHelper.getCustomCropInstructions());
    }

    public static String readMutationData() {
        String data = IOHelper.readOrWrite(directory, "Mutations", IOHelper.getDefaultMutations(), generateDefaults);
        if(generateDefaults) {
            ConfigurationHandler.propGenerateDefaults.setToDefault();
        }
        return data;
    }

    public static String readSpreadChances() {
        return IOHelper.readOrWrite(directory, "SpreadChancesOverrides", IOHelper.getSpreadChancesOverridesInstructions());
    }

    public static String readSeedTiers() {
        return IOHelper.readOrWrite(directory, "SeedTiers", IOHelper.getSeedTierOverridesInstructions());
    }

    public static String readSeedBlackList() {
        return IOHelper.readOrWrite(directory, "SeedBlackList", IOHelper.getSeedBlackListInstructions());
    }

    public static String readSoils() {
        return IOHelper.readOrWrite(directory, "SoilWhitelist", IOHelper.getSoilwhitelistData());
    }

    @SubscribeEvent
    public void onCOnfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Reference.MOD_ID)) {
            loadConfiguration();
            LogHelper.debug("Configuration reloaded.");
        }
    }
}
