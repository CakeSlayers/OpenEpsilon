package studio.coni.epsilon.util.extension

import studio.coni.epsilon.common.extensions.attackDamage
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.init.Enchantments
import net.minecraft.item.*
import kotlin.math.roundToInt

val ItemStack.originalName: String get() = item.getItemStackDisplayName(this)

val Item.id get() = Item.getIdFromItem(this)

val Item.block: Block get() = Block.getBlockFromItem(this)

val Item.isWeapon get() = this is ItemSword || this is ItemAxe

val Item.isTool: Boolean
    get() = this is ItemTool
            || this is ItemSword
            || this is ItemHoe
            || this is ItemShears

val ItemFood.foodValue get() = this.getHealAmount(ItemStack.EMPTY)

val ItemFood.saturation get() = foodValue * this.getSaturationModifier(ItemStack.EMPTY) * 2f

val ItemStack.attackDamage
    get() = this.item.baseAttackDamage + EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, this).let {
        if (it > 0) it * 0.5f + 0.5f
        else 0.0f
    }

val Item.baseAttackDamage
    get() = when (this) {
        is ItemSword -> this.attackDamage + 4.0f
        is ItemTool -> this.attackDamage + 1.0f
        else -> 1.0f
    }

val ItemStack.durability: Int
    get() = this.maxDamage - this.itemDamage

val ItemStack.duraPercentage: Int
    get() = (this.durability * 100.0f / this.maxDamage.toFloat()).roundToInt()

fun ItemStack.getEnchantmentLevel(enchantment: Enchantment) =
    EnchantmentHelper.getEnchantmentLevel(enchantment, this)
