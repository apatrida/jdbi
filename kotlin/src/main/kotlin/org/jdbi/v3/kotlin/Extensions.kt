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

import org.jdbi.v3.Handle
import org.jdbi.v3.sqlobject.Bind
import org.jdbi.v3.sqlobject.FallbackBinderFactory
import org.jdbi.v3.sqlobject.SqlObjectBuilder
import kotlin.reflect.KClass

fun <T: Any> attachSqlObject(handle: Handle, sqlObjectType: KClass<T>, fallbackBinderFactory: FallbackBinderFactory<Bind> = KotlinBinderFactory()): T {
    return SqlObjectBuilder.attach(handle, sqlObjectType.java, fallbackBinderFactory)
}

inline fun <reified T: Any> attachSqlObject(handle: Handle, fallbackBinderFactory: FallbackBinderFactory<Bind> = KotlinBinderFactory()): T {
    return attachSqlObject(handle, T::class, fallbackBinderFactory)
}

private val metadataFqName = "kotlin.Metadata"

fun Class<*>.isKotlinClass(): Boolean {
    return this.annotations.singleOrNull { it.annotationClass.java.name == metadataFqName } != null
}
