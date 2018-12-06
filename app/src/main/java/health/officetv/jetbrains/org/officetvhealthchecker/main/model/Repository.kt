package health.officetv.jetbrains.org.officetvhealthchecker.main.model

interface Repository<K, V> {

    fun get(param: K): V
    fun set(param: K, value: V)
    fun getAll(): List<V>
    fun size() = getAll().size
    fun remove(param: K)
}