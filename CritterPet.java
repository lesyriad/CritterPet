/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CritterPet;

import java.net.URL;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import Reika.CritterPet.Entities.EntitySpiderBase;
import Reika.CritterPet.Registry.CritterOptions;
import Reika.CritterPet.Registry.CritterType;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod( modid = "CritterPet", name="Critter Pet", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class CritterPet extends DragonAPIMod {

	@Instance("CritterPet")
	public static CritterPet instance = new CritterPet();

	public static final ControlledConfig config = new ControlledConfig(instance, CritterOptions.optionList, null, null, null, 0);

	public static ItemCritterEgg egg;
	public static ItemTaming tool;

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.CritterPet.CritterClient", serverSide="Reika.CritterPet.CritterCommon")
	public static CritterCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, CritterOptions.LOGLOADING.getState(), CritterOptions.DEBUGMODE.getState(), false);
		MinecraftForge.EVENT_BUS.register(this);

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);

		proxy.registerSounds();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		egg = new ItemCritterEgg(CritterOptions.EGGID.getValue());
		egg.setUnlocalizedName("petcritteregg");
		tool = new ItemTaming(CritterOptions.TOOLID.getValue());
		tool.setUnlocalizedName("crittertamer");
		for (int i = 0; i < CritterType.critterList.length; i++) {
			CritterType type = CritterType.critterList[i];
			if (type.isAvailable()) {
				int id = EntityRegistry.findGlobalUniqueEntityId();
				EntityRegistry.registerGlobalEntityID(type.entityClass, type.name, id);
				EntityRegistry.registerModEntity(type.entityClass, type.name, id, instance, 32, 20, true);
				type.initializeMapping(id);
				GameRegistry.addShapelessRecipe(new ItemStack(tool.itemID, 1, i+1), new ItemStack(tool.itemID, 1, 0), type.tamingItem);
				logger.log("Loading Critter Type "+type.name());
			}
			else {
				logger.log("Not Loading Critter Type "+type.name());
			}
		}
		proxy.registerRenderers();
		LanguageRegistry.addName(tool, "Critter Taming Device");
		GameRegistry.addRecipe(new ItemStack(tool), " ID", " II", "I  ", 'I', Item.ingotIron, 'D', Item.diamond);
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {

	}

	@ForgeSubscribe
	public void disallowDespawn(AllowDespawn d) {
		EntityLivingBase e = d.entityLiving;
		if (e instanceof EntitySpiderBase)
			d.setResult(Result.DENY);
	}

	@Override
	public String getDisplayName() {
		return "Critter Pet";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage(instance);
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return false;
	}

	@Override
	public String getVersionName() {
		return null;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

}