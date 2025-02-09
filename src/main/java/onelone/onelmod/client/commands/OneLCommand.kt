package onelone.onelmod.client.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import onelone.onelmod.client.OneLClient
import onelone.onelmod.client.config.Config
import onelone.onelmod.client.language.Compiler
import onelone.onelmod.client.language.LanguageError

object OneLCommand {
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource?>) {
        val expressionCommand = ClientCommandManager.literal("expr")
            .then(ClientCommandManager.argument("expression", StringArgumentType.greedyString())
                .executes(executeExpression)
            )

        val command = dispatcher.register(ClientCommandManager.literal("onelmod")
            .then(ClientCommandManager.literal("nbt").executes(showNBT))
            .then(expressionCommand)
            .then(ClientCommandManager.literal("gui").executes(openSettings))
            .executes(openSettings)
        )

        dispatcher.register(expressionCommand)
        dispatcher.register(
            ClientCommandManager.literal("onel")
                .redirect(command)
                .executes(openSettings)
        )
    }

    private val openSettings = { _: CommandContext<FabricClientCommandSource> ->
        OneLClient.mc.send {
            OneLClient.mc.setScreen(Config.handler.generate(null))
        }

        Command.SINGLE_SUCCESS
    }

    private val showNBT = { _: CommandContext<FabricClientCommandSource> ->
        val mainHand = OneLClient.mc.player!!.mainHandStack
        val comps = mainHand.components
        val builder = Text.literal("")

        builder.append(mainHand.toHoverableText().copy().formatted(Formatting.RED))
        builder.append(Text.literal(" {").formatted(Formatting.GRAY))

        for (comp in comps) {
            builder.append(Text.of("\n  "))
            builder.append(Text.literal(comp.type.toString()).formatted(Formatting.AQUA))
            builder.append(Text.literal(": "))
            builder.append(Text.literal(comp.value.toString()).formatted(Formatting.GREEN))
            builder.append(Text.literal(","))
        }

        builder.append(Text.literal("\n}").formatted(Formatting.GRAY))
        OneLClient.logChat(builder)

        Command.SINGLE_SUCCESS
    }

    private val executeExpression = { ctx: CommandContext<FabricClientCommandSource> ->
        val expr = StringArgumentType.getString(ctx, "expression")
        val res = Compiler.compileToExpression(expr)

        if (res.isFailure) {
            val exception = res.exceptionOrNull()!! as LanguageError

            throw CommandSyntaxException(
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
                Text.of("Error at input column ${exception.span.start+1}: ${exception.message}")
            )
        } else {
            val client = MinecraftClient.getInstance()
            client.player!!.networkHandler.sendChatCommand("num ${res.getOrNull()!!}")

            Command.SINGLE_SUCCESS
        }
    }
}