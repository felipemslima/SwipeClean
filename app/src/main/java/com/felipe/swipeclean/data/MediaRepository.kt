package com.felipe.swipeclean.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

/**
 * Lê fotos da galeria via MediaStore.
 *
 * DIFERENCIAL: itera sobre TODOS os volumes externos (getExternalVolumeNames),
 * o que inclui o cartão SD — por isso enxerga fotos que o app comum (que só lê
 * o volume primário) ignora.
 */
class MediaRepository(private val context: Context) {

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
    )

    /** Lê todas as fotos de todos os volumes. */
    fun loadAllPhotos(): List<Photo> {
        val result = ArrayList<Photo>()
        val volumes = MediaStore.getExternalVolumeNames(context)   // inclui o cartão SD

        for (volume in volumes) {
            val collection = MediaStore.Images.Media.getContentUri(volume)
            queryVolume(collection, volume, result)
        }
        // Mais recentes primeiro
        result.sortByDescending { it.dateAdded }
        return result
    }

    private fun queryVolume(collection: Uri, volume: String, out: MutableList<Photo>) {
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val bucketIdCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                out.add(
                    Photo(
                        id = id,
                        uri = ContentUris.withAppendedId(collection, id),
                        name = c.getString(nameCol) ?: "(sem nome)",
                        sizeBytes = c.getLong(sizeCol),
                        dateAdded = c.getLong(dateCol),
                        bucketId = c.getString(bucketIdCol) ?: "0",
                        bucketName = c.getString(bucketNameCol) ?: "Outros",
                        volumeName = volume,
                    )
                )
            }
        }
    }

    /** Agrupa as fotos em álbuns (buckets), com capa e tamanho total. */
    fun groupIntoAlbums(photos: List<Photo>): List<Album> =
        photos.groupBy { it.bucketId }
            .map { (bucketId, list) ->
                Album(
                    bucketId = bucketId,
                    name = list.first().bucketName,
                    photoCount = list.size,
                    totalBytes = list.sumOf { it.sizeBytes },
                    coverUri = list.firstOrNull()?.uri,
                )
            }
            .sortedByDescending { it.photoCount }
}
