package com.tstudioz.fax.fme.feature.iksica.models

data class IksicaModel(
    val balance: IksicaBalance?,
    val studentData: StudentDataRealm?,
    val receipts: List<Receipt>
)
