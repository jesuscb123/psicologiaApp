package dam2.tfg.psicologiaapp.resumenIa.data.mappers

import dam2.tfg.psicologiaapp.resumenIa.data.remote.ResumenIaResponseDto
import dam2.tfg.psicologiaapp.resumenIa.domain.model.ResumenIa

fun ResumenIaResponseDto.toDomain(): ResumenIa = ResumenIa(
    resumen = resumen,
    numeroNotasAnalizadas = numeroNotasAnalizadas,
    generadoEn = generadoEn,
    modelo = modelo,
)
