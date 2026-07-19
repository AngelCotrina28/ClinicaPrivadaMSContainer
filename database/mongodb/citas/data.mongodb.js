db = db.getSiblingDB('clinica_citas_db');

db.citas.updateOne(
  { _id: ObjectId('64b000000000000000009001') },
  {
    $set: {
      pacienteId: NumberLong('1'),
      medicoId: NumberLong('10'),
      especialidadId: NumberLong('2'),
      fecha: '2026-08-04',
      horaInicio: '10:00:00',
      horaFin: '10:30:00',
      consultorio: 'Consultorio 201',
      motivo: 'Consulta pediatrica de demostracion'
    },
    $setOnInsert: {
      estado: 'PROGRAMADA',
      motivoCancelacion: null,
      createdAt: new Date(),
      updatedAt: new Date(),
      _class: 'com.clinica.citas.entities.Cita'
    }
  },
  { upsert: true }
);

const citasDemo = [
  {
    _id: ObjectId('64b000000000000000009002'),
    pacienteId: NumberLong('1'), medicoId: NumberLong('11'), especialidadId: NumberLong('3'),
    fecha: '2026-08-05', horaInicio: '09:00:00', horaFin: '09:30:00',
    consultorio: 'Consultorio 102', motivo: 'Control de demostracion', estado: 'PROGRAMADA'
  },
  {
    _id: ObjectId('64b000000000000000009003'),
    pacienteId: NumberLong('2'), medicoId: NumberLong('11'), especialidadId: NumberLong('3'),
    fecha: '2026-08-05', horaInicio: '10:00:00', horaFin: '10:30:00',
    consultorio: 'Consultorio 102', motivo: 'Control cardiologico demo', estado: 'PROGRAMADA'
  },
  {
    _id: ObjectId('64b000000000000000009004'),
    pacienteId: NumberLong('3'), medicoId: NumberLong('12'), especialidadId: NumberLong('4'),
    fecha: '2026-08-06', horaInicio: '11:00:00', horaFin: '11:30:00',
    consultorio: 'Consultorio 203', motivo: 'Dolor lumbar demo', estado: 'ATENDIDA'
  },
  {
    _id: ObjectId('64b000000000000000009005'),
    pacienteId: NumberLong('4'), medicoId: NumberLong('13'), especialidadId: NumberLong('1'),
    fecha: '2026-08-07', horaInicio: '08:30:00', horaFin: '09:00:00',
    consultorio: 'Consultorio 101', motivo: 'Consulta de medicina general demo', estado: 'PROGRAMADA'
  },
  {
    _id: ObjectId('64b000000000000000009006'),
    pacienteId: NumberLong('2'), medicoId: NumberLong('10'), especialidadId: NumberLong('2'),
    fecha: '2026-08-08', horaInicio: '14:00:00', horaFin: '14:30:00',
    consultorio: 'Consultorio 201', motivo: 'Consulta pediatrica demo', estado: 'CANCELADA',
    motivoCancelacion: 'Reprogramacion solicitada por el paciente'
  },
  {
    _id: ObjectId('64b000000000000000009007'),
    pacienteId: NumberLong('1'), medicoId: NumberLong('12'), especialidadId: NumberLong('4'),
    fecha: '2026-08-09', horaInicio: '16:00:00', horaFin: '16:30:00',
    consultorio: 'Consultorio 203', motivo: 'Seguimiento demo', estado: 'PROGRAMADA'
  }
];

citasDemo.forEach((cita) => {
  const { _id, estado, motivoCancelacion, ...datosEstables } = cita;
  db.citas.updateOne(
    { _id },
    {
      $set: datosEstables,
      $setOnInsert: {
        estado,
        motivoCancelacion: motivoCancelacion || null,
        createdAt: new Date(),
        updatedAt: new Date(),
        _class: 'com.clinica.citas.entities.Cita'
      }
    },
    { upsert: true }
  );
});
