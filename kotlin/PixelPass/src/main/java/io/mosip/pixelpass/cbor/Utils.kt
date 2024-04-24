package io.mosip.pixelpass.cbor

import co.nstant.`in`.cbor.model.Array
import co.nstant.`in`.cbor.model.ByteString
import co.nstant.`in`.cbor.model.DataItem
import co.nstant.`in`.cbor.model.MajorType
import co.nstant.`in`.cbor.model.Map
import co.nstant.`in`.cbor.model.UnicodeString
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class Utils {
    fun toJson(dataItem: DataItem): Any {
        return if (dataItem.majorType == MajorType.MAP)
            mapToJson(JSONObject(), dataItem as Map)
        else
            arrayToJson(JSONArray(), dataItem as Array)
    }

    private fun mapToJson(accumulator: JSONObject, map: Map): JSONObject {
        val iterator: Iterator<DataItem> = map.keys.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val key = next.toString()
            val dataItem = map.get(next)
            when (dataItem.majorType) {
                MajorType.MAP -> accumulator.put(key, mapToJson(JSONObject(), dataItem as Map))
                MajorType.ARRAY -> accumulator.put(key, arrayToJson(JSONArray(), dataItem as Array))
                MajorType.UNICODE_STRING -> accumulator.put(key, (dataItem as UnicodeString).string)
                MajorType.UNSIGNED_INTEGER,
                MajorType.NEGATIVE_INTEGER -> accumulator.put(key, (dataItem.toString()).toInt())
                MajorType.BYTE_STRING -> accumulator.put(key, dataItem as ByteString)
                MajorType.SPECIAL -> accumulator.put(key, getSpecial(dataItem.toString()))
                MajorType.INVALID -> accumulator.put(key, JSONObject.NULL)
                else -> accumulator.put(key, dataItem)
            }
        }
        return accumulator
    }

    private fun arrayToJson(accumulator: JSONArray, array: Array): JSONArray {
        for (dataItem in array.dataItems) {
            when (dataItem.majorType) {
                MajorType.MAP -> accumulator.put(mapToJson(JSONObject(), dataItem as Map))
                MajorType.ARRAY -> accumulator.put(arrayToJson(JSONArray(), dataItem as Array))
                MajorType.UNICODE_STRING -> accumulator.put((dataItem as UnicodeString).string)
                MajorType.UNSIGNED_INTEGER,
                MajorType.NEGATIVE_INTEGER -> accumulator.put((dataItem.toString()).toInt())
                MajorType.BYTE_STRING -> accumulator.put(dataItem as ByteString)
                MajorType.SPECIAL -> accumulator.put(getSpecial(dataItem.toString()))
                MajorType.INVALID -> accumulator.put(JSONObject.NULL)
                else -> accumulator.put(dataItem)
            }
        }
        return accumulator
    }

    private fun getSpecial(get: String): Any? {
        return try {
            get.toFloat()
        } catch (_: Exception) {
            try {
                if (get.lowercase(Locale.getDefault())
                        .toBooleanStrictOrNull() == null
                ) JSONObject.NULL else get.lowercase(Locale.getDefault()).toBooleanStrict()
            } catch (_: Exception) {
                return JSONObject.NULL
            }
        }
    }

}