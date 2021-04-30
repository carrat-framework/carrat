package org.carrat.web.builder

import kotlinx.html.TagConsumer

public typealias TagConstructor<T> = (initialAttributes : Map<String, String>, consumer : TagConsumer<*>) -> T
