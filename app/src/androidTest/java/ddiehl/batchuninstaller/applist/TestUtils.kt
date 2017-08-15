package ddiehl.batchuninstaller.applist

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

inline fun <reified T> whenever(t: T): OngoingStubbing<T> = Mockito.`when`(t)

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
