package org.carrat.web.builder

import kotlinx.html.Tag

public sealed interface CUnsafeTagBuilder<T : Tag> : CAnyTagBuilder<T> {
}
