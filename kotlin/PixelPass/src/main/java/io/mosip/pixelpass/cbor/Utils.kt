package io.mosip.pixelpass.cbor

import android.annotation.SuppressLint
import android.os.Build
import co.nstant.`in`.cbor.CborDecoder
import co.nstant.`in`.cbor.model.Array
import co.nstant.`in`.cbor.model.ByteString
import co.nstant.`in`.cbor.model.DataItem
import co.nstant.`in`.cbor.model.DoublePrecisionFloat
import co.nstant.`in`.cbor.model.MajorType
import co.nstant.`in`.cbor.model.Map
import co.nstant.`in`.cbor.model.NegativeInteger
import co.nstant.`in`.cbor.model.SimpleValue
import co.nstant.`in`.cbor.model.SimpleValueType
import co.nstant.`in`.cbor.model.Special
import co.nstant.`in`.cbor.model.UnicodeString
import co.nstant.`in`.cbor.model.UnsignedInteger
import io.mosip.pixelpass.common.BuildConfig
import io.mosip.pixelpass.common.Encoder
import io.mosip.pixelpass.shared.isNegative
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONObject.NULL
import java.io.ByteArrayInputStream
import java.util.Base64
import java.util.Locale

class Utils {

    @SuppressLint("NewApi")
    fun toJson(base64UrlEncodedCborEncodedString: String): Any {
        val decodedData: ByteArray =
            Encoder().decodeFromBase64UrlFormatEncoded(base64UrlEncodedCborEncodedString)
        val cbor: DataItem? =
            CborDecoder(ByteArrayInputStream(decodedData)).decode()[0]
        return toJson(cbor!!)
    }

    fun toJson(dataItem: DataItem): Any {
        return if (dataItem.majorType == MajorType.MAP)
            mapToJson(JSONObject(), dataItem as Map)
        else
            arrayToJson(JSONArray(), dataItem as Array)
    }

    fun toDataItem(json: Any): DataItem {
        return if (json is JSONObject)
            mapToDataItem(Map(), json)
        else
            arrayToDataItem(Array(), json as JSONArray)

    }

    private fun mapToDataItem(accumulator: Map, jsonObject: JSONObject): Map {
        val iterator = jsonObject.keys().iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            when (val value = jsonObject.get(key)) {
                is JSONObject -> accumulator.put(UnicodeString(key.toString()), mapToDataItem(Map(), value))
                is JSONArray -> accumulator.put(UnicodeString(key.toString()), arrayToDataItem(Array(), value))
                is String -> accumulator.put(UnicodeString(key.toString()), UnicodeString(value))
                is Int -> {
                    if (value.isNegative())
                        accumulator.put(UnicodeString(key.toString()), NegativeInteger(value.toLong()))
                    else
                        accumulator.put(UnicodeString(key.toString()), UnsignedInteger(value.toLong()))
                }

                is Long -> accumulator.put(UnicodeString(key.toString()), DoublePrecisionFloat(value.toDouble()))
                is Double -> accumulator.put(UnicodeString(key.toString()), DoublePrecisionFloat(value))
                true -> accumulator.put(UnicodeString(key.toString()), SimpleValue(SimpleValueType.TRUE))
                false -> accumulator.put(UnicodeString(key.toString()), SimpleValue(SimpleValueType.FALSE))
                NULL -> accumulator.put(UnicodeString(key.toString()), SimpleValue(SimpleValueType.NULL))
                else -> accumulator.put(UnicodeString(key.toString()), SimpleValue(SimpleValueType.UNDEFINED))
            }
        }
        return accumulator
    }

    private fun arrayToDataItem(accumulator: Array, jsonArray: JSONArray): Array {
        for (i in 0 until jsonArray.length()) {
            when (val value = jsonArray.get(i)) {
                is JSONObject -> accumulator.add(mapToDataItem(Map(), value))
                is JSONArray -> accumulator.add(arrayToDataItem(Array(), value))
                is String -> accumulator.add(UnicodeString(value))
                is Int -> {
                    if (value.isNegative())
                        accumulator.add(NegativeInteger(value.toBigInteger()))
                    else
                        accumulator.add(UnsignedInteger(value.toBigInteger()))
                }

                is Long -> accumulator.add(DoublePrecisionFloat(value.toDouble()))
                is Double -> accumulator.add(DoublePrecisionFloat(value))
                true -> accumulator.add(SimpleValue(SimpleValueType.TRUE))
                false -> accumulator.add(SimpleValue(SimpleValueType.FALSE))
                NULL -> accumulator.add(SimpleValue(SimpleValueType.NULL))

                else -> accumulator.add(SimpleValue(SimpleValueType.UNDEFINED))
            }
        }
        return accumulator
    }

    private fun mapToJson(accumulator: JSONObject, map: Map): JSONObject {
        val iterator: Iterator<DataItem> = map.keys.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val key = next.toString()
            val dataItem = map.get(next)
            accumulator.put(key, parse(dataItem))
        }
        return accumulator
    }

    private fun arrayToJson(accumulator: JSONArray, array: Array): JSONArray {
        for (dataItem in array.dataItems) {
            accumulator.put(parse(dataItem))
        }
        return accumulator
    }

    private fun parse(dataItem: DataItem): Any? {
        return when (dataItem.majorType) {
            MajorType.MAP -> (mapToJson(JSONObject(), dataItem as Map))
            MajorType.ARRAY -> (arrayToJson(JSONArray(), dataItem as Array))
            MajorType.UNICODE_STRING -> ((dataItem as UnicodeString).string)
            MajorType.UNSIGNED_INTEGER,
            MajorType.NEGATIVE_INTEGER,
            -> ((dataItem.toString()).toInt())

            MajorType.BYTE_STRING -> {
                try {
                    val decoded =
                        CborDecoder(ByteArrayInputStream((dataItem as ByteString).bytes)).decode()

                    return parse(decoded[0])
                } catch (e: Exception) {
                    return (String((dataItem as ByteString).bytes))
                } catch (e: Error) {
                    return (String((dataItem as ByteString).bytes))
                }
            }

            MajorType.SPECIAL -> (getSpecial(dataItem.toString()))
            MajorType.INVALID -> (NULL)
            else -> (dataItem)
        }
    }

    private fun getSpecial(get: String): Any? {
        return try {
            get.toFloat()
        } catch (_: Exception) {
            try {
                if (get.lowercase(Locale.getDefault())
                        .toBooleanStrictOrNull() == null
                ) NULL else get.lowercase(Locale.getDefault()).toBooleanStrict()
            } catch (_: Exception) {
                return NULL
            }
        }
    }

}