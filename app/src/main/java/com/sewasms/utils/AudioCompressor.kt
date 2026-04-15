package com.sewasms.utils

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import java.io.File

object AudioCompressor {

    fun compressAudio(inputFile: File, outputFile: File): Boolean {
        return try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(inputFile.absolutePath)

            val trackCount = mediaExtractor.trackCount
            if (trackCount == 0) return false

            mediaExtractor.selectTrack(0)
            val format = mediaExtractor.getTrackFormat(0)

            val reducedFormat = MediaFormat.createAudioFormat(
                format.getString(MediaFormat.KEY_MIME) ?: "audio/mp4a-latm",
                22050,
                1
            )
            reducedFormat.setInteger(MediaFormat.KEY_BIT_RATE, 32000)
            reducedFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodec.AVCProfileConstrainedBaseline)

            val mediaMuxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val trackId = mediaMuxer.addTrack(reducedFormat)
            mediaMuxer.start()

            val buffer = android.media.MediaCodec.BufferInfo()
            val bufferSize = 64 * 1024
            val byteBuffer = android.nio.ByteBuffer.allocateDirect(bufferSize)

            while (true) {
                val sampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (sampleSize < 0) break

                buffer.offset = 0
                buffer.size = sampleSize
                buffer.presentationTimeUs = mediaExtractor.sampleTime
                buffer.flags = mediaExtractor.sampleFlags

                mediaMuxer.writeSampleData(trackId, byteBuffer, buffer)
                mediaExtractor.advance()
            }

            mediaMuxer.stop()
            mediaMuxer.release()
            mediaExtractor.release()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun quickCompress(inputFile: File, outputFile: File): Boolean {
        return try {
            val inputBytes = inputFile.readBytes()

            val compressed = if (inputBytes.size > 100000) {
                inputBytes.take((inputBytes.size * 0.7).toInt()).toByteArray()
            } else {
                inputBytes
            }

            outputFile.writeBytes(compressed)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCompressionRatio(original: Long, compressed: Long): String {
        val ratio = (100 * (original - compressed) / original)
        return "$ratio%"
    }
}
