package onelone.onelmod.client.mode.modes

import net.minecraft.block.Blocks
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import onelone.onelmod.client.mode.DFMode

import kotlin.math.abs

object DevMode : DFMode() {
    override val name: Text = Text.translatable("onelmod.mode.dev")
    override val icon: Item = Items.DIAMOND_BLOCK

    private const val BASE_Y_LEVEL = 49
    private const val CODE_AREA_LIMIT = 1000

    var codeLines: List<CodeLine> = listOf()
        private set

    private class CodeArea(val x1: Int, val z1: Int, val x2: Int, val z2: Int) {
        override fun toString(): String {
            return "CodeArea(x1=$x1, z1=$z1, x2=$x2, z2=$z2)"
        }
    }

    private fun findCodeArea(): CodeArea? {
        val client = MinecraftClient.getInstance()
        val world = client.world!!
        val playerPos = client.player!!.blockPos

        fun findBoundary(step: Int, axis: Char): Int? {
            var pos = if (axis == 'x') playerPos.x else playerPos.z
            val curr = if (axis == 'x') playerPos.x else playerPos.z

            while (abs(curr - pos) <= CODE_AREA_LIMIT) {
                val blockPos = if (axis == 'x') BlockPos(pos, BASE_Y_LEVEL, playerPos.z) else BlockPos(playerPos.x, BASE_Y_LEVEL, pos)
                val state = world.getBlockState(blockPos)

                if (state.block == Blocks.IRON_BLOCK || state.block == Blocks.STONE)
                    return pos

                pos += step
            }

            return null
        }

        val xMin = findBoundary(-1, 'x') ?: return null
        val xMax = findBoundary(1, 'x')  ?: return null
        val zMin = findBoundary(-1, 'z') ?: return null
        val zMax = findBoundary(1, 'z')  ?: return null

        return CodeArea(xMin, zMin, xMax, zMax)
    }

    enum class CodeLineType {
        EVENT,
        PROCESS,
        FUNCTION
    }

    class CodeLine(val type: CodeLineType, val name: String, val position: BlockPos) {
        class CodeBlock(val text: String) {}

        private var blocks: List<CodeBlock> = listOf()

        fun discover(world: ClientWorld) {
            val blocks = mutableListOf<CodeBlock>()
            var z = position.z + 2
            var state = world.getBlockState(BlockPos(position.x, position.y, z))

            while (state.block != Blocks.AIR) {
                if (state.block != Blocks.STONE) {
                    val signState = world.getBlockEntity(BlockPos(position.x - 1, position.y, z))

                    if (signState is SignBlockEntity) {
                        blocks.add(CodeBlock(signState.frontText.toString()))
                    }
                }

                z++
                state = world.getBlockState(BlockPos(position.x, position.y, z))
            }

            this.blocks = blocks
        }
    }

    private fun findCodeLines(area: CodeArea): List<CodeLine> {
        val codeLines: MutableList<CodeLine> = mutableListOf()
        val client = MinecraftClient.getInstance()
        val world = client.world!!
        var hasNextLayer = true
        val y = BASE_Y_LEVEL + 1

        while (hasNextLayer) {
            hasNextLayer = false

            for (x in area.x1..area.x2) {
                for (z in area.z1..area.z2) {
                    val pos = BlockPos(x, y, z)
                    val state = world.getBlockState(pos)
                    val block = state.block
                    var type: CodeLineType? = null

                    if (block == Blocks.DIAMOND_BLOCK) type = CodeLineType.EVENT
                    if (block == Blocks.EMERALD_BLOCK) type = CodeLineType.PROCESS
                    if (block == Blocks.LAPIS_BLOCK) type = CodeLineType.FUNCTION

                    if (type != null) {
                        val entity = world.getBlockEntity(BlockPos(x-1, y, z))

                        if (entity is SignBlockEntity) {
                            val name = entity.frontText.getMessage(1, false).string
                            val line = CodeLine(type, name, pos)

                            line.discover(world)

                            codeLines.add(line)
                        }
                    }
                }
            }
        }

        return codeLines
    }

//        val area = findCodeArea() ?: return
//        this.codeLines = findCodeLines(area)

    override fun onEnable() {}
    override fun onDisable() {}
    override fun onTick() {}
}