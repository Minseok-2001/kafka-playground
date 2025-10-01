package minseok.kafkaplayground.common.support

import io.hypersistence.tsid.TSID
import org.slf4j.LoggerFactory

object TsidFactoryProvider {
    private const val DEFAULT_NODE_BITS = 10
    private const val ENV_NODE_ID = "TSID_NODE_ID"
    private const val ENV_NODE_BITS = "TSID_NODE_BITS"
    private const val PROP_NODE_ID = "tsid.node-id"
    private const val PROP_NODE_BITS = "tsid.node-bits"

    private val logger = LoggerFactory.getLogger(TsidFactoryProvider::class.java)

    private val factory: TSID.Factory = buildFactory()

    fun nextLong(): Long = factory.generate().toLong()

    fun nextString(): String = factory.generate().toString()

    private fun buildFactory(): TSID.Factory {
        val nodeBits = resolveInt(PROP_NODE_BITS, ENV_NODE_BITS) ?: DEFAULT_NODE_BITS
        require(nodeBits in 0..20) {
            "tsid.node-bits must be between 0 and 20, but was $nodeBits"
        }

        val nodeId = resolveInt(PROP_NODE_ID, ENV_NODE_ID)
        nodeId?.let {
            val maxNodes = 1 shl nodeBits
            require(it in 0 until maxNodes) {
                "tsid.node-id/TSID_NODE_ID must be between 0 (inclusive) and $maxNodes (exclusive) when nodeBits=$nodeBits, but was $it"
            }
        }

        val builder =
            TSID.Factory
                .builder()
                .withNodeBits(nodeBits)

        nodeId?.let { builder.withNode(it) }

        logger.info(
            "Configured TSID factory with nodeBits={} and nodeId={}",
            nodeBits,
            nodeId ?: "auto",
        )
        return builder.build()
    }

    private fun resolveInt(
        systemProperty: String,
        environmentVariable: String,
    ): Int? {
        val propertyValue = System.getProperty(systemProperty)?.takeIf { it.isNotBlank() }
        val rawValue =
            propertyValue ?: System.getenv(environmentVariable)?.takeIf { it.isNotBlank() }
        return rawValue?.let {
            runCatching { it.toInt() }
                .getOrElse { throw IllegalArgumentException("$systemProperty/$environmentVariable must be an integer, but was '$it'") }
        }
    }
}
