package org.carrat.build.compile

import org.carrat.build.KDocInfo
import org.carrat.build.model.DeclarationsBuildingContext

fun buildContext(docInfos: Map<String, KDocInfo>) : DeclarationsBuildingContext {
    return DeclarationsBuildingContext(docInfos)
}