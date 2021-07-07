package org.example.configuration

import com.expediagroup.graphql.generator.scalars.ID
import org.example.entity.User
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
    override fun convert(source: ID): Long? {
        return if (source.value.isNotEmpty()) {
            source.value.toLong()
        } else {
            null
        }
    }
}

@ReadingConverter
class LongToIDReadingConverter : Converter<Long, ID> {
    override fun convert(source: Long): ID {
        return ID(source.toString())
    }
}

@ReadingConverter
class StringToRoleReadingConverter : Converter<String, User.Role> {
    override fun convert(source: String): User.Role? = if (source.isNotEmpty()) {
        User.Role.valueOf(source)
    } else {
        null
    }
}

@WritingConverter
class RoleToStringWritingConverter : Converter<User.Role, String> {
    override fun convert(source: User.Role): String = source.name
}

@WritingConverter
class OffsetDateTimeToLocalDateTimeWritingConverter : Converter<OffsetDateTime, LocalDateTime> {
    override fun convert(source: OffsetDateTime): LocalDateTime {
        return source.toLocalDateTime()
    }
}

@ReadingConverter
class LocalDateTimeToOffsetDateTimeReadingConverter : Converter<LocalDateTime, OffsetDateTime> {
    private val zoneId: ZoneId = ZoneId.systemDefault()

    override fun convert(source: LocalDateTime): OffsetDateTime {
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
    private val zoneId: ZoneId = ZoneId.systemDefault()

    override fun convert(source: LocalDateTime): ZonedDateTime {
        return source.atZone(zoneId)
    }
}

inline fun <reified T> ConversionService.convert(target: Any?): T {
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    return convert(target, T::class.java)
}