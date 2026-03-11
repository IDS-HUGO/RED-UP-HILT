package com.hugodev.red_up.features.publications.data.repositories

import com.hugodev.red_up.features.publications.data.datasources.remote.api.UpRedApi
import com.hugodev.red_up.features.publications.data.datasources.remote.mapper.toDomain
import com.hugodev.red_up.features.publications.data.datasources.remote.models.CreatePublicationRequestDto
import com.hugodev.red_up.features.publications.data.datasources.local.PublicationDao
import com.hugodev.red_up.features.publications.data.datasources.local.toDomain
import com.hugodev.red_up.features.publications.data.datasources.local.toEntity
import com.hugodev.red_up.features.publications.domain.entities.Publications
import com.hugodev.red_up.features.publications.domain.repositories.PublicationRepository
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class PublicationRepositoryImpl @Inject constructor(
    private val upRedApi: UpRedApi,
    private val publicationDao: PublicationDao
) : PublicationRepository {

    override suspend fun getPublications(): Result<List<Publications>> {
        return runCatching {
            val remotePublications = upRedApi.getPublications().map { it.toDomain() }
            publicationDao.replaceAll(remotePublications.map { it.toEntity() })
            remotePublications
        }.recoverCatching {
            val cached = publicationDao.getAll().map { it.toDomain() }
            if (cached.isNotEmpty()) cached else throw it
        }
    }

    override suspend fun createPublication(
        titulo: String,
        contenido: String,
        imageBytes: ByteArray?,
        tipoPublicacion: String
    ): Result<Publications> {
        return runCatching {
            val audience = if (tipoPublicacion.equals("GENERAL", true)) "general" else "carrera"
            val created = if (imageBytes != null) {
                val imageRequest = imageBytes.toRequestBody("image/jpeg".toMediaType())
                val imagePart = MultipartBody.Part.createFormData(
                    "files",
                    "publicacion_${System.currentTimeMillis()}.jpg",
                    imageRequest
                )

                upRedApi.createPublicationWithImage(
                    titulo = titulo.toRequestBody("text/plain".toMediaType()),
                    contenido = contenido.toRequestBody("text/plain".toMediaType()),
                    audiencia = audience.toRequestBody("text/plain".toMediaType()),
                    files = imagePart
                )
            } else {
                upRedApi.createPublication(
                    CreatePublicationRequestDto(
                        titulo = titulo,
                        contenido = contenido,
                        audiencia = audience
                    )
                )
            }.toDomain()

            publicationDao.upsert(created.toEntity())
            created
        }.recoverCatching { throwable ->
            if (throwable is HttpException && throwable.code() == 413) {
                throw IllegalStateException("La imagen es demasiado pesada. Intenta con una foto mas ligera.")
            }

            throw throwable
        }
    }

    override suspend fun deletePublication(id: Long): Result<Unit> {
        return runCatching {
            upRedApi.deletePublication(id)
            publicationDao.deleteById(id)
        }
    }

    override suspend fun editPublication(
        id: Long,
        titulo: String,
        contenido: String,
        tipoPublicacion: String
    ): Result<Publications> {
        return runCatching {
            val updated = upRedApi.editPublication(
                id = id,
                request = CreatePublicationRequestDto(
                    titulo = titulo,
                    contenido = contenido,
                    audiencia = if (tipoPublicacion.equals("GENERAL", true)) "general" else "carrera"
                )
            ).toDomain()

            publicationDao.upsert(updated.toEntity())
            updated
        }
    }
}
