package com.suppergerrie2.headpets.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.suppergerrie2.headpets.ITreatEffects;
import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.entities.EntityHead;
import com.suppergerrie2.headpets.entities.EntityHeadEvil;
import com.suppergerrie2.headpets.entities.EntityHeadPet;
import com.suppergerrie2.headpets.items.ItemCraftWand;
import com.suppergerrie2.headpets.items.ItemSpawnEvilHead;
import com.suppergerrie2.headpets.items.ItemSpawnPet;
import com.suppergerrie2.headpets.items.ItemTreat;
import com.suppergerrie2.headpets.items.crafting.SkullProfileCopierRecipe;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber(modid=Reference.MODID)
public class ModItems {

	public static final CreativeTabs tabHeadPets = new CreativeTabs("tabHeadPets") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Items.SKULL, 1 , 3);
		}

		@Override
		public boolean hasSearchBar() {
			return true;
		}

		@Override
		public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_) {
			super.displayAllRelevantItems(p_78018_1_);
			p_78018_1_.sort(new Comparator<ItemStack>() {

				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					
					//Check if both items are itemTreat items.
					if(o1.getItem() instanceof ItemTreat && o2.getItem() instanceof ItemTreat) {
						//Check if they have death effects
						if(((ItemTreat)o1.getItem()).effects.hasDeathEffect()) {
							//If they both have death effects, sort on name
							if(((ItemTreat)o2.getItem()).effects.hasDeathEffect()) {
								return o1.getDisplayName().compareTo(o2.getDisplayName());
							} else {
								//Else make sure o1 gets behind o2
								return 1;
							}
						
						//If o2 has the death effect but o1 not make sure it gets behind o1
						} else if(((ItemTreat)o2.getItem()).effects.hasDeathEffect()) {
							return -1;
						}
						
						//Else sort on name
						return o1.getDisplayName().compareTo(o2.getDisplayName());
					}
					
					//Determine which of the items is the ItemTreat and return the correct int
					if(o1.getItem() instanceof ItemTreat) {
						return 1;
					} else if (o2.getItem() instanceof ItemTreat) {
						return -1;
					}

					return o1.getDisplayName().compareTo(o2.getDisplayName());
				}

			});
		}



	}.setBackgroundImageName("item_search.png");

	public static Item spawnPet;
	public static Item craftWand;
	private static List<ItemTreat> treats = new ArrayList<ItemTreat>();

	private final static ItemStack[] treatTypes = new ItemStack[] {
			new ItemStack(Blocks.LOG2, 1,  0/*BlockPlanks.EnumType.ACACIA.getMetadata()*/),
			new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.ANDESITE.getMetadata()),
			new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata()),
			new ItemStack(Blocks.BLACK_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BLACK.getMetadata()),
			new ItemStack(Blocks.BLUE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BLUE.getMetadata()),
			new ItemStack(Blocks.BROWN_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BROWN.getMetadata()),
			new ItemStack(Blocks.COAL_BLOCK),
			new ItemStack(Blocks.COBBLESTONE),
			new ItemStack(Blocks.CYAN_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.CYAN.getMetadata()),
			new ItemStack(Blocks.LOG2, 1, 1/*BlockPlanks.EnumType.DARK_OAK.getMetadata()*/),
			new ItemStack(Blocks.DIAMOND_ORE),
			new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.DIORITE.getMetadata()),
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.EMERALD_ORE),
			new ItemStack(Items.EXPERIENCE_BOTTLE),
			new ItemStack(Blocks.GOLD_ORE),
			new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.GRANITE.getMetadata()),
			new ItemStack(Blocks.GRASS),
			new ItemStack(Blocks.GRAY_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.GRAY.getMetadata()),
			new ItemStack(Blocks.GREEN_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.GREEN.getMetadata()),
			new ItemStack(Blocks.IRON_ORE),
			new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()),
			new ItemStack(Blocks.LAPIS_BLOCK),
			new ItemStack(Blocks.LIGHT_BLUE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.LIGHT_BLUE.getMetadata()),
			new ItemStack(Blocks.SILVER_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.SILVER.getMetadata()),
			new ItemStack(Blocks.LIME_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.LIME.getMetadata()),
			new ItemStack(Blocks.MAGENTA_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.MAGENTA.getMetadata()),
			new ItemStack(Blocks.MOSSY_COBBLESTONE),
			new ItemStack(Blocks.MYCELIUM),
			new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata()),
			new ItemStack(Blocks.ORANGE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.ORANGE.getMetadata()),
			new ItemStack(Blocks.PINK_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.PINK.getMetadata()),
			new ItemStack(Blocks.PURPLE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.PURPLE.getMetadata()),
			new ItemStack(Blocks.QUARTZ_ORE),
			new ItemStack(Blocks.RED_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.RED.getMetadata()),
			new ItemStack(Blocks.REDSTONE_ORE),
			new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()),			
			new ItemStack(Blocks.WHITE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.WHITE.getMetadata()),
			new ItemStack(Blocks.YELLOW_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.YELLOW.getMetadata()),

	};

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		spawnPet = new ItemSpawnPet();
		craftWand = new ItemCraftWand();

		for(ItemStack stack : treatTypes) {
			treats.add(new ItemTreat(stack));
		}

		//Skull swarm
		treats.add(new ItemTreat("treat_skull_swarm", new ITreatEffects() {

			@Override
			public void onDeath(EntityHeadPet head, ItemTreat item, int level, Entity killer) {
				int amount = head.world.rand.nextInt(level)+6;
				for(int i = 0; i < amount; i++) {
					EntityHeadEvil evil = new EntityHeadEvil(head.world, head);

					double totalVel = 0.25;
					double arc = ((Math.PI*2)/amount)*i;

					double xVel = amount==1?0:Math.cos(arc)*totalVel;
					double zVel = amount==1?0:Math.sin(arc)*totalVel;

//					evil.setVelocity(xVel, 0.5 + head.world.rand.nextFloat()*0.1-0.05, zVel);
					evil.motionX = xVel;
					evil.motionY = 0.5 + head.world.rand.nextFloat()*0.1-0.05;
					evil.motionZ = zVel;
					
					head.world.spawnEntity(evil);
				}
			}
			
			public boolean hasDeathEffect() {
				return true;
			}

		}));

		//Bomb
		treats.add(new ItemTreat("treat_bomb", new ITreatEffects() {

			@Override
			public void onDeath(EntityHeadPet head, ItemTreat item, int level, Entity killer) {
				head.spawnExplosionParticle();
					EntityTNTPrimed tnt = new EntityTNTPrimed(head.world, head.posX, head.posY, head.posZ, head);
//					tnt.setVelocity(0, 0, 0);
					tnt.motionX = 0;
					tnt.motionY = 0;
					tnt.motionZ = 0;
					tnt.setFuse(0);

					head.world.spawnEntity(tnt);
			}
			
			public boolean hasDeathEffect() {
				return true;
			}

		}));

		//Bomb
		treats.add(new ItemTreat("treat_super_bomb", new ITreatEffects() {

			@Override
			public void onDeath(EntityHeadPet head, ItemTreat item, int level, Entity killer) {
				head.spawnExplosionParticle();
				int ringAmount = head.world.rand.nextInt(level+1)+2;
				int startTntAmount = (head.world.rand.nextInt(level)+2)*3;

				for(int ringIndex = 0; ringIndex < ringAmount; ringIndex++) {
					int amount =  startTntAmount*(ringIndex+1);
					for(int i = 0; i < amount; i++) {
						EntityTNTPrimed tnt = new EntityTNTPrimed(head.world, head.posX, head.posY, head.posZ, head);

						double totalVel = 0.5*(ringIndex+1);
						double arc = ((Math.PI*2)/amount)*i;

						double xVel = amount==1?0:Math.cos(arc)*totalVel;
						double zVel = amount==1?0:Math.sin(arc)*totalVel;

//						tnt.setVelocity(xVel, 0.6 + head.world.rand.nextFloat()*0.1-0.05, zVel);
						tnt.motionX = xVel;
						tnt.motionY = 0.6 + head.world.rand.nextFloat()*0.1-0.05;
						tnt.motionZ = zVel;
						tnt.setFuse(30);

						head.world.spawnEntity(tnt);
					}
				}
			}
			
			public boolean hasDeathEffect() {
				return true;
			}

		}));

		//Blood bunny swarm
		treats.add(new ItemTreat("treat_blood_bunny", new ITreatEffects() {

			@Override
			public void onDeath(EntityHeadPet head, ItemTreat item, int level, Entity killer) {
				int amount = head.world.rand.nextInt(level)+6;
				for(int i = 0; i < amount; i++) {
					EntityRabbit rabbit = new EntityRabbit(head.world);
					rabbit.setRabbitType(99);

					rabbit.setPosition(head.posX, head.posY, head.posZ);

					double totalVel = 0.25;
					double arc = ((Math.PI*2)/amount)*i;

					double xVel = amount==1?0:Math.cos(arc)*totalVel;
					double zVel = amount==1?0:Math.sin(arc)*totalVel;

//					rabbit.setVelocity(xVel, 0.5 + head.world.rand.nextFloat()*0.1-0.05, zVel);
					rabbit.motionX = xVel;
					rabbit.motionY = 0.5 + head.world.rand.nextFloat()*0.1-0.05;
					rabbit.motionZ = zVel;
					
					head.world.spawnEntity(rabbit);
				}
			}
			
			public boolean hasDeathEffect() {
				return true;
			}

		}));

		//Potion
		treats.add(new ItemTreat("treat_potion", new ITreatEffects() {

			final int potionTime = 20*60*2;

			@Override
			public void onDeath(EntityHeadPet head, ItemTreat item, int level, Entity killer) {

				for(int i = 0; i < level; i++) {
					ItemStack potion = new ItemStack(Items.SPLASH_POTION);
					potion = PotionUtils.appendEffects(potion, Arrays.asList(
							new PotionEffect(MobEffects.BLINDNESS, potionTime),
							new PotionEffect(MobEffects.HUNGER, potionTime),
							new PotionEffect(MobEffects.INSTANT_DAMAGE, potionTime),
							new PotionEffect(MobEffects.LEVITATION, potionTime),
							new PotionEffect(MobEffects.MINING_FATIGUE, potionTime),
							new PotionEffect(MobEffects.NAUSEA, potionTime),
							new PotionEffect(MobEffects.POISON, potionTime),
							new PotionEffect(MobEffects.SLOWNESS, potionTime),
							new PotionEffect(MobEffects.UNLUCK, potionTime),
							new PotionEffect(MobEffects.WEAKNESS, potionTime),
							new PotionEffect(MobEffects.WITHER, potionTime)));

					EntityPotion potionEntity = new EntityPotion(head.world, head.posX, head.posY+1.5, head.posZ, potion);

					if(killer!=null&&i==0) {
						potionEntity.shoot(killer.posX-head.posX, killer.posY-head.posY+5, killer.posZ-head.posZ, 0.8f, 0);
					} else {
						potionEntity.shoot(head.world.rand.nextInt(4)-2, head.world.rand.nextInt(4), head.world.rand.nextInt(4)-2, 0.8f, 0);
					}

					head.world.spawnEntity(potionEntity);
				}
			}
			
			public boolean hasDeathEffect() {
				return true;
			}

		}));

		event.getRegistry().registerAll(spawnPet, craftWand);
		event.getRegistry().registerAll(treats.toArray(new ItemTreat[0]));

        event.getRegistry().register(new ItemSpawnEvilHead());

		for(Item item : treats) { 
			OreDictionary.registerOre("treat", item);
		}
	}

	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) {
		registerRender(spawnPet);
		registerRender(craftWand);
		for(ItemTreat item : treats) {
			registerRender(item);
		}
	}

	private static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
	}
	
}
