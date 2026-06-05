package com.felipe.swipeclean.data

import android.net.Uri

/** Uma foto da galeria (de qualquer volume, incl. cartão SD). */
data class Photo(
    val id: Long,
    val uri: Uri,
    val name: String,
    val sizeBytes: Long,
    val dateAdded: Long,        // segundos desde epoch
    val bucketId: String,       // id do álbum/pasta
    val bucketName: String,     // nome do álbum/pasta
    val volumeName: String,     // "external_primary" ou id do cartão SD
)

/** Um álbum = um bucket do MediaStore (uma pasta), agregando contagem e tamanho. */
data class Album(
    val bucketId: String,
    val name: String,
    val photoCount: Int,
    val totalBytes: Long,
    val coverUri: Uri?,
)
