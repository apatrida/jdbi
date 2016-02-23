/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.kotlin

import org.jdbi.v3.H2DatabaseRule
import org.jdbi.v3.sqlobject.SqlQuery
import org.jdbi.v3.sqlobject.SqlUpdate
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PluginTest {
    @Rule @JvmField
    val db = H2DatabaseRule().withPlugins()

    data class Thing(val id: Int, val name: String)

    internal interface ThingDao {
        @SqlUpdate("insert into something (id, name) values (:something.id, :something.name)")
        fun insert(something: Thing)

        @SqlQuery("select id, name from something")
        fun list(): List<Thing>

        @SqlQuery("select id, name from something where id=:id")
        fun findById(id: Int): Thing
    }

    @Test
    fun testPluginInstallsJpaMapper() {
        val brian = Thing(1, "Brian")
        val keith = Thing(2, "Keith")

        val dao: ThingDao = attachSqlObject(db.sharedHandle)
        dao.insert(brian)
        dao.insert(keith)

        val rs = dao.list()

        assertEquals(rs.size.toLong(), 2)
        assertEquals(rs[0], brian)
        assertEquals(rs[1], keith)
    }
}

