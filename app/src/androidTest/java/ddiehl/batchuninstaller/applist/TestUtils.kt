package ddiehl.batchuninstaller.applist

import ddiehl.batchuninstaller.model.appinfo.IPackageInfo
import ddiehl.batchuninstaller.model.appinfo.impl.APackageInfo
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

inline fun <reified T> whenever(t: T): OngoingStubbing<T> = Mockito.`when`(t)

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

fun getFakeListOfPackages(): List<IPackageInfo> = listOf(
        APackageInfo("foo")
)
