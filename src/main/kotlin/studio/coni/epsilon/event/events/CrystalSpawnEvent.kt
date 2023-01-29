package studio.coni.epsilon.event.events

import studio.coni.epsilon.event.Event
import studio.coni.epsilon.util.CrystalDamage

class CrystalSpawnEvent(
    val entityID: Int,
    val crystalDamage: CrystalDamage
) : Event()