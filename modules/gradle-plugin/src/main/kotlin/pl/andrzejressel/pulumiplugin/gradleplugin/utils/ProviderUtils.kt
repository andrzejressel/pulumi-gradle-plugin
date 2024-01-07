// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.gradleplugin.utils

import org.gradle.api.provider.Provider

fun <S, T : Any> Provider<S>.cast(): Provider<T> where S : T = this.map { it }
