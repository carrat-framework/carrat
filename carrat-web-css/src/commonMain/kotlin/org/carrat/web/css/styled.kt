package org.carrat.web.css

import org.carrat.web.builder.CBuilder
import org.carrat.web.builder.CTagBlock
import org.carrat.web.builder.CTagBuilder

public typealias StyledBlock<T> = CTagBlock<T>

public typealias Styled<T> = CBuilder.(block: StyledBlock<T>) -> Unit
