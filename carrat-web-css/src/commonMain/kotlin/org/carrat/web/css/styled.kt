package org.carrat.web.css

import org.carrat.web.builder.html.TagBlock
import org.carrat.web.builder.html.TagConsumer

public typealias StyledBlock<T, E> = TagBlock<T, E>

public typealias Styled<T, E> = TagConsumer<*>.(block: StyledBlock<T, E>) -> Unit
