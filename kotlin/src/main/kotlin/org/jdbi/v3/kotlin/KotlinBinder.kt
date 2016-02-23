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

import org.jdbi.v3.PreparedBatchPart
import org.jdbi.v3.SQLStatement
import org.jdbi.v3.Types
import org.jdbi.v3.sqlobject.Bind
import org.jdbi.v3.sqlobject.Binder
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.declaredMemberProperties
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

class KotlinBinder(val method: Method, val paramIdx: Int) : Binder<Bind, Any> {
    override fun bind(q: SQLStatement<*>, parameter: Parameter, bind: Bind?, arg: Any?) {
        val paramType = parameter.getParameterizedType()

        val bindName = if (parameter.isNamePresent()) {
            parameter.getName()
        } else {
            method.kotlinFunction?.parameters?.dropWhile { it.kind != KParameter.Kind.VALUE }?.toList()?.get(paramIdx)?.name
        } ?: throw UnsupportedOperationException("A parameter was not given a name, "
                + "and parameter name data is not present in the class file, for: "
                + parameter.getDeclaringExecutable() + " :: " + parameter)

        fun bind(q: SQLStatement<*>, bindToParm: String?, bindAsType: Type, value: Any?, prefix: String = "") {
            val type = if (q is PreparedBatchPart) {
                // FIXME BatchHandler should extract the iterable/iterator element type and pass it to the binder
                val erasedType = Types.getErasedType(bindAsType)
                if (Iterable::class.java.isAssignableFrom(erasedType)) {
                    Types.findGenericParameter(bindAsType, Iterable::class.java).get()
                } else if (Iterator::class.java.isAssignableFrom(erasedType)) {
                    Types.findGenericParameter(bindAsType, Iterator::class.java).get()
                } else {
                    bindAsType
                }
            } else {
                bindAsType
            }

            val erasedType = Types.getErasedType(type)
            if (erasedType.isKotlinClass()) {
                @Suppress("UNCHECKED_CAST")
                (erasedType.kotlin as KClass<Any>).declaredMemberProperties.forEach { subProp ->
                    bind(q, subProp.name, subProp.returnType.javaType, if (value == null) null else subProp.get(value), "${prefix}${bindToParm}.")
                }
            } else {
                if (bindToParm != null) {
                    q.bindByType("${prefix}${bindToParm}", value, type)
                } else if (prefix.isNullOrBlank()) {
                    // we can't really bind sub items by order
                    q.bindByType(paramIdx, value, type)
                }
            }
        }

        bind(q, bindName, paramType, arg)
    }
}