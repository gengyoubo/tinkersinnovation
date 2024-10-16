package com.gjhi.tinkersinnovation.modifiers;

import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.*;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.*;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

import javax.annotation.Nullable;

import java.util.Iterator;

import static com.gjhi.tinkersinnovation.register.TinkersInnovationMaterials.polychrome_alloy;
import static com.gjhi.tinkersinnovation.register.TinkersInnovationMaterials.slimton;
import static net.minecraft.world.level.Level.OVERWORLD;
import static slimeknights.tconstruct.tools.data.material.MaterialIds.*;

public class OmnipotentModifier extends Modifier implements VolatileDataModifierHook, MeleeDamageModifierHook, ToolStatsModifierHook, MeleeHitModifierHook, ProjectileHitModifierHook, ConditionalStatModifierHook, BreakSpeedModifierHook, ToolDamageModifierHook, DamageTakenModifierHook, ModifyDamageModifierHook, DamageDealtModifierHook, DamageBlockModifierHook, InventoryTickModifierHook {
    private static final String[] list = {
            //this mod
            polychrome_alloy.toString(),
            slimton.toString(),
            //tconstruct
            manyullyn.toString(),
            queensSlime.toString(),
            hepatizon.toString(),
            ancientHide.toString(),
            //tinkers calibration
            "tinkerscalibration:mangobberslime",
            "tinkerscalibration:mandite",
            "tinkerscalibration:darkmatter",
            "tinkerscalibration:redmatter",
            "tinkerscalibration:emperorslime",
            "tinkerscalibration:netherite",
            "tinkerscalibration:bnetherite",
            "tinkerscalibration:fnetherite",
            "tinkerscalibration:gnetherite",
            "tinkerscalibration:wnetherite",
            "tinkerscalibration:snetherite",
            "tinkerscalibration:pnetherite",
            "tinkerscalibration:prnetherite",
            "tinkerscalibration:enetherite",
            "tinkerscalibration:fazelle",
            "tinkerscalibration:breashell",
            "tinkerscalibration:gobbernether",
            "tinkerscalibration:oraclium",
            "tinkerscalibration:soulgold",
            "tinkerscalibration:jazz",
            "tinkerscalibration:wither",
            "tinkerscalibration:gravity",
            //tinkers thinking
            "tinkers_thinking:stewium",
            //tinkers ingenuity
            "tinkers_ingenuity:bedrock_alloy_material",
            "tinkers_ingenuity:blood_binding_material",
            "tinkers_ingenuity:blood_steel_material",
            "tinkers_ingenuity:blue_sky_material",
            "tinkers_ingenuity:crocell_material",
            "tinkers_ingenuity:crystal_matrix_material",
            "tinkers_ingenuity:dye_fire_material",
            "tinkers_ingenuity:etherium_material",
            "tinkers_ingenuity:evil_material",
            "tinkers_ingenuity:fire_steel_material",
            "tinkers_ingenuity:gaia_material",
            "tinkers_ingenuity:glasya_material",
            "tinkers_ingenuity:gleiter_material",
            "tinkers_ingenuity:ice_steel_material",
            "tinkers_ingenuity:ignitium_material",
            "tinkers_ingenuity:infinity_material",
            "tinkers_ingenuity:knight_crystal_material",
            "tinkers_ingenuity:lighting_steel_material",
            "tinkers_ingenuity:neutronium_material",
            "tinkers_ingenuity:ocean_alloy_material",
            "tinkers_ingenuity:prince_slime_material",
            "tinkers_ingenuity:sea_dream_material",
            "tinkers_ingenuity:shine_alloy_material",
            "tinkers_ingenuity:shine_gold_material",
            "tinkers_ingenuity:simir_material",
            "tinkers_ingenuity:sunlit_material",
            "tinkers_ingenuity:teslin_alloy_material",
            "tinkers_ingenuity:twilight_material",
            "tinkers_ingenuity:xuan_ming_material",
            "tinkers_ingenuity:zesley_material",
    };

    public OmnipotentModifier() {
        MinecraftForge.EVENT_BUS.addListener(this::leftClickBlock);
    }

    private void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getPlayer();
        if (player != null && player.getMainHandItem().is(TinkerTags.Items.MODIFIABLE)) {
            ToolStack tool = ToolStack.from(player.getMainHandItem());
            if (comp(tool, "tinkers_ingenuity:bedrock_alloy_material") > 0) {
                VoidModifier breakBlock = new VoidModifier();
                breakBlock.leftClickBlock(event);
            }
        }
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    protected void registerHooks(ModifierHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, TinkerHooks.MELEE_DAMAGE, TinkerHooks.MELEE_HIT, TinkerHooks.PROJECTILE_HIT, TinkerHooks.TOOL_STATS, TinkerHooks.VOLATILE_DATA, TinkerHooks.CONDITIONAL_STAT, TinkerHooks.BREAK_SPEED, TinkerHooks.TOOL_DAMAGE, TinkerHooks.DAMAGE_TAKEN, TinkerHooks.MODIFY_DAMAGE, TinkerHooks.DAMAGE_DEALT, TinkerHooks.DAMAGE_BLOCK, TinkerHooks.INVENTORY_TICK);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        int partnum;
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:etherium_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (player.level.dimension().equals(OVERWORLD)) {
                knockback += partnum;
            }
        }
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:sea_dream_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,partnum*100,modifier.getLevel()-1));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,partnum*100,modifier.getLevel()-1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,partnum*100,modifier.getLevel()-1));
        }
        return knockback;
    }

    @Override
    public void afterMeleeHit(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        int partnum;
        if (target != null && comp(tool, "tinkerscalibration:redmatter", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED) > 0) {
            target.setHealth(target.getHealth() * 0.8f);
        } else if (target != null && comp(tool, "tinkerscalibration:darkmatter", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED) > 0) {
            target.setHealth(target.getHealth() * 0.9f);
        }
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:blood_steel_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (RANDOM.nextFloat() > 0.1 * partnum * modifier.getLevel())
                tool.setDamage(Math.max(0, tool.getDamage() - 1));
        }
        if (player != null && target != null && (partnum = comp(tool, "tinkerscalibration:breashell", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20 * partnum));
            target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40 * partnum));
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20 * partnum));
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40 * partnum));
        }
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:blue_sky_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (!player.isOnGround())
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * partnum));
        }
        if (target != null && (partnum = comp(tool, "tinkers_ingenuity:shine_gold_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100 * partnum, modifier.getLevel() - 1));
        }
        if (player != null && target != null && (partnum = comp(tool, "tinkers_ingenuity:simir_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100 * partnum, modifier.getLevel() - 1));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 100 * partnum, modifier.getLevel() - 1));
        }
        if (target != null && (partnum = comp(tool, "tinkers_ingenuity:teslin_alloy_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100 * partnum, modifier.getLevel() - 1));
        }
        if (player != null && (partnum = comp(tool, "tinkerscalibration:soulgold", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100 * partnum, modifier.getLevel() - 1));
        }
        if (player != null && (partnum = comp(tool, "tinkerscalibration:wither", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (RANDOM.nextFloat() < 0.1f * modifier.getLevel()) {
                player.invulnerableTime = 20 * partnum;
            }
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:sunlit_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (player != null && player.level.isDay() && player.level.canSeeSky(player.blockPosition()) && !player.level.isRaining() && !player.level.isThundering()) {
                if (target != null) {
                    target.hurt(DamageSource.MAGIC, damageDealt * 0.1f);
                }
            }
        }
        if (target != null && comp(tool, "tinkerscalibration:mandite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED) > 0) {
            target.invulnerableTime = 0;
        }
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:crocell_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (damageDealt < 0.05 * partnum * modifier.getLevel()){
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 5));
            }
        }
        if (target != null && (partnum = comp(tool, "tinkers_ingenuity:infinity_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED )) > 0) {
            target.setHealth(1);
        }
        if (target != null && (partnum = comp(tool, "tinkers_ingenuity:evil_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED )) > 0) {
            if (target instanceof Shulker){
                target.kill();
            }
        }
    }

    @Override
    public int onDamageTool(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, int amount, @org.jetbrains.annotations.Nullable LivingEntity player) {
        int partnum;
        if (player != null && (partnum = comp(tool, "tinkerscalibration:fazelle")) > 0) {
            player.setSpeed(player.getSpeed() + player.getSpeed() * modifier.getLevel() * partnum * 0.1f);
        }
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:blood_binding_material")) > 0) {
            if (RANDOM.nextFloat() < 0.1 * partnum * modifier.getLevel())
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 1));
        }
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:neutronium_material")) > 0) {
            if (RANDOM.nextFloat() < 0.75)
                return 0;
        }
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:prince_slime_material")) > 0) {
            OverslimeModifier overslime = TinkerModifiers.overslime.get();
            if (RANDOM.nextFloat() < 0.8 * ((double) overslime.getOverslime(tool) /overslime.getOverslime(tool)))
                overslime.addOverslime(tool, 2 * partnum);
        }
        return amount;
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, @NotNull ModifierEntry modifier, @NotNull Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        boolean result = false;
        for (ModifierEntry entry : modifiers.getModifiers()) {
            if (entry.getId().toString().equals("tinkerscalibration:bloodthirsty"))
                result = true;
        }
        if (target != null && result && projectile instanceof AbstractArrow) {
            target.invulnerableTime = 0;
        }
        return false;
    }

    @Override
    public void addToolStats(@NotNull ToolRebuildContext context, @NotNull ModifierEntry modifier, @NotNull ModifierStatsBuilder builder) {
        int partnum;
        OverslimeModifier overslime = TinkerModifiers.overslime.get();
        if ((partnum = comp(context, hepatizon.toString(), TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.VELOCITY))
                ToolStats.VELOCITY.add(builder, context.getBaseStats().get(ToolStats.VELOCITY) * modifier.getLevel() * partnum * 0.1f);
            if (context.getBaseStats().hasStat(ToolStats.MINING_SPEED))
                ToolStats.MINING_SPEED.add(builder, context.getBaseStats().get(ToolStats.VELOCITY) * modifier.getLevel() * partnum * 0.1f);
        }
        if ((partnum = comp(context, hepatizon.toString(), TinkerTags.Items.ARMOR)) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.KNOCKBACK_RESISTANCE))
                ToolStats.KNOCKBACK_RESISTANCE.add(builder, context.getBaseStats().get(ToolStats.KNOCKBACK_RESISTANCE) * modifier.getLevel() * partnum * 0.1f);
            if (context.getBaseStats().hasStat(ToolStats.MINING_SPEED))
                ToolStats.ARMOR_TOUGHNESS.add(builder, context.getBaseStats().get(ToolStats.ARMOR_TOUGHNESS) * modifier.getLevel() * partnum * 0.1f);
        }
        if (comp(context, ancientHide.toString()) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.VELOCITY))
                ToolStats.VELOCITY.add(builder, modifier.getLevel() * 0.5);
            if (context.getBaseStats().hasStat(ToolStats.MINING_SPEED))
                ToolStats.MINING_SPEED.add(builder, modifier.getLevel() * 0.5);
            if (context.getBaseStats().hasStat(ToolStats.ATTACK_DAMAGE))
                ToolStats.ATTACK_DAMAGE.add(builder, modifier.getLevel() * 1.5);
        }
        if ((partnum = comp(context, TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.DURABILITY))
                ToolStats.DURABILITY.add(builder, modifier.getLevel() * partnum * 50);
            if (context.getBaseStats().hasStat(ToolStats.ATTACK_DAMAGE))
                ToolStats.ATTACK_DAMAGE.add(builder, modifier.getLevel() * partnum * 2);
        }
        if ((partnum = comp(context, TinkerTags.Items.ARMOR)) > 0){
            if (context.getBaseStats().hasStat(ToolStats.DURABILITY))
                ToolStats.DURABILITY.add(builder, modifier.getLevel() * partnum * 100);
            if (context.getBaseStats().hasStat(ToolStats.ARMOR))
                ToolStats.ARMOR.add(builder, modifier.getLevel() * partnum * 4);
            if (context.getBaseStats().hasStat(ToolStats.ARMOR_TOUGHNESS))
                ToolStats.ARMOR_TOUGHNESS.add(builder, modifier.getLevel() * partnum * 2);
        }
        if ((partnum = comp(context, "tinkers_thinking:stewium")) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.DURABILITY))
                ToolStats.DURABILITY.add(builder, context.getStats().get(ToolStats.DURABILITY) * 0.2);
        }
        if ((partnum = comp(context, "tinkers_ingenuity:gaia_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.ATTACK_SPEED))
                ToolStats.ATTACK_SPEED.add(builder, context.getStats().get(ToolStats.ATTACK_SPEED) * 0.25 * modifier.getLevel());
        }
        if ((partnum = comp(context, "tinkers_ingenuity:glasya_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.ATTACK_DAMAGE))
                ToolStats.ATTACK_DAMAGE.add(builder, context.getStats().get(ToolStats.ATTACK_DAMAGE) * 0.2 * partnum);
        }
        if ((partnum = comp(context, "tinkers_ingenuity:gleiter_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.ATTACK_DAMAGE))
                ToolStats.ATTACK_DAMAGE.add(builder, context.getStats().get(ToolStats.ATTACK_DAMAGE) * 0.2 * partnum);
        }
        if ((partnum = comp(context, "tinkers_ingenuity:twilight_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (context.getBaseStats().hasStat(ToolStats.ATTACK_SPEED))
                ToolStats.ATTACK_SPEED.add(builder, 0.5);
        }
    }

    @Override
    public float modifyStat(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
        int partnum;
        OverslimeModifier overslime = TinkerModifiers.overslime.get();
        if ((partnum = comp(tool, slimton.toString(), TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            float current = (float) overslime.getOverslime(tool);
            if (stat == ToolStats.DRAW_SPEED) {
                return (float) (baseValue + Math.sqrt(current) * modifier.getLevel() * partnum * 0.01 * tool.getMultiplier(ToolStats.DRAW_SPEED));
            } else if (stat == ToolStats.ACCURACY) {
                return (float) (baseValue + Math.sqrt(current) * modifier.getLevel() * partnum * 0.01 * tool.getMultiplier(ToolStats.DRAW_SPEED));
            } else if (stat == ToolStats.VELOCITY) {
                return (float) (baseValue + Math.sqrt(current) * modifier.getLevel() * partnum * 0.01 * tool.getMultiplier(ToolStats.DRAW_SPEED));
            }
        }
        return baseValue;
    }

    @Override
    public void onBreakSpeed(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, PlayerEvent.BreakSpeed event, @NotNull Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        // the speed is reduced when not on the ground, cancel out
        Player player = event.getPlayer();
        int partnum;
        partnum = comp(tool, "tinkerscalibration:netherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        partnum += comp(tool, "tinkerscalibration:bnetherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        partnum += comp(tool, "tinkerscalibration:fnetherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        partnum += comp(tool, "tinkerscalibration:gnetherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        partnum += comp(tool, "tinkerscalibration:wnetherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        partnum += comp(tool, "tinkerscalibration:snetherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        partnum += comp(tool, "tinkerscalibration:pnetherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        partnum += comp(tool, "tinkerscalibration:prnetherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        partnum += comp(tool, "tinkerscalibration:enetherite", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST);
        if (partnum > 0 && player != null && player.getY() < 64) {
            event.setNewSpeed((float) (event.getNewSpeed() + event.getNewSpeed() * partnum * modifier.getLevel() * 0.01 * (64 - player.getY())));
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:etherium_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST)) > 0) {
            if (player != null && player.level.dimension().equals(OVERWORLD)) {
                event.setNewSpeed(event.getNewSpeed() + event.getNewSpeed() * 0.5f * partnum);
            }
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:shine_alloy_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST)) > 0) {
            if (player != null && player.level.isDay()) {
                event.setNewSpeed(event.getNewSpeed() + event.getNewSpeed() * 0.1f * partnum * modifier.getLevel());
            }
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:zesley_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST)) > 0) {
            if (player != null) {
                float num = (player.getMaxHealth() - player.getHealth()) / 2;
                event.setNewSpeed(event.getNewSpeed() + event.getNewSpeed() * Math.min( 0.75f, 0.075f * num) * partnum);
            }
        }
    }

    @Override
    public float getMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        int partnum;
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && (partnum = comp(tool, manyullyn.toString(), TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            damage += (target.getMaxHealth() - target.getHealth()) * (0.1f + 0.05f * modifier.getLevel() * partnum);
        }
        if (player != null && (partnum = comp(tool, "tinkers_ingenuity:knight_crystal_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            damage += (player.getMaxHealth() - player.getHealth()) * (0.25f * modifier.getLevel() * partnum);
        }
        if (player != null && target != null && (partnum = comp(tool, "tinkerscalibration:gravity", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            damage += target.fallDistance * partnum;
            damage += player.fallDistance * partnum;
            player.resetFallDistance();
        }
        if (target != null && (partnum = comp(tool, "tinkerscalibration:jazz", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            damage += damage * RANDOM.nextFloat();
        }
        if (target != null && (partnum = comp(tool, "tinkers_ingenuity:crystal_matrix_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (tool.getDamage() * 2 > tool.getCurrentDurability())
                damage += damage * 0.2f * partnum;
        }
        if (target != null && (partnum = comp(tool, "tinkers_ingenuity:ocean_alloy_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (target.isInWaterOrBubble()){
                damage += damage * 0.2f * partnum;
            }
        }
        if (target != null && (partnum = comp(tool, "tinkers_ingenuity:dye_fire_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (target.isOnFire() || target.isInWaterRainOrBubble())
                damage += 3 * partnum;
        }
        partnum = comp(tool, "tinkers_ingenuity:fire_steel_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED);
        partnum += comp(tool, "tinkers_ingenuity:ice_steel_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED);
        partnum += comp(tool, "tinkers_ingenuity:lighting_steel_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED);
        if (target != null && partnum > 0) {
            if (target instanceof EnderDragon){
                damage += 4 * partnum * modifier.getLevel();
            }
        }
        if (target != null && (partnum = comp(tool, "tinkers_ingenuity:ignitium_material", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            if (target.isOnFire()){
                damage += 4 * partnum * modifier.getLevel();
            }
        }
        if (target != null && (partnum = comp(tool, "tinkerscalibration:oraclium", TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.RANGED)) > 0) {
            target.setHealth(target.getHealth() - damage * 0.1f * partnum);
            damage -= damage * 0.1f * partnum;
        }
        if (target != null && comp(tool, "tinkerscalibration:mandite") > 0) {
            target.invulnerableTime = 0;
        }
        return damage;
    }

    @Override
    public void addVolatileData(@NotNull ToolRebuildContext context, @NotNull ModifierEntry modifier, @NotNull ModDataNBT volatileData) {
        OverslimeModifier overslime = TinkerModifiers.overslime.get();
        overslime.setFriend(volatileData);
        int partnum;
        if ((partnum = comp(context, "tinkerscalibration:mangobberslime")) > 0) {
            volatileData.addSlots(SlotType.ABILITY, partnum);
            volatileData.addSlots(SlotType.UPGRADE, partnum);
            if (context.hasTag(TinkerTags.Items.ARMOR)) {
                volatileData.addSlots(SlotType.DEFENSE, partnum);
            }
        }
        if ((partnum = comp(context, "tinkerscalibration:gobbernether")) > 0) {
            volatileData.addSlots(SlotType.ABILITY, 1);
        }
        if ((partnum = comp(context, queensSlime.toString())) > 0) {
            overslime.addCapacity(volatileData, 200 * partnum * modifier.getLevel());
        }
        if ((partnum = comp(context, "tinkerscalibration:emperorslime")) > 0) {
            overslime.addCapacity(volatileData, (int) (overslime.getCapacity(volatileData) * 0.2 * modifier.getLevel() * partnum + 0.5));
        }
        if ((partnum = comp(context, "tinkers_thinking:stewium")) > 0) {
            overslime.setCapacity(volatileData, (int) (overslime.getCapacity(volatileData) * 0.8));
        }
    }
    @Override
    public void onDamageTaken(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity wearer = context.getEntity();
        LivingEntity target = null;
        if (source.getEntity() instanceof LivingEntity entity)
            target = entity;
        int partnum;
        if ((partnum = comp(tool, "tinkers_ingenuity:blue_sky_material", TinkerTags.Items.ARMOR)) > 0) {
            wearer.resetFallDistance();
            if (target != null) {
                target.fallDistance *= 1.5f;
            }
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:knight_crystal_material", TinkerTags.Items.ARMOR)) > 0) {
            wearer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,200 * modifier.getLevel(), partnum - 1));
        }
        if ( (partnum = comp(tool, "tinkers_ingenuity:simir_material", TinkerTags.Items.ARMOR)) > 0) {
            wearer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, partnum - 1));
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:sunlit_material", TinkerTags.Items.ARMOR)) > 0) {
            if (RANDOM.nextFloat() < 0.1 * partnum * modifier.getLevel()) {
                wearer.setSecondsOnFire(10);
                if (target != null) {
                    target.setSecondsOnFire(10);
                }
            }
        }
        if ( (partnum = comp(tool, "tinkers_ingenuity:teslin_alloy_material", TinkerTags.Items.ARMOR)) > 0) {
            if (RANDOM.nextFloat() <0.1 * partnum * modifier.getLevel()){
                tool.setDamage(tool.getDamage()-10);
            }
        }
    }
    @Override
    public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, @NotNull LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity wearer = context.getEntity();
        int partnum;
        if ((partnum = comp(tool, "tinkers_ingenuity:crocell_material", TinkerTags.Items.ARMOR)) > 0) {
            if (amount < 0.05 * partnum * modifier.getLevel() * target.getMaxHealth()){
                wearer.setHealth(Math.min(wearer.getMaxHealth(), wearer.getHealth() + 5));
            }
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:shine_gold_material", TinkerTags.Items.ARMOR)) > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100 * partnum, modifier.getLevel() - 1));
        }
        if ((partnum = comp(tool, "tinkerscalibration:soulgold", TinkerTags.Items.ARMOR)) > 0) {
            wearer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100 * partnum, modifier.getLevel() - 1));
        }
        if ((partnum = comp(tool, "tinkerscalibration:wither", TinkerTags.Items.ARMOR)) > 0) {
            if (RANDOM.nextFloat() < 0.1f * modifier.getLevel()) {
                wearer.invulnerableTime = 20 * partnum;
            }
        }
    }
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        LivingEntity wearer = context.getEntity();
        int partnum;
        if ((partnum = comp(tool, "tinkers_ingenuity:dye_fire_material", TinkerTags.Items.ARMOR)) > 0) {
            return source.isFire();
        }
        if ((partnum = comp(tool, "tinkerscalibration:redmatter", TinkerTags.Items.ARMOR)) > 0) {
            return RANDOM.nextFloat() < 0.2;
        }else if ((partnum = comp(tool, "tinkerscalibration:darkmatter", TinkerTags.Items.ARMOR)) > 0) {
            return RANDOM.nextFloat() < 0.1;
        }
        return false;
    }
    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int partnum;
        LivingEntity wearer = context.getEntity();
        LivingEntity target = null;
        if (source.getEntity() instanceof LivingEntity entity)
            target = entity;
        partnum = comp(tool, "tinkerscalibration:netherite", TinkerTags.Items.ARMOR);
        partnum += comp(tool, "tinkerscalibration:bnetherite", TinkerTags.Items.ARMOR);
        partnum += comp(tool, "tinkerscalibration:fnetherite", TinkerTags.Items.ARMOR);
        partnum += comp(tool, "tinkerscalibration:gnetherite", TinkerTags.Items.ARMOR);
        partnum += comp(tool, "tinkerscalibration:wnetherite", TinkerTags.Items.ARMOR);
        partnum += comp(tool, "tinkerscalibration:snetherite", TinkerTags.Items.ARMOR);
        partnum += comp(tool, "tinkerscalibration:pnetherite", TinkerTags.Items.ARMOR);
        partnum += comp(tool, "tinkerscalibration:prnetherite", TinkerTags.Items.ARMOR);
        partnum += comp(tool, "tinkerscalibration:enetherite", TinkerTags.Items.ARMOR);
        if (partnum > 0 && source.isFire()){
            amount /= 2;
        }
        if ((partnum = comp(tool, "tinkersinnovation:slimton", TinkerTags.Items.ARMOR)) > 0) {
            if (wearer.getHealth()/wearer.getMaxHealth() < 0.2 * partnum){
                amount /= 2;
            }
        }
        if (target != null && (partnum = comp(tool, manyullyn.toString(), TinkerTags.Items.ARMOR)) > 0) {
            amount -= (target.getMaxHealth() - target.getHealth()) * (0.1f + 0.05f * modifier.getLevel() * partnum);
        }
        if ((partnum = comp(tool, "tinkerscalibration:oraclium", TinkerTags.Items.ARMOR)) > 0) {
            amount -= amount * 0.1f * partnum;
        }
        if ((partnum = comp(tool, "tinkerscalibration:jazz", TinkerTags.Items.ARMOR)) > 0) {
            if (amount / wearer.getMaxHealth() > 0.4 /(partnum * modifier.getLevel())){
                amount = wearer.getMaxHealth() * 0.4f /(partnum * modifier.getLevel());
            }
        }
        return Math.max(amount, 0);
    }
    @Override
    public void onInventoryTick (IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack){
        int partnum;
        if ((partnum = comp(tool, "tinkers_ingenuity:ocean_alloy_material", TinkerTags.Items.ARMOR)) > 0) {
            if (holder.isInWater() && !holder.hasEffect(MobEffects.WATER_BREATHING)){
                holder.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20));
            }
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:shine_alloy_material", TinkerTags.Items.ARMOR)) > 0) {
            if (world.isDay() && world.canSeeSky(holder.blockPosition()) && !world.isRaining() && !world.isThundering()){
                if (RANDOM.nextFloat() < 0.005){
                    holder.setHealth(Math.min(holder.getMaxHealth(), holder.getHealth() + partnum));
                }
            }
        }
        if ((partnum = comp(tool, "tinkers_ingenuity:xuan_ming_material", TinkerTags.Items.ARMOR)) > 0) {
            if (holder.getMaxHealth() == holder.getHealth()){
                if (RANDOM.nextFloat() < 0.005){
                    tool.setDamage(Math.max(0, tool.getDamage() - partnum));
                }
            }
        }
    }
    private static int comp(IToolStackView tool, String id) {
        return comp(tool, id, TinkerTags.Items.MODIFIABLE);
    }

    private static int comp(ToolRebuildContext tool, String id) {
        return comp(tool, id, TinkerTags.Items.MODIFIABLE);
    }

    @SafeVarargs
    private static int comp(IToolStackView tool, String id, TagKey<Item>... tags) {
        for (TagKey<Item> tag :tags) {
            if (tool.hasTag(tag)) {
                int level = 0;
                for (MaterialVariant material : tool.getMaterials().getList()){
                    if (material.getId().toString().equals(id)) {
                        level++;
                    }
                }
                return level;
            }
        }
        return 0;
    }

    @SafeVarargs
    private static int comp(ToolRebuildContext tool, String id, TagKey<Item>... tags) {
        for (TagKey<Item> tag :tags) {
            if (tool.hasTag(tag)) {
                int level = 0;
                for (MaterialVariant material : tool.getMaterials().getList()){
                    if (material.getId().toString().equals(id)) {
                        level++;
                    }
                }
                return level;
            }
        }
        return 0;
    }

    private static int comp(IToolStackView tool) {
        return comp(tool, TinkerTags.Items.MODIFIABLE);
    }

    private static int comp(ToolRebuildContext tool) {
        return comp(tool, TinkerTags.Items.MODIFIABLE);
    }

    @SafeVarargs
    private static int comp(IToolStackView tool, TagKey<Item>... tags) {
        int level = 0;
        for (TagKey<Item> tag :tags) {
            if (tool.hasTag(tag)) {
                for (MaterialVariant material : tool.getMaterials().getList()) {
                    if (material.get().getTier() >= 4 && notfind(material.getId().toString())) {
                        level++;
                    }
                }
                break;
            }
        }
        return level;
    }

    @SafeVarargs
    private static int comp(ToolRebuildContext tool, TagKey<Item>... tags) {
        int level = 0;
        for (TagKey<Item> tag :tags) {
            if (tool.hasTag(tag)) {
                for (MaterialVariant material : tool.getMaterials().getList()) {
                    if (material.get().getTier() >= 4 && notfind(material.getId().toString())) {
                        level++;
                    }
                }
                break;
            }
        }
        return level;
    }

    private static boolean notfind(String name) {
        for (String s : OmnipotentModifier.list) {
            if (name.equals(s))
                return false;
        }
        return true;
    }

}
