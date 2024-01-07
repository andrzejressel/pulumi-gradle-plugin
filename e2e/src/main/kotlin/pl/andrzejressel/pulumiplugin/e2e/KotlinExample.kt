package pl.andrzejressel.pulumiplugin.e2e

import com.pulumi.Context
import com.pulumi.command.local.kotlin.command
import com.pulumi.random.kotlin.randomString
import kotlinx.coroutines.runBlocking

object KotlinExample {
    fun run(context: Context) {
        runBlocking {
            val whoami = command("whoami") {
                args {
                    create("whoami")
                }
            }
            val randomString = randomString("random-string") {
                args {
                    length(8)
                }
            }
            context.export("string", randomString.result)
            context.export("whoami", whoami.stdout)
        }
    }
}
