package studio.coni.epsilon.gui.def.theme

import studio.coni.epsilon.gui.ITheme
import studio.coni.epsilon.gui.ThemeCategory
import studio.coni.epsilon.gui.def.components.Panel
import studio.coni.epsilon.gui.def.components.elements.*
import studio.coni.epsilon.gui.def.components.elements.other.SBBox

interface IDefaultBothTheme : ITheme {

    override val category: ThemeCategory
        get() = ThemeCategory.Root

    fun actionButton(actionButton: ActionButton, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun bindButton(bindButton: BindButton, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun booleanButton(booleanButton: BooleanButton, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun colorPicker(colorPicker: ColorPicker, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun <T : Enum<T>> enumButton(enumButton: EnumButton<T>, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun moduleButton(moduleButton: ModuleButton, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun <T> numberSlider(
        numberSlider: NumberSlider<T>,
        mouseX: Int, mouseY: Int, partialTicks: Float
    ) where T : Comparable<T>, T : Number

    fun stringField(stringField: StringField, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun saturationBrightnessBox(box: SBBox, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun panel(panel: Panel, mouseX: Int, mouseY: Int, partialTicks: Float)

}