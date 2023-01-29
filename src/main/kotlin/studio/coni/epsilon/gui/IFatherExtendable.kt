package studio.coni.epsilon.gui

import studio.coni.epsilon.util.Timer

interface IFatherExtendable : IFatherComponent {
    val timer: Timer
    var isPaused: Boolean
    var target: Int
    var current: Int
    var visibleChildren: List<IChildComponent>

    fun updateChildren() {
        visibleChildren = children.filter { it.isVisible() }
    }
}
