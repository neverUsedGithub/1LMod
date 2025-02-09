package onelone.onelmod.client.features.play

import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import onelone.onelmod.client.OneLClient
import onelone.onelmod.client.features.Feature
import kotlin.concurrent.thread
import kotlin.math.abs

object CodeHider : Feature() {
    private const val GROUND_Y = 49

    private var minRenderX: Int? = null
    private var maxRenderX: Int? = null
    private var minRenderZ: Int? = null
    private var maxRenderZ: Int? = null
    private var codeHiderTicker = 0

    private fun movePositionX(pos: BlockPos, amount: Int): BlockPos {
        return BlockPos(pos.x + amount, pos.y, pos.z)
    }

    private fun codeHiderX() {
        val maxOffset = OneLClient.mc.options.viewDistance.value * 16
        val player = OneLClient.mc.player ?: return
        val world = player.world ?: return

        if (minRenderX == null) {
            thread {
                var minX = BlockPos(player.pos.x.toInt(), GROUND_Y, player.pos.z.toInt())
                while (abs(minX.x - player.pos.x) <= maxOffset) {
                    if (world.getBlockState(minX).block == Blocks.STONE &&
                        world.getBlockState(movePositionX(minX, -1)).block == Blocks.GRASS_BLOCK &&
                        world.getBlockState(movePositionX(minX, -2)).block == Blocks.GRASS_BLOCK &&
                        world.getBlockState(movePositionX(minX, -3)).block == Blocks.GRASS_BLOCK &&
                        world.getBlockState(movePositionX(minX, -4)).block == Blocks.GRASS_BLOCK) {
                        minRenderX = minX.x + 20
                        OneLClient.shouldWorldRerender = true
                        codeHiderZ()
                        break
                    }

                    minX = movePositionX(minX, -1)
                }
            }
        }

        if (maxRenderX == null) {
            thread {
                var maxX = BlockPos(player.pos.x.toInt(), GROUND_Y, player.pos.z.toInt())
                while (abs(maxX.x - player.pos.x) <= maxOffset) {
                    if (world.getBlockState(maxX).block == Blocks.STONE &&
                        world.getBlockState(movePositionX(maxX, -1)).block == Blocks.GRASS_BLOCK &&
                        world.getBlockState(movePositionX(maxX, -2)).block == Blocks.GRASS_BLOCK &&
                        world.getBlockState(movePositionX(maxX, -3)).block == Blocks.GRASS_BLOCK &&
                        world.getBlockState(movePositionX(maxX, -4)).block == Blocks.GRASS_BLOCK) {
                        maxX = movePositionX(maxX, -1)
                        while (world.getBlockState(maxX).block == Blocks.GRASS_BLOCK) maxX = movePositionX(maxX, -1)
                        maxRenderX = maxX.x
                        OneLClient.shouldWorldRerender = true
                        codeHiderZ()
                        break
                    }

                    maxX = movePositionX(maxX, 1)
                }
            }
        }
    }

    private fun codeHiderZ() {
        val maxOffset = OneLClient.mc.options.viewDistance.value * 16
        val xPosition = minRenderX ?: maxRenderX ?: return
        val player = OneLClient.mc.player!!
        val world = OneLClient.mc.world!!

        if (minRenderZ == null) {
            thread {
                var minZ = BlockPos(xPosition, GROUND_Y, player.pos.z.toInt())

                while (abs(minZ.z - player.pos.z) <= maxOffset) {
                    if (world.getBlockState(minZ).block == Blocks.GRASS_BLOCK) {
                        minRenderZ = minZ.z + 1
                        OneLClient.shouldWorldRerender = true
                        break
                    }

                    minZ = BlockPos(minZ.x, minZ.y, minZ.z - 1)
                }
            }
        }

        if (maxRenderZ == null) {
            thread {
                var maxZ = BlockPos(xPosition, GROUND_Y, player.pos.z.toInt())
                while (abs(maxZ.z - player.pos.z) <= maxOffset) {
                    if (world.getBlockState(maxZ).block == Blocks.GRASS_BLOCK) {
                        maxRenderZ = maxZ.z - 1
                        OneLClient.shouldWorldRerender = true
                        break
                    }

                    maxZ = BlockPos(maxZ.x, maxZ.y, maxZ.z + 1)
                }
            }
        }
    }

    fun shouldRenderPos(requireWorldHider: Boolean, pos: BlockPos): Boolean {
        if (!isEnabled()) return true
        if (requireWorldHider && !WorldHider.isEnabled()) return true

        val minX = minRenderX
        val maxX = maxRenderX
        val minZ = minRenderZ
        val maxZ = maxRenderZ

        if (minX != null && pos.x < minX) return false
        if (maxX != null && pos.x > maxX) return false
        if (minZ != null && pos.z < minZ) return false
        if (maxZ != null && pos.z > maxZ) return false

        return true
    }

    override fun onEnable() {
        minRenderX = null
        maxRenderX = null
        minRenderZ = null
        maxRenderZ = null
        codeHiderTicker = 60
    }

    override fun onDisable() {
        OneLClient.shouldWorldRerender = true
    }

    override fun onTick() {
        codeHiderTicker++

        if (codeHiderTicker >= 40) {
            codeHiderTicker = 0

            codeHiderX()
            codeHiderZ()
        }
    }
}