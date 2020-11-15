package com.github.watabee.qiitacompose.repository

import androidx.annotation.IntRange
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.await
import com.github.watabee.qiitacompose.api.response.Error
import com.github.watabee.qiitacompose.api.response.ErrorResponse
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.api.response.Rate
import com.github.watabee.qiitacompose.api.response.SuccessResponse
import com.github.watabee.qiitacompose.di.Api
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import java.io.IOException
import javax.inject.Inject
import kotlin.reflect.javaType
import kotlin.reflect.typeOf
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly

interface QiitaRepository {
    suspend fun findItems(
        @IntRange(from = 1, to = 100)
        page: Int,
        @IntRange(from = 1, to = 100)
        perPage: Int,
        query: String?
    ): QiitaApiResult<SuccessResponse<List<Item>>, ErrorResponse>
}

internal class QiitaRepositoryImpl
    @Inject
    constructor(@Api private val okHttpClient: OkHttpClient, private val moshi: Moshi) :
    QiitaRepository {

    override suspend fun findItems(
        @IntRange(from = 1, to = 100)
        page: Int,
        @IntRange(from = 1, to = 100)
        perPage: Int,
        query: String?
    ): QiitaApiResult<SuccessResponse<List<Item>>, ErrorResponse> {
        val httpUrl =
            HttpUrl.Builder()
                .scheme("https")
                .host("qiita.com")
                .addPathSegments("api/v2/items")
                .addQueryParameter("page", page.toString())
                .addQueryParameter("per_page", perPage.toString())
                .apply {
                    if (!query.isNullOrBlank()) {
                        addQueryParameter("query", query)
                    }
                }
                .build()

        return httpGet(httpUrl)
    }

    private suspend inline fun <reified T : Any> httpGet(
        httpUrl: HttpUrl
    ): QiitaApiResult<T, ErrorResponse> {
        val request = Request.Builder().url(httpUrl).get().build()

        return try {
            parseResponse(okHttpClient.newCall(request).await())
        } catch (e: IOException) {
            QiitaApiResult.Failure.NetworkFailure(e)
        } catch (e: Throwable) {
            QiitaApiResult.Failure.UnknownFailure(e)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private inline fun <reified T : Any> parseResponse(
        response: Response
    ): QiitaApiResult<T, ErrorResponse> {
        val source = response.body?.source()
        try {
            return if (response.isSuccessful) {
                if (T::class == SuccessResponse::class) {
                    val type = typeOf<T>().arguments[0].type?.javaType?.rawType
                    val rawResponse = moshi.adapter(type).fromJson(source)!!
                    val rate = Rate.parseHeaders(response.headers)
                    QiitaApiResult.Success(SuccessResponse(rate, rawResponse) as T)
                } else {
                    QiitaApiResult.Success(moshi.adapter(T::class.java).fromJson(source)!!)
                }
            } else {
                val error = moshi.adapter(Error::class.java).fromJson(source)!!
                val rate = Rate.parseHeaders(response.headers)
                QiitaApiResult.Failure.HttpFailure(response.code, ErrorResponse(rate, error))
            }
        } finally {
            source?.closeQuietly()
        }
    }
}
