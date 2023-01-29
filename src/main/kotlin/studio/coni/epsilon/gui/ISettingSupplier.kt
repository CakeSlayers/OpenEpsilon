package studio.coni.epsilon.gui

import studio.coni.epsilon.setting.AbstractSetting

interface ISettingSupplier<T> {
    val setting: AbstractSetting<T>
}