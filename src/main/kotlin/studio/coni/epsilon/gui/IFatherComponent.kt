package studio.coni.epsilon.gui

interface IFatherComponent : IComponent {
    var isActive: Boolean
    var children: MutableList<IChildComponent>
}