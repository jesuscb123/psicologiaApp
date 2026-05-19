package dam2.tfg.psicologiaapp.cita.data.mappers

import dam2.tfg.psicologiaapp.cita.data.local.CitaEntity
import dam2.tfg.psicologiaapp.cita.data.remote.CitaResponseDto
import dam2.tfg.psicologiaapp.cita.data.remote.DisponibilidadResponseDto
import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaPersistido
import java.time.LocalDate
import java.time.LocalTime

fun DisponibilidadResponseDto.toDomain(): DisponibilidadDia = DisponibilidadDia(
    fecha = runCatching { LocalDate.parse(fecha) }.getOrElse { LocalDate.now() },
    zonaHoraria = zonaHoraria,
    horasDisponibles = horasDisponibles.mapNotNull { texto ->
        runCatching {
            // Acepta "HH:mm" o "HH:mm:ss"
            if (texto.length == 5) LocalTime.parse(texto) else LocalTime.parse(texto.take(8))
        }.getOrNull()
    },
)

fun CitaResponseDto.toDomain(): Cita = Cita(
    id = id,
    inicio = inicio,
    fin = fin,
    psicologoId = psicologo.idEntidadPsicologo,
    pacienteId = paciente.idPaciente,
    nombrePsicologo = listOf(psicologo.nombre, psicologo.apellidos).filter { it.isNotBlank() }.joinToString(" "),
    nombrePaciente = listOf(paciente.nombre, paciente.apellidos).filter { it.isNotBlank() }.joinToString(" "),
    estadoPersistido = runCatching { EstadoCitaPersistido.valueOf(estadoPersistido) }
        .getOrElse { EstadoCitaPersistido.RESERVADA },
    estadoCalculado = runCatching { EstadoCitaCalculado.valueOf(estadoCalculado) }
        .getOrElse { EstadoCitaCalculado.ACTIVA },
)

fun CitaResponseDto.toEntity(esDePaciente: Boolean): CitaEntity = CitaEntity(
    id = id,
    inicio = inicio,
    fin = fin,
    psicologoId = psicologo.idEntidadPsicologo,
    pacienteId = paciente.idPaciente,
    nombrePsicologo = listOf(psicologo.nombre, psicologo.apellidos).filter { it.isNotBlank() }.joinToString(" "),
    nombrePaciente = listOf(paciente.nombre, paciente.apellidos).filter { it.isNotBlank() }.joinToString(" "),
    estadoPersistido = estadoPersistido,
    estadoCalculado = estadoCalculado,
    esDePaciente = esDePaciente,
)

fun CitaEntity.toDomain(): Cita = Cita(
    id = id,
    inicio = inicio,
    fin = fin,
    psicologoId = psicologoId,
    pacienteId = pacienteId,
    nombrePsicologo = nombrePsicologo,
    nombrePaciente = nombrePaciente,
    estadoPersistido = runCatching { EstadoCitaPersistido.valueOf(estadoPersistido) }
        .getOrElse { EstadoCitaPersistido.RESERVADA },
    estadoCalculado = runCatching { EstadoCitaCalculado.valueOf(estadoCalculado) }
        .getOrElse { EstadoCitaCalculado.ACTIVA },
)

