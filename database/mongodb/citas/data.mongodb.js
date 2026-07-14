db = db.getSiblingDB('clinica_citas_db');

db.citas.updateOne(
  { _id: ObjectId('64b000000000000000009001') },
  {
    $setOnInsert: {
      pacienteId: NumberLong(1),
      medicoId: NumberLong(10),
      especialidadId: NumberLong(2),
      fecha: '2026-07-15',
      horaInicio: '10:00:00',
      horaFin: '10:30:00',
      consultorio: 'Consultorio 201',
      motivo: 'Consulta general de demostracion',
      estado: 'PROGRAMADA',
      motivoCancelacion: null,
      createdAt: new Date(),
      updatedAt: new Date(),
      _class: 'com.clinica.citas.entities.Cita'
    }
  },
  { upsert: true }
);
