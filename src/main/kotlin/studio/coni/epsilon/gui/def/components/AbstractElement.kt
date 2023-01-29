package studio.coni.epsilon.gui.def.components

import studio.coni.epsilon.gui.IComponent

abstract class AbstractElement : IComponent, IAnimatable {
    override var height: Int = 0
    override var width: Int = 0
    override var x: Int = 0
    override var y: Int = 0

    open fun getDescription(): String {
        return ""
    }

}