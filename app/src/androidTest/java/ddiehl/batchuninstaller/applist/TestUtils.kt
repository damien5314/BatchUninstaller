package ddiehl.batchuninstaller.applist

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

inline fun <reified T> whenever(t: T): OngoingStubbing<T> = Mockito.`when`(t)

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

fun <E, T : Comparable<T>> List<E>.isSorted(f: (e: E) -> T)
        = (0 until size-1).none { f(get(it)) >= f(get(it+1)) }
