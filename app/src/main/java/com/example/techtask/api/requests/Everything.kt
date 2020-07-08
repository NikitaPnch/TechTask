package com.example.techtask.api.requests

import com.example.techtask.Constants
import com.example.techtask.api.model.APINews
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Everything {

    /** Поиск по новостям
     * @param q строка запроса
     * @param sortBy сортировка результатов
     * @param from поиск от конкретной даты
     * @param to поиск по конкретную даты
     * @param language на каком языке нужно найти новости, две буквы по стандарту ISO-639-1
     * @param pageSize кол-во результатов на страницу, мин = 20, макс = 100
     */
    @GET("v2/everything")
    fun searchEverything(
        @Query("page") page: Int = 1,
        @Query("q") query: String = Constants.QUERY,
        @Query("sortBy") sortBy: String? = Constants.PUBLISHED_AT,
        @Query("from") from: String? = Constants.FROM,
        @Query("pageSize") pageSize: Int? = Constants.PAGE_SIZE
    ): Single<APINews>
}