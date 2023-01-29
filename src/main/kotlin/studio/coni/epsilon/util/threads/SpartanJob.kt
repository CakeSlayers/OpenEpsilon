package studio.coni.epsilon.util.threads

class SpartanJob(private val task: () -> Unit) {

    var isFinished = false

    fun execute() {
        task.invoke()
        isFinished = true
    }

}