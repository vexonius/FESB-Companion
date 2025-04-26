package com.tstudioz.fax.fme.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tstudioz.fax.fme.common.user.models.UserRoom
import com.tstudioz.fax.fme.database.models.EventRoom
import com.tstudioz.fax.fme.database.models.NoteRoom
import com.tstudioz.fax.fme.feature.attendance.dao.AttendanceDao
import com.tstudioz.fax.fme.feature.attendance.models.AttendanceEntry
import com.tstudioz.fax.fme.feature.home.dao.NoteDao
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDao
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptRoom
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataRoom
import com.tstudioz.fax.fme.feature.login.dao.UserDao
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.timetable.dao.TimeTableDao

@Database(
    entities = [
        UserRoom::class,
        AttendanceEntry::class,
        NoteRoom::class,
        StudentDataRoom::class,
        ReceiptRoom::class,
        EventRoom::class,
        StudomatSubject::class,
        StudomatYearInfo::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun noteDao(): NoteDao
    abstract fun iksicaDao(): IksicaDao
    abstract fun timetableDao(): TimeTableDao
    abstract fun studomatDao(): StudomatDao
}