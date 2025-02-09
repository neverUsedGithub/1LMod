package onelone.onelmod.client

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

typealias Task = () -> Unit

object Scheduler {
    class ScheduledTask(var time: Int, val task: Task)

    private val tasks: MutableList<ScheduledTask> = mutableListOf()

    fun task(timeout: Int, task: Task) {
        tasks.add(ScheduledTask(timeout, task))
    }

    init {
        ClientTickEvents.START_CLIENT_TICK.register {
            if (tasks.size == 0) return@register

            for (i in tasks.size-1..0) {
                tasks[i].time--

                if (tasks[i].time <= 0) {
                    tasks[i].task()
                    tasks.removeAt(i)
                }
            }
        }
    }
}