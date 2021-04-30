package org.carrat.web.builder

import org.carrat.experimental.ExperimentalMultipleReceivers

public typealias CBlock = CBuilder.() -> Unit
//Would require higher order generics
//@CarratExperimentalMultipleReceivers
//public typealias CAnyTagBlock<B:<T>->in CAnyTagBuilder<T>, T> = B<T>.() -> Unit
@ExperimentalMultipleReceivers
public typealias CTagBlock<T> = CTagBuilder<T>.() -> Unit
@ExperimentalMultipleReceivers
public typealias CUnsafeTagBlock<T> = CUnsafeTagBuilder<T>.() -> Unit
