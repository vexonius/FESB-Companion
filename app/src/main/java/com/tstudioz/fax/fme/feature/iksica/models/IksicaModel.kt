package com.tstudioz.fax.fme.feature.iksica.models

data class IksicaModel(
    val balance: IksicaBalance?,
    val studentData: StudentData?,
    val receipts: List<Receipt>
)
