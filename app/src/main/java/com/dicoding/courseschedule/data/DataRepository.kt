package com.dicoding.courseschedule.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dicoding.courseschedule.util.QueryType
import com.dicoding.courseschedule.util.QueryUtil.nearestQuery
import com.dicoding.courseschedule.util.QueryUtil.sortedQuery
import com.dicoding.courseschedule.util.SortType
import com.dicoding.courseschedule.util.executeThread
import java.util.Calendar

//TODO 4 : Implement repository with appropriate dao
class DataRepository(private val dao: CourseDao) {

    fun getNearestSchedule(queryType: QueryType) : LiveData<Course?> {
        return dao.getNearestSchedule(nearestQuery(queryType))
    }

    fun getAllCourse(sortType: SortType): LiveData<PagedList<Course>> {
        val newData = dao.getAll(sortedQuery(sortType))
        val config = PagedList.Config.Builder().setPageSize(PAGE_SIZE)
            .build()
        return LivePagedListBuilder(newData, config).build()
    }

    fun getCourse(id: Int) : LiveData<Course> {
        return dao.getCourse(id)
    }

    fun getTodaySchedule() : List<Course> {
        val today = Calendar.getInstance()
        val todayOnWeek = today.get(Calendar.DAY_OF_WEEK) - 1

        return dao.getTodaySchedule(todayOnWeek)
    }

    fun insert(course: Course) = executeThread {
        return@executeThread dao.insert(course)
    }

    fun delete(course: Course) = executeThread {
        return@executeThread dao.delete(course)
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null
        private const val PAGE_SIZE = 10

        fun getInstance(context: Context): DataRepository? {
            return instance ?: synchronized(DataRepository::class.java) {
                if (instance == null) {
                    val database = CourseDatabase.getInstance(context)
                    instance = DataRepository(database.courseDao())
                }
                return instance
            }
        }
    }
}