package org.example.configuration

import com.expediagroup.graphql.generator.scalars.ID
import org.slf4j.LoggerFactory
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@WritingConverter
class IDToLongWritingConverter : Converter<ID, Long?> {
    private val log = LoggerFactory.getLogger(IDToLongWritingConverter::class.java)
    override fun convert(source: ID): Long? {
        log.info("convert() called with: source = $source")

        return if (source.value.isNotEmpty()) {
            source.value.toLong()
        } else {
            null
        }
    }
}

@ReadingConverter
class LongToIDReadingConverter : Converter<Long, ID> {
    private val log = LoggerFactory.getLogger(LongToIDReadingConverter::class.java)
    override fun convert(source: Long): ID {
        log.info("convert() called with: source = $source")
        return ID(source.toString())
    }
}

@WritingConverter
class OffsetDateTimeToLocalDateTimeWritingConverter : Converter<OffsetDateTime, LocalDateTime> {
    private val log = LoggerFactory.getLogger(OffsetDateTimeToLocalDateTimeWritingConverter::class.java)
    override fun convert(source: OffsetDateTime): LocalDateTime {
        log.debug("convert() called with: source = $source")
        return source.toLocalDateTime()
    }
}

@ReadingConverter
class LocalDateTimeToOffsetDateTimeReadingConverter : Converter<LocalDateTime, OffsetDateTime> {
    private val log = LoggerFactory.getLogger(LocalDateTimeToOffsetDateTimeReadingConverter::class.java)
    private val zoneId: ZoneId = ZoneId.systemDefault()

    override fun convert(source: LocalDateTime): OffsetDateTime {
        log.debug("convert() called with: source = $source")
        return source.atZone(zoneId).toOffsetDateTime()
    }
}

@WritingConverter
class ZonedDateTimeToLocalDateTimeWritingConverter : Converter<ZonedDateTime, LocalDateTime> {
    private val log = LoggerFactory.getLogger(ZonedDateTimeToLocalDateTimeWritingConverter::class.java)
    override fun convert(source: ZonedDateTime): LocalDateTime {
        log.debug("convert() called with: source = $source")
        return source.toLocalDateTime()
    }
}

@ReadingConverter
class LocalDateTimeToZonedDateTimeReadingConverter : Converter<LocalDateTime, ZonedDateTime> {
    private val log = LoggerFactory.getLogger(LocalDateTimeToZonedDateTimeReadingConverter::class.java)
    private val zoneId: ZoneId = ZoneId.systemDefault()

    override fun convert(source: LocalDateTime): ZonedDateTime {
        log.debug("convert() called with: source = $source")
        return source.atZone(zoneId)
    }
}

inline fun <reified T> ConversionService.convert(target: Any?): T {
    return convert(target, T::class.java)
}