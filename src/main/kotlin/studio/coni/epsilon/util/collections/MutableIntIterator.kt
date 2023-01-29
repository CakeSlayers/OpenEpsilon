package studio.coni.epsilon.util.collections

interface MutableIntIterator : MutableIterator<Int> {
    override fun next(): Int {
        return nextInt()
    }

    fun nextInt(): Int
}