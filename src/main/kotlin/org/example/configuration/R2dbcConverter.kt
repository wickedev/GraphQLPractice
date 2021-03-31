package org.example.configuration

import com.expediagroup.graphql.generator.scalars.ID
import io.r2dbc.spi.Row
import org.slf4j.LoggerFactory
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.math.BigInteger
import java.util.*


@WritingConverter
class IDToLongWritingConverter : Converter<ID, Long> {
    private val log = LoggerFactory.getLogger(IDToLongWritingConverter::class.java)
    override fun convert(source: ID): Long {
        log.debug("convert() called with: source = $source")

        return if (source.value.isNotEmpty()) {
            convertToBigInteger(UUID.fromString(source.value)).longValueExact()
        } else {
            0
        }
    }
}

@ReadingConverter
class LongToIDReadingConverter : Converter<Long, ID> {
    private val log = LoggerFactory.getLogger(LongToIDReadingConverter::class.java)
    override fun convert(source: Long): ID {
        log.debug("convert() called with: source = $source")
        return ID(convertFromBigInteger(source.toBigInteger()).toString())
    }
}

val B: BigInteger = BigInteger.ONE.shiftLeft(64) // 2^64

val L: BigInteger = BigInteger.valueOf(Long.MAX_VALUE)

fun convertToBigInteger(id: UUID): BigInteger {
    var lo = BigInteger.valueOf(id.leastSignificantBits)
    var hi = BigInteger.valueOf(id.mostSignificantBits)

    // If any of lo/hi parts is negative interpret as unsigned
    if (hi.signum() < 0) hi = hi.add(B)
    if (lo.signum() < 0) lo = lo.add(B)
    return lo.add(hi.multiply(B))
}

fun convertFromBigInteger(x: BigInteger): UUID {
    val parts = x.divideAndRemainder(B)
    var hi = parts[0]
    var lo = parts[1]
    if (L < lo) lo = lo.subtract(B)
    if (L < hi) hi = hi.subtract(B)
    return UUID(hi.longValueExact(), lo.longValueExact())
}

inline fun <reified T> ConversionService.convert(target: Any?): T {
    return convert(target, T::class.java)
}
